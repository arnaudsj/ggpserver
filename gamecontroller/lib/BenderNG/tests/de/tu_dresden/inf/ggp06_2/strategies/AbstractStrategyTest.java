package de.tu_dresden.inf.ggp06_2.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Map;
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
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class AbstractStrategyTest {

    private static final Logger logger = Logger.getLogger(AbstractStrategyTest.class);

    private static final Atom role = new Atom("a");
    private static final Atom opponent = new Atom("b");
    private static final Atom a1 = new Atom("1");
    private static final Atom a4 = new Atom("4");
    private static final Atom pick = new Atom("pick");
    private static final Atom pickedby = new Atom("pickedby");
    private static final Atom control = new Atom("control");

    final Predicate pDoesAPick1 = new Predicate(
            Const.aDoes, role, new Predicate(pick, a1));
    final Predicate pDoesAPick2 = new Predicate(
            Const.aDoes, role, new Predicate(pick, new Atom("2")));
    final Predicate pDoesAPick3 = new Predicate(
            Const.aDoes, role, new Predicate(pick, new Atom("3")));
    final Predicate pDoesAPick4 = new Predicate(
            Const.aDoes, role, new Predicate(pick, a4));
    final Predicate pDoesAPick5 = new Predicate(
            Const.aDoes, role, new Predicate(pick, new Atom("5")));
    final Predicate pDoesAPick6 = new Predicate(
            Const.aDoes, role, new Predicate(pick, new Atom("6")));
    final Predicate pDoesAPick7 = new Predicate(
            Const.aDoes, role, new Predicate(pick, new Atom("7")));
    final Predicate pDoesAPick8 = new Predicate(
            Const.aDoes, role, new Predicate(pick, new Atom("8")));
    final Predicate pDoesAPick9 = new Predicate(
            Const.aDoes, role, new Predicate(pick, new Atom("9")));
    final Predicate pDoesAPick10 = new Predicate(
            Const.aDoes, role, new Predicate(pick, new Atom("10")));
    final Predicate pDoesBNoop = new Predicate(
            Const.aDoes, opponent, new Atom("noop"));

    final Predicate pPickedByA1 = new Predicate(
            Const.aTrue, new Predicate(pickedby, role, a1));
    final Predicate pPickedByA4 = new Predicate(
            Const.aTrue, new Predicate(pickedby, role, a4));

    final Predicate pControlB = new Predicate(
            Const.aTrue, new Predicate(control, opponent));

    AbstractStrategy strategy;
    GameNode    gameNode;
    GameManager manager = new GameManager();

    @Before
    public void setUp(){
        ExpressionList gameRules = Parser.parseFile( "./testdata/mobility.kif" );
        Game game = manager.getGameByGDL( gameRules.toString() );
        gameNode = game.getInitialNode();
        strategy = new Mobility(game, role.toString());
    }

    @Test
    public void otherRoles() {
        Atom[] otherRoles = strategy.otherRoles;

        assertNotNull(otherRoles);
        logger.info( otherRoles );
        assertEquals(1, otherRoles.length);
        assertEquals(opponent, otherRoles[0]);
    }

    @Test
    public void legalMoves(){
        ExpressionList legalMoves = null;
        try {
            legalMoves = strategy.game.getLegalMoves(
                    role, gameNode.getState(), new TimerFlag() );
        }
        catch ( InterruptedException e ) {
            fail();
        }

        assertNotNull(legalMoves);
        assertEquals(10, legalMoves.size());
        for(Expression aMove : legalMoves){
            assertTrue(
                    pDoesAPick1.equals( aMove ) ||
                    pDoesAPick2.equals( aMove ) ||
                    pDoesAPick3.equals( aMove ) ||
                    pDoesAPick4.equals( aMove ) ||
                    pDoesAPick5.equals( aMove ) ||
                    pDoesAPick6.equals( aMove ) ||
                    pDoesAPick7.equals( aMove ) ||
                    pDoesAPick8.equals( aMove ) ||
                    pDoesAPick9.equals( aMove ) ||
                    pDoesAPick10.equals( aMove ));
        }

        ExpressionList opponentLegalMoves = null;
        try {
            opponentLegalMoves = strategy.game.getLegalMoves(
                    opponent, gameNode.getState(), new TimerFlag());
        }
        catch ( InterruptedException e ) {
            fail();
        }

        assertNotNull( opponentLegalMoves );
        assertFalse(opponentLegalMoves.isEmpty());
        assertEquals( 1, opponentLegalMoves.size() );
        assertEquals( pDoesBNoop, opponentLegalMoves.get( 0 ) );

    }

    @Test
    public void nextStateOddMove(){
        ExpressionList moves = new ExpressionList();
        moves.add( pDoesAPick1 );
        moves.add( pDoesBNoop);

        GameNode nextGameNode = null;
        try {
            nextGameNode = strategy.game.produceNextNode(
                    gameNode, moves, new TimerFlag());
        }
        catch ( InterruptedException e ) {
            fail();
        }

        assertNotNull(nextGameNode.getState());
        Map<Atom, ExpressionList> fluents = nextGameNode.getState();
        assertNotNull(fluents);
        assertFalse(fluents.isEmpty());
        assertEquals( 2, fluents.entrySet().size() );

        assertTrue( fluents.containsKey( pickedby ));
        ExpressionList picked = fluents.get( pickedby );

        assertNotNull(picked);
        assertFalse(picked.isEmpty());
        assertEquals( 1, picked.size() );
        assertEquals( pPickedByA1, picked.get( 0 ));

        assertTrue( fluents.containsKey( control ));
        ExpressionList controls = fluents.get( control );

        assertNotNull(controls);
        assertFalse(controls.isEmpty());
        assertEquals( 1, controls.size() );
        assertEquals( pControlB,  controls.get( 0 ));

        if (logger.isTraceEnabled()){
            logger.trace( "next state after odd move: "+nextGameNode.getState() );
        }
    }

    @Test
    public void nextStateEvenMove(){

        ExpressionList moves = new ExpressionList();
        moves.add( pDoesAPick4 );
        moves.add( pDoesBNoop);

        GameNode nextGameNode = null;
        try {
            nextGameNode = strategy.game.produceNextNode(
                    gameNode, moves, new TimerFlag());
        }
        catch ( InterruptedException e ) {
            fail();
        }

        assertNotNull(nextGameNode.getState());
        Map<Atom, ExpressionList> fluents = nextGameNode.getState();
        assertNotNull(fluents);
        assertFalse(fluents.isEmpty());
        assertEquals( 2, fluents.entrySet().size() );

        assertTrue( fluents.containsKey( pickedby ));
        ExpressionList picked = fluents.get( pickedby );

        assertNotNull(picked);
        assertFalse(picked.isEmpty());
        assertEquals( 1, picked.size() );
        assertEquals( pPickedByA4, picked.get( 0 ));

        assertTrue( fluents.containsKey( control ));
        ExpressionList controls = fluents.get( control );

        assertNotNull(controls);
        assertFalse(controls.isEmpty());
        assertEquals( 1, controls.size() );
        assertEquals( pControlB,  controls.get( 0 ));

        if (logger.isTraceEnabled()){
            logger.trace( "next state after even move: "+nextGameNode.getState() );
        }
    }

    @Test
    public void testMobilityKif() {
        ExpressionList fluents = new ExpressionList();
        fluents.add( pPickedByA1 );
        fluents.add( pControlB );
        GameState afterOneTurn = new GameState(fluents);

        ExpressionList legalMoves = null;
        try {
            legalMoves = strategy.game.getLegalMoves(
                    opponent, afterOneTurn, new TimerFlag() );
        }
        catch ( InterruptedException e ) {
            fail();
        }

        assertNotNull(legalMoves);
        assertFalse(legalMoves.isEmpty());
        if (logger.isTraceEnabled()){
            logger.trace( "opponent moves: "+legalMoves );
        }
        assertEquals( 5, legalMoves.size() );

    }

}
