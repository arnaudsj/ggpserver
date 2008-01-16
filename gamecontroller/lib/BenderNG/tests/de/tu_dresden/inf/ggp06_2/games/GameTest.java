package de.tu_dresden.inf.ggp06_2.games;

import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
/*
 * So far, this file only tests handling of synchronous and
 * singlePlayer descriptors of a game
 * TODO: test other features of the Game class
 * */
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;
import java.util.ArrayList;
import java.util.List;


public class GameTest {

    private static final Logger logger  = Logger.getLogger( GameTest.class );
    private ExpressionList  expressionList1;
    private ExpressionList  expressionList2;
    private ExpressionList  expressionList3;


    private Game        game;
    private TimerFlag   timerFlag;

    private static GameManager manager;
    private List<ExpressionList> expListList;
    private List<ExpressionList> expListList2 = new ArrayList<ExpressionList>();

    @BeforeClass
    public static void putTitle(){
        logger.info( "\n=== GameTest ===" );
        manager = new GameManager();
    }

    @Before
    public void setUp() {

        expressionList1 = Parser.parseFile( "testdata/games/Othello.kif" );

        game = manager.getGameByGDL( expressionList1.toString() );
        timerFlag = new TimerFlag();

        /*
         * For the game of Othello WHITE player has 4 legal moves,
         * while BLACK has only NOOP, combined there should be 4 possibilities
         */
        try {
            expressionList2 = game.getLegalMoves( new Atom("WHITE"),
                    game.getInitialNode().getState(), timerFlag );
            expressionList3 = game.getLegalMoves( new Atom("BLACK"),
                    game.getInitialNode().getState(), timerFlag );

            expListList = game.
            getCombinedLegalMoves( game.getInitialNode().getState(), timerFlag );
        }
        catch ( InterruptedException e1 ) {
            logger.error( "error during initialization!" );
            e1.printStackTrace();
        }

        //Computing the combined legal moves 'by hand'
        for (int i = 0; i < expressionList2.size(); i++){
            for (int j = 0; j < expressionList3.size(); j++){
                ExpressionList e = new ExpressionList(expressionList2.get( i ));
                e.add( expressionList3.get( j ) );
                expListList2.add( e );
            }
        }

    }

    // Test if getCombinedLegalMoves() returns the same combined moves for
    // Othello as computed 'by hand' above
    @Test
    public void combinedMovesTest(){
        logger.info( ""+expListList );
        assertTrue(expListList.equals( expListList2));
    }

}
