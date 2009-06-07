package de.tu_dresden.inf.ggp06_2.connection;

import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.Match;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;
import de.tu_dresden.inf.ggp06_2.strategies.AbstractStrategy;
import de.tu_dresden.inf.ggp06_2.strategies.SimultaneousFuzzySearch;
import de.tu_dresden.inf.ggp06_2.strategies.SinglePlayerFuzzySearch;
import de.tu_dresden.inf.ggp06_2.strategies.SinglePlayerSearch;
import de.tu_dresden.inf.ggp06_2.strategies.TurnTakingFuzzySearch;
import de.tu_dresden.inf.ggp06_2.strategies.mixins.LegalInBodyMixin;
import de.tu_dresden.inf.ggp06_2.strategies.mixins.StrategyMixin;
import de.tu_dresden.inf.ggp06_2.strategies.mixins.StubMixin;

public class Player {

    /**
     * The MatchTimer class stops the search for legal moves after the specified
     * time.
     */
    public static class MatchTimerTask extends TimerTask {

        Match   match;
        public MatchTimerTask( Match matchToTime ) {
            match = matchToTime;
            match.getTimerFlag().reset();
            logger.info( "Create a new RunnableMatch Timer." );
        }

        @Override
        public void run() {
            match.getTimerFlag().interrupt();
            logger.info( "RunnableMatch gets interrupted." );
        }
    }

    public static class FlagTask extends TimerTask {
        TimerFlag flag;
        public FlagTask(TimerFlag flatToSet){
            this.flag = flatToSet;
            this.flag.reset();
        }

        @Override
        public void run(){
            flag.interrupt();
        }
    }

    /* Stores the logger for this class */
    public final static Logger logger = Logger.getLogger(Player.class);

    /* GameSimulator of the player */
    Match            realMatch;
    MatchTimerTask   matchTimerTask;

    int              playClock;
    int              startClock;
    Timer            timer;
    GameManager      gameManager = new GameManager();

    final static String STR_ERROR1 = "Play message for the wrong match ";

    /**
     * This method is called when a new match begins.<br>
     * <br>
     * msg="(START &lt;MATCH ID&gt; &lt;ROLE&gt; &lt;GAME DESCRIPTION&gt; &lt;STARTCLOCK&gt;
     *      &lt;PLAYCLOCK&gt;)"<br>
     * e.g. msg="(START tictactoe1 white ((role white) (role black) ...) 1800
     *          120)" means:
     * <ul>
     *   <li>the current match is called "tictactoe1"</li>
     *   <li>your role is "white",</li>
     *   <li>
     *     after at most 1800 seconds, you have to return from the
     *     commandStart method
     *   </li>
     *   <li>for each move you have 120 seconds</li>
     * </ul>
     *
     * TODO:
     *    - use the time to "contemplate" about the game description
     *      and return on time (before STARTCLOCK is over!)
     */
    public void commandStart(Message msg){

        // create the clock
        playClock  = (msg.getPlayClock()  - 5) * 1000;
        startClock = (msg.getStartClock() - 5) * 1000;

        logger.info( "Start Clock " + (startClock / 1000) );
        logger.info( "Play  Clock " + (playClock  / 1000) );

        Timer startClockTimer = new Timer();
        TimerFlag   timerFlag = new TimerFlag();
        startClockTimer.schedule( new FlagTask(timerFlag), startClock );

        // get the game from the database
        Game runningGame = gameManager.getGameByGDL( msg.getGameDescription().toString() );
        logger.info("Bender created the game.");


        AbstractStrategy strategy;

        /**
         * brutal and annoying strategy switch
         */
        // single player strategy
        if ( runningGame.isSinglePlayer() ) {
            TimerFlag flag = new TimerFlag();
            new Timer().schedule( new FlagTask(flag), startClock/2 );
            SinglePlayerSearch crispStrategy = new SinglePlayerSearch( runningGame, msg.getRole(), flag );
            crispStrategy.pickMove( runningGame.getInitialNode() );
            if (crispStrategy.isSomeGoalReached()){
                strategy = crispStrategy;
            } else {
                StrategyMixin mixin = new StubMixin();
                if (Parser.isLegalInsideBody()){
                    mixin = new LegalInBodyMixin();
                }
                strategy = new SinglePlayerFuzzySearch(runningGame, msg.role, mixin);
            }

        // multiplayer strategy
        } else

            // turn taking game
            if ( runningGame.isTurnTaking() ) {

                // create the strategy
                StrategyMixin mixin = new StubMixin();
                if (Parser.isLegalInsideBody()){
                    mixin = new LegalInBodyMixin();
                }
                strategy = new TurnTakingFuzzySearch( runningGame, msg.getRole(), mixin );

            // simultanous game
            } else {
                StrategyMixin mixin = new StubMixin();
                if (Parser.isLegalInsideBody()){
                    mixin = new LegalInBodyMixin();
                }
                strategy = new SimultaneousFuzzySearch( runningGame, msg.getRole(), mixin );
            }
        logger.info( "Bender created the strategy "      +
                     strategy.getClass().getSimpleName() +
                     "." );

        // create a match
        realMatch = new Match ( msg.getMatchId(), strategy, msg.getRole() );
        realMatch.setTimerFlag( timerFlag );
        logger.info( "Bender created the match." );

//        new Timer().schedule( new MatchTimerTask(realMatch), startClock );
        logger.info( "Bender starts contemplate while doing yoga." );
        // here search should be started
        realMatch.selectMove();

        logger.info( "Bender is prepared to start the game." );
    }

    /**
     * This method is called once for each move<br>
     * <br>
     * msg = "(PLAY <MATCH ID> <JOINT MOVE>)<br>
     * <JOINT MOVE> will be NIL for the first PLAY message and the list of the
     * moves of all the players in the previous state<br>
     * e.g. msg="(PLAY tictactoe1 NIL)" for the first PLAY message
     *   or msg="(PLAY tictactoe1 ((MARK 1 2) NOOP))" if white marked cell (1,2)
     *   and black did a "noop".<br>
     * <br>
     * TODO:<br>
     * <ul>
     *   <li>
     *     calculate the new state from the old one and the <JOINT MOVE>
     *   </li>
     *   <li>
     *     use the time to find the best of your possible moves in the current
     *     state
     *   </li>
     *   <li>
     *     return your move (instead of "NIL") on time (before PLAYCLOCK is
     *     over!)
     *   </li>
     * </ul>
     * @return the move of this player
     */
    public String commandPlay(Message msg){

        // calls interrupted after playClock time is over (-1 to be on the save
        // side
        new Timer().schedule( new MatchTimerTask(realMatch), playClock );
        logger.info( "Bender started its playClock timer." );

        checkMatchId(msg);

        // only make a turn if the move list is not empty
        // is it empty it means we hit the first play message, setting the
        // initial state is done while constructing the match object
        if ( msg.getMoves() != null ){
            logger.info( "Moves from GameMaster: " + msg.getMoves() );
            realMatch.makeTurn( msg.getMoves() );
        }

        // real work is done here
        String move = realMatch.getMoveString();

        // logging the resulting move
        logger.info( "Our move: " + move );
        return move;
    }


    /**
     * This method is called if the match is over
     *
     * msg="(STOP <MATCH ID> <JOINT MOVE>)
     *
     * TODO:
     * <ul>
     *   <li>clean up the GamePlayer for the next match</li>
     *   <li>
     *     be happy if you have won, think about what went wrong if you have
     *     lost ;-)
     *   </li>
     * </ul>
     */
    public void commandStop(Message msg){

        checkMatchId(msg);

        // adds the moves to the match
        if ( msg.getMoves() != null )
            realMatch.makeTurn( msg.getMoves() );

        // check if we agree to be in a final state and log the result
        if ( !realMatch.isFinished() )
            logger.fatal( "Game stopped but not finished" );
        else
            logger.info( "Game finished" );

        // match information is stored
        realMatch.saveInformationToDB();

    }


    /**
     * This method is for game player spectators.
     * @return The current running match.
     */
    public Match getMatch() {
        return this.realMatch;
    }


    /**
     * This method is for error checking nothing more.
     * @param msg
     */
    public void checkMatchId( Message msg ) {

        if ( !msg.getMatchId().equals( realMatch.getMatchId() ) )
            logger.error( STR_ERROR1 + msg.getMatchId() );

    }

}
