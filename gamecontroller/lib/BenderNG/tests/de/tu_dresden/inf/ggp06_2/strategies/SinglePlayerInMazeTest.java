package de.tu_dresden.inf.ggp06_2.strategies;

import java.util.Timer;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.connection.Player.MatchTimerTask;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.Match;

public class SinglePlayerInMazeTest {

    private GameManager gameManager = new GameManager();
    private Match mazeMatch = null;

    @Before
    public void setUp(){
        ExpressionList chessRules = Parser.parseFile("./testdata/games/Maze.kif");
        Game           game    = gameManager.getGameByGDL( chessRules.toString() );
        AbstractStrategy singlePlayer = new SinglePlayerSearch(game, "robot");
        mazeMatch = new Match("0", singlePlayer, "robot");
        mazeMatch.strategy.game.getTheory();
    }

    @Test
    public void simulateMazeGame(){
        ExpressionList moves = new ExpressionList();
        Timer timer = new Timer();

        timer.schedule( new MatchTimerTask(mazeMatch), 9000 );
        mazeMatch.selectMove();
        timer.schedule( new MatchTimerTask(mazeMatch), 9000 );
        Expression move = mazeMatch.selectMove();
        moves.add( move );
        mazeMatch.makeTurn( moves );
        moves.clear();

        timer.schedule( new MatchTimerTask(mazeMatch), 9000 );
        move = mazeMatch.selectMove();
        moves.add( move );
        mazeMatch.makeTurn( moves );
        moves.clear();

        timer.schedule( new MatchTimerTask(mazeMatch), 9000 );
        move = mazeMatch.selectMove();
        moves.add( move );
        mazeMatch.makeTurn( moves );
        moves.clear();

        timer.schedule( new MatchTimerTask(mazeMatch), 9000 );
        move = mazeMatch.selectMove();
        moves.add( move );
        mazeMatch.makeTurn( moves );
        moves.clear();
    }
}
