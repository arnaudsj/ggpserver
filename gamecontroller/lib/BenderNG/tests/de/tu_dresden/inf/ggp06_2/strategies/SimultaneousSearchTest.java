package de.tu_dresden.inf.ggp06_2.strategies;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class SimultaneousSearchTest {
    private static final Logger logger = Logger.getLogger(SimultaneousSearchTest.class);


    AbstractStrategy  simultaneousSearch;
    GameNode gameNode;
    GameManager manager = new GameManager();
    Game game;

    //Test
    public void pickMoveSimTicSacToe(){
        ExpressionList gameRules = Parser.parseFile( "./testdata/games/SimultaneousTicTacToe.kif" );
        logger.info( ""+gameRules );
        game = manager.getGameByGDL( gameRules.toString() );
        simultaneousSearch  = new SimultaneousSearch( game, "white" , new TimerFlag());

        logger.info( "=== Simultaneous Tic-Tac-Toe ===" );
        Expression aMove = simultaneousSearch.pickMove(game.getInitialNode());

        assertNotNull ( aMove                              );
        assertTrue    ( aMove instanceof Predicate         );
        logger.info( aMove );
    }


}
