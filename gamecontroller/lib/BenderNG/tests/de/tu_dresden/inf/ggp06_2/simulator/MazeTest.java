package de.tu_dresden.inf.ggp06_2.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;
import de.tu_dresden.inf.ggp06_2.resolver.helper.ResolutionHelper;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.MovesScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.TheoryScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class MazeTest {
    
    private static final Logger logger  = Logger.getLogger( MazeTest.class );
    
    private Theory theory;

    private TheoryScope theoryScope;

    private TimerFlag flag;
    
    private static final Variable vX      = new Variable( "?x" );
    
    @BeforeClass
    public static void putTitle(){
        logger.info( "\n=== MazeTest ===" );
    }
    
    @Before
    public void setUp(){
        ExpressionList expList = Parser.parseFile("./testdata/maze.kif");
        theory = new Theory( expList );
        theoryScope = new TheoryScope(theory);
        flag = new TimerFlag();
    }
    
    @Test
    public void mazeGame(){
        
        /**
         * First of all let's find out, who is the player.
         */
        ExpressionList players = null;
        try {
            players = ResolutionHelper.resolveAndApply( 
                    vX, 
                    new Predicate( Const.aRole, vX ), 
                    theoryScope,
                    flag);
        }
        catch ( InterruptedException e ) {
            logger.error( "interrupted! " );
        }
        assertNotNull( players );
        assertTrue( 0 != players.size() );        
        Expression player = players.get( 0 );
        /*So player is stored in 'player' variable.*/
        logger.info( "player: "+player );
        
        /* Initialize game */
        ExpressionList initialState = null;
        try {
            initialState = ResolutionHelper.resolveAndApply(
                    Const.pTrue, Const.pInit, theoryScope, flag);
        }
        catch ( InterruptedException e1 ) {
            logger.error( "interrupted!" );
        }
        
        assertNotNull(initialState);
        assertTrue(0 != initialState.size());
        GameState state0  = new GameState( initialState );
        GameStateScope scope0 = new GameStateScope(theory, state0);
        logger.info( "initial state: "+initialState );
        /* initial state is assigned*/
        
        /* Now get ready for making moves */
        ExpressionList moves = null;
        Expression answerTemplate = new Predicate( Const.aDoes, player, vX );
        Expression predicateToResolve = new Predicate( Const.aLegal, player, vX );
        try {
            /**
             * Here we look for legal moves.
             */
            moves = ResolutionHelper.resolveAndApply( 
                    answerTemplate, predicateToResolve, scope0, flag );
        }
        catch ( InterruptedException e ) {
            logger.error( "interrupted!" );
        }
        assertNotNull( moves );
        //logger.info( "moves: "+moves );
        assertTrue( 0 != moves.size() );
        
        ExpressionList playerMoves = new ExpressionList();
        playerMoves.add( moves.get( 0 ) );
        MovesScope scope1 = new MovesScope(theory, state0, playerMoves);
        
        ExpressionList nextState = null;
        try {
            /**
             * Here we calculate new step.
             */
            nextState = ResolutionHelper.resolveAndApply( 
                    Const.pTrue, Const.pNext,  scope1, flag);
        }
        catch ( InterruptedException e ) {
            logger.error( "interrupted" );
        }
        assertNotNull( nextState );
        assertTrue( 0 != nextState.size() );
        logger.info( "\nNext state calculated:" );
        for ( Expression aTrue : nextState ) {
            assertTrue( aTrue instanceof Predicate );
            Predicate currentFluent = (Predicate) aTrue;
            assertEquals( Const.aTrue, currentFluent.firstOperand() );
            logger.info( "new state: " + currentFluent );
        }

        /**
         * Here we apply new step.
         */
        GameState state2 = new GameState( nextState );
        GameStateScope scope2 = new GameStateScope(theory, state2);
        logger.info( "\nNext state applied! Searching for new moves." );
        try {
            /**
             * Here we look for legal moves.
             */
            moves = ResolutionHelper.resolveAndApply(
                    answerTemplate, predicateToResolve, scope2, flag );
        }
        catch ( InterruptedException e ) {
            logger.error( "interrupted!" );
        }
        assertNotNull( moves );
        logger.info( "moves: "+moves );
        assertEquals( 2, moves.size() );
    }
}
