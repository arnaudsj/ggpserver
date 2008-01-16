package de.tu_dresden.inf.ggp06_2.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.Match;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;
import de.tu_dresden.inf.ggp06_2.strategies.AbstractStrategy;
import de.tu_dresden.inf.ggp06_2.strategies.RandomStrategy;

public class ChessMatchTest {
    
        private GameManager gameManager = new GameManager();
        private Match chessMatch = null;
        private Theory theory = null;
        
        private static final Atom aMove = new Atom("move");
        private static final Logger logger = Logger.getLogger( ChessMatchTest.class );
        private static final Atom aWhite = new Atom("white");
        private static final Atom a50 = new Atom("50");        
        private static final Predicate pGoalWhite50 = new Predicate(Const.aGoal, aWhite, a50);
        private static final Atom aBlack = new Atom("black");

        @Before
        public void setUp(){
            ExpressionList chessRules = Parser.parseFile("./testdata/games/Chess.kif");
            Game           game    = gameManager.getGameByGDL( chessRules.toString() );
            AbstractStrategy dummy = new RandomStrategy(game, "white");
            chessMatch = new Match("0", dummy, "0");
            theory = chessMatch.strategy.getGame().getTheory();
        }
        
        @Test
        public void turnMaking(){
            FuzzySubstitution emptySub = new FuzzySubstitution();
            ArrayList<Expression> guard = new ArrayList<Expression>();
            
            GameState initial = chessMatch.getState();
            logger.info( " before " );
            ExpressionList whiteMoves, blackMoves;
            try {
                whiteMoves = chessMatch.strategy.getGame().getLegalMoves( aWhite, initial, new TimerFlag() );
                blackMoves = chessMatch.strategy.getGame().getLegalMoves( aBlack, initial, new TimerFlag() );
            }
            catch ( InterruptedException e1 ) {
                logger.error("error during moves generation!");
                e1.printStackTrace();
                fail();
                return;
            }
            logger.info( "initial moves calculated" );
            logger.info( "white: "+whiteMoves );
            logger.info( "black: "+blackMoves);
            Atom atom = new Atom("CELL");
            ExpressionList cells = initial.get( atom );
            assertEquals(64, cells.size());
            
            GameStateScope gameStateScope = new GameStateScope(theory, initial);
            FuzzyResolution fuzzyGoal50Value = null;            
            try {            
               fuzzyGoal50Value = pGoalWhite50.fuzzyEvaluate( 
                        emptySub, gameStateScope, 
                        guard, new TimerFlag() );
               
            }
            catch ( InterruptedException e ) {
                logger.info( "interrupted!" );
                e.printStackTrace();
            }
            assertNotNull(fuzzyGoal50Value);
            assertTrue( 0 < fuzzyGoal50Value.getFuzzyValue());
            assertTrue( 1 > fuzzyGoal50Value.getFuzzyValue());
            assertTrue(guard.isEmpty());
            logger.info("HURRAAAYYY!");
            
            ExpressionList gameMasterMoves = new ExpressionList();
            makeWhiteTurn(chessMatch, gameMasterMoves, "WP", "E", "2", "E", "4");
            GameState intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            logger.info( " before " );
            try {
                whiteMoves = chessMatch.strategy.getGame().getLegalMoves( aWhite, intermediate, new TimerFlag() );
                blackMoves = chessMatch.strategy.getGame().getLegalMoves( aBlack, intermediate, new TimerFlag() );
            }
            catch ( InterruptedException e1 ) {
                logger.error("error during moves generation!");
                e1.printStackTrace();
                fail();
                return;
            }
            logger.info( "initial moves calculated" );
            logger.info( "white: "+whiteMoves );
            logger.info( "black: "+blackMoves);
            
            gameStateScope = new GameStateScope(theory, intermediate);
            fuzzyGoal50Value = null;            
            try {            
               fuzzyGoal50Value = pGoalWhite50.fuzzyEvaluate( 
                        emptySub, gameStateScope, 
                        guard, new TimerFlag() );
               
            }
            catch ( InterruptedException e ) {
                logger.info( "interrupted!" );
                e.printStackTrace();
            }
            assertNotNull(fuzzyGoal50Value);
            logger.info( "white turn evaluated to: "+fuzzyGoal50Value.getFuzzyValue() );
            assertTrue( 0 < fuzzyGoal50Value.getFuzzyValue());
            assertTrue( 1 > fuzzyGoal50Value.getFuzzyValue());
            assertTrue(guard.isEmpty());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BP", "C", "7", "C", "5");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            logger.info( " before " );
            try {            
                whiteMoves = chessMatch.strategy.getGame().getLegalMoves( aWhite, intermediate, new TimerFlag() );
                blackMoves = chessMatch.strategy.getGame().getLegalMoves( aBlack, intermediate, new TimerFlag() );
            }
            catch ( InterruptedException e1 ) {
                logger.error("error during moves generation!");
                e1.printStackTrace();
                fail();
                return;
            }
            logger.info( "initial moves calculated" );
            logger.info( "white: "+whiteMoves );
            logger.info( "black: "+blackMoves);
            
            gameStateScope = new GameStateScope(theory, intermediate);
            fuzzyGoal50Value = null;            
            try {            
               fuzzyGoal50Value = pGoalWhite50.fuzzyEvaluate( 
                        emptySub, gameStateScope, 
                        guard, new TimerFlag() );
               
            }
            catch ( InterruptedException e ) {
                logger.info( "interrupted!" );
                e.printStackTrace();
            }
            assertNotNull(fuzzyGoal50Value);
            logger.info( "black turn evaluated to: "+fuzzyGoal50Value.getFuzzyValue() );
            assertTrue( 0 < fuzzyGoal50Value.getFuzzyValue());
            assertTrue( 1 > fuzzyGoal50Value.getFuzzyValue());
            assertTrue(guard.isEmpty());
            
            /*
            makeWhiteTurn(chessMatch, gameMasterMoves, "WP", "C", "2", "C", "3");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BP", "D", "7", "D", "5");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeWhiteTurn(chessMatch, gameMasterMoves, "WP", "E", "4", "D", "5");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BQ", "D", "8", "D", "5");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeWhiteTurn(chessMatch, gameMasterMoves, "WN", "G", "1", "F", "3");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BP", "G", "7", "G", "6");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeWhiteTurn(chessMatch, gameMasterMoves, "WN", "B", "1", "A", "3");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BB", "F", "8", "G", "7");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeWhiteTurn(chessMatch, gameMasterMoves, "WP", "E", "2", "E", "4");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BP", "C", "7", "C", "5");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeWhiteTurn(chessMatch, gameMasterMoves, "WB", "F", "1", "C", "4");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BQ", "D", "5", "D", "8");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeWhiteTurn(chessMatch, gameMasterMoves, "WQ", "D", "1", "B", "3");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BP", "E", "7", "E", "6");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeWhiteTurn(chessMatch, gameMasterMoves, "WP", "D", "2", "D", "4");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BP", "C", "5", "D", "4");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeWhiteTurn(chessMatch, gameMasterMoves, "WB", "C", "1", "G", "5");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            
            makeBlackTurn(chessMatch, gameMasterMoves, "BN", "G", "8", "F", "6");
            intermediate = chessMatch.getState();
            cells = intermediate.get( atom );
            assertEquals(64, cells.size());
            */
        }
        
        private void makeWhiteTurn(Match chessMatch, ExpressionList moves,
                String wPiece, String wfVertical, String wfHorizontal, String wtVertical, String wtHorizontal) {
            ExpressionList whiteMoveArgs = new ExpressionList();
            whiteMoveArgs.add( new Atom(wPiece) );
            whiteMoveArgs.add( new Atom(wfVertical) );
            whiteMoveArgs.add( new Atom(wfHorizontal));
            whiteMoveArgs.add( new Atom(wtVertical));
            whiteMoveArgs.add( new Atom(wtHorizontal));        
            Expression whiteMove = new Predicate(aMove, whiteMoveArgs);
            
            moves.add( whiteMove );        
            moves.add( Const.aNoop );
            chessMatch.makeTurn( moves );
            moves.clear();
        }
            
       private void makeBlackTurn(Match chessMatch, ExpressionList moves,
               String bPiece, String bfVertical, String bfHorizontal, String btVertical, String btHorizontal){     
            ExpressionList blackMoveArgs = new ExpressionList();
            blackMoveArgs.add( new Atom(bPiece) );
            blackMoveArgs.add( new Atom(bfVertical) );
            blackMoveArgs.add( new Atom(bfHorizontal));
            blackMoveArgs.add( new Atom(btVertical));
            blackMoveArgs.add( new Atom(btHorizontal));
            Expression blackMove = new Predicate(aMove, blackMoveArgs);
                    
            moves.add( Const.aNoop );
            moves.add( blackMove );        
            chessMatch.makeTurn( moves );
            moves.clear();            
        }
        
        
}



