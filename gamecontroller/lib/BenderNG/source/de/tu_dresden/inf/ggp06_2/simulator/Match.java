package de.tu_dresden.inf.ggp06_2.simulator;

import java.util.List;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.MatchManager;
import de.tu_dresden.inf.ggp06_2.gamedb.objects.MatchInformation;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Connective;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.DoesPredicate;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;
import de.tu_dresden.inf.ggp06_2.strategies.AbstractStrategy;

public final class Match {

    /* Stores the logger for this class */
    public static final Logger logger = Logger.getLogger(Match.class);

    /* Stores the persistence manager for the information object */
    public static final MatchManager manager = new MatchManager();

    public final AbstractStrategy strategy;
    public final MatchInformation info;

    /**
     * Timer handling
     */
    TimerFlag timerFlag   = new TimerFlag();
    GameNode  currentNode;

    /**
     * Constructor creates a new match based on a match identifier and a game
     * identifier.
     * @param id
     * @param gameId
     */
    public Match(String matchId, AbstractStrategy gameStrategy, String role) {

        strategy     = gameStrategy;
        strategy.setTimerFlag(timerFlag);
        info         = manager.createInformation( matchId, role );
        currentNode  = strategy.getGame().getInitialNode();

    }

    /**
     * This method returns the match id.
     * @return Returns a string containing the match id.
     */
    public final String getMatchId() {
        return info.getMatchId();
    }

    /**
     * This method returns a flag that is setted by a timer,
     * once a game step is about to end.
     * @return
     */
    public final TimerFlag getTimerFlag(){
        return timerFlag;
    }

    /**
     * This method returns the current state of the match.
     * @return
     */
    public final GameState getState() {
        return currentNode.getState();
    }

    /**
     * This method returns the current state of the match.
     * @return
     */
    public final GameNode getNode() {
        return currentNode;
    }

    /**
     * This method returns wether the game reached the final state or not.
     * @return whether the current state is terminal
     */
    public final boolean isFinished() {
        return info.isFinished();
    }

    /**
     * This method normalizes the expression list for mapping role -> action and
     * generates the next real game state.
     *
     * This method assumes that gameMasterMoves are not empty otherwise we get
     * an corrupted game state, meaning an game state that lost fluents.
     * @param gameMasterMoves
     */
    public void makeTurn(ExpressionList gameMasterMoves) {
        if ( timerFlag.interrupted() )
            return;

        // normalize the move list
        ExpressionList moves = new ExpressionList();
        int i = 0;
        List<Atom> roles = strategy.getGame().getRoleNames();
        for (Expression anExpression: gameMasterMoves) {
            moves.add( Const.aDoes.equals( anExpression.getKeyAtom() ) ?
                            anExpression :
                            new DoesPredicate( roles.get(i), anExpression ) );
            i++;
        }

        logger.info( "Moves applied to our state " + moves );

        // using the normalized move list to generate the next state
        GameNode tmpNode = null;

        try {
            tmpNode = strategy.getGame().produceNextNode( currentNode, moves, timerFlag );
            logger.info( "Returning evaluated child!" + tmpNode.getState() );
        }
        catch ( InterruptedException e ) {
            return;
        }

        currentNode = tmpNode;
        // mark state as a played state of the current match
        currentNode.getState().setPlayed();

    }

    /**
     *
     * @return
     */
    public Expression selectMove() {
        return strategy.pickMove( getNode() );
    }

    public String getMoveString() {
        return ((Connective)selectMove()).getOperands().get(1).toString();
    }

    /**
     * This method makes the information of this match persistent.
     */
    public void saveInformationToDB() {
        MatchManager.saveOrUpdate( info );
    }

    public void setTimerFlag(TimerFlag timerFlag) {
        this.timerFlag = timerFlag;
        strategy.setTimerFlag( timerFlag );
    }

}
