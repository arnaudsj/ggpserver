package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class FuzzyEvaluationTest {

    private static final Logger logger = Logger.getLogger( FuzzyEvaluationTest.class );

    private static final Atom a100 = new Atom("100");
    private static final Atom aXplayer = new Atom("xplayer");
    private static final Atom aCell = new Atom("cell");
    private static final Atom aX    = new Atom("x");
    private static final Atom aO    = new Atom("o");
    private static final Atom aB    = new Atom("b");
    private static final Atom a1    = new Atom("1");
    private static final Atom a2    = new Atom("2");
    private static final Atom a3    = new Atom("3");

    GameState state1, state2, state3, state4;
    GameStateScope gameState1Scope, gameState2Scope, gameState3Scope, gameState4Scope;
    Expression goal;

    public Predicate makeTrueCellFluent(Term x, Term y, Term value){
        ExpressionList cellState = new ExpressionList();
        cellState.add(x);
        cellState.add(y);
        cellState.add(value);
        return new TruePredicate( new Predicate(aCell, cellState));
    }

    @Before
    public void setUp(){

        ExpressionList expList = Parser.parseFile("./testdata/games/Tictactoe1.kif");
        Theory theory = new Theory( expList );

        ExpressionList state1Fluents = new ExpressionList();
        state1Fluents.add( makeTrueCellFluent( a1, a1, aX));
        state1Fluents.add( makeTrueCellFluent( a1, a2, aB));
        state1Fluents.add( makeTrueCellFluent( a1, a3, aB));
        state1Fluents.add( makeTrueCellFluent( a2, a1, aO));
        state1Fluents.add( makeTrueCellFluent( a2, a2, aB));
        state1Fluents.add( makeTrueCellFluent( a2, a3, aB));
        state1Fluents.add( makeTrueCellFluent( a3, a1, aB));
        state1Fluents.add( makeTrueCellFluent( a3, a2, aX));
        state1Fluents.add( makeTrueCellFluent( a3, a3, aB));
        state1 = new GameState(state1Fluents);
        gameState1Scope = new GameStateScope(theory, state1);

        ExpressionList state2Fluents = new ExpressionList();
        state2Fluents.add( makeTrueCellFluent( a1, a1, aX));
        state2Fluents.add( makeTrueCellFluent( a1, a2, aB));
        state2Fluents.add( makeTrueCellFluent( a1, a3, aX));
        state2Fluents.add( makeTrueCellFluent( a2, a1, aO));
        state2Fluents.add( makeTrueCellFluent( a2, a2, aB));
        state2Fluents.add( makeTrueCellFluent( a2, a3, aB));
        state2Fluents.add( makeTrueCellFluent( a3, a1, aB));
        state2Fluents.add( makeTrueCellFluent( a3, a2, aB));
        state2Fluents.add( makeTrueCellFluent( a3, a3, aB));
        state2 = new GameState(state2Fluents);
        gameState2Scope = new GameStateScope(theory, state2);

        ExpressionList state3Fluents = new ExpressionList();
        state3Fluents.add( makeTrueCellFluent( a1, a1, aX));
        state3Fluents.add( makeTrueCellFluent( a1, a2, aB));
        state3Fluents.add( makeTrueCellFluent( a1, a3, aB));
        state3Fluents.add( makeTrueCellFluent( a2, a1, aB));
        state3Fluents.add( makeTrueCellFluent( a2, a2, aB));
        state3Fluents.add( makeTrueCellFluent( a2, a3, aB));
        state3Fluents.add( makeTrueCellFluent( a3, a1, aB));
        state3Fluents.add( makeTrueCellFluent( a3, a2, aB));
        state3Fluents.add( makeTrueCellFluent( a3, a3, aB));
        state3 = new GameState(state3Fluents);
        gameState3Scope = new GameStateScope(theory, state3);

        ExpressionList state4Fluents = new ExpressionList();
        state4Fluents.add( makeTrueCellFluent( a1, a1, aB));
        state4Fluents.add( makeTrueCellFluent( a1, a2, aB));
        state4Fluents.add( makeTrueCellFluent( a1, a3, aB));
        state4Fluents.add( makeTrueCellFluent( a2, a1, aB));
        state4Fluents.add( makeTrueCellFluent( a2, a2, aX));
        state4Fluents.add( makeTrueCellFluent( a2, a3, aB));
        state4Fluents.add( makeTrueCellFluent( a3, a1, aB));
        state4Fluents.add( makeTrueCellFluent( a3, a2, aB));
        state4Fluents.add( makeTrueCellFluent( a3, a3, aB));
        state4 = new GameState(state4Fluents);
        gameState4Scope = new GameStateScope(theory, state4);

        goal = new Predicate(Const.aGoal, aXplayer, a100);
    }

    @Test
    public void fuzzyInitialMove(){

        FuzzyResolution fuzzyState3Value = null;
        logger.info("======== First case (distant to the goal) ===========");
        FuzzySubstitution emptySubstitution = new FuzzySubstitution();
        ArrayList<Expression> guard = new ArrayList<Expression>();
        try {
            fuzzyState3Value = goal.fuzzyEvaluate(
                    emptySubstitution, gameState3Scope,
                    guard, new TimerFlag() );
        }
        catch ( InterruptedException e ) {
            logger.error(" interrupted !");
        }
        assertNotNull(fuzzyState3Value);
        logger.info( "fuzzyState3Value: "+fuzzyState3Value.getFuzzyValue() );
        assertTrue(0 < fuzzyState3Value.getFuzzyValue() );
        assertTrue(1.0 > fuzzyState3Value.getFuzzyValue() );
        assertTrue(guard.isEmpty());


        logger.info("\n\n\n======== Second case (closer to the goal) ===========");
        FuzzyResolution fuzzyState4Value = null;
        try {
            fuzzyState4Value = goal.fuzzyEvaluate(
                    emptySubstitution, gameState4Scope,
                    guard, new TimerFlag() );
        }
        catch ( InterruptedException e ) {
            logger.error(" interrupted !");
        }
        assertNotNull(fuzzyState4Value);
        assertTrue( 0 < fuzzyState4Value.getFuzzyValue());
        assertTrue( 1.0 > fuzzyState4Value.getFuzzyValue() );
        assertTrue(guard.isEmpty());
        logger.info( " fuzzyState3Value: "+fuzzyState3Value.getFuzzyValue());
        logger.info( " fuzzyState4Value: "+fuzzyState4Value.getFuzzyValue());
        assertTrue( fuzzyState3Value.getFuzzyValue() < fuzzyState4Value.getFuzzyValue());

    }

    @Test
    public void fuzzyStateValues(){

        FuzzyResolution fuzzyState1Value = null;
        logger.info("======== First case (distant to the goal) ===========");
        FuzzySubstitution emptySubstitution = new FuzzySubstitution();
        ArrayList<Expression> guard = new ArrayList<Expression>();
        try {
            fuzzyState1Value = goal.fuzzyEvaluate(
                    emptySubstitution, gameState1Scope,
                    guard, new TimerFlag() );
        }
        catch ( InterruptedException e ) {
            logger.error(" interrupted !");
        }
        assertNotNull(fuzzyState1Value);
        assertTrue(0 < fuzzyState1Value.getFuzzyValue() );
        assertTrue(1.0 > fuzzyState1Value.getFuzzyValue() );
        assertTrue(guard.isEmpty());


        logger.info("\n\n\n======== Second case (closer to the goal) ===========");
        FuzzyResolution fuzzyState2Value = null;
        try {
            fuzzyState2Value = goal.fuzzyEvaluate(
                    emptySubstitution, gameState2Scope,
                    guard, new TimerFlag());
        }
        catch ( InterruptedException e ) {
            logger.error(" interrupted !");
        }
        assertNotNull(fuzzyState2Value);
        assertTrue( 0 < fuzzyState2Value.getFuzzyValue());
        assertTrue( 1.0 > fuzzyState2Value.getFuzzyValue() );
        logger.info( " fuzzyState1Value: "+fuzzyState1Value);
        logger.info( " fuzzyState2Value: "+fuzzyState2Value);
        assertTrue( fuzzyState1Value.getFuzzyValue() < fuzzyState2Value.getFuzzyValue());
        assertTrue(guard.isEmpty());

    }

    @Test
    public void fuzzyConjuncts(){
        Variable vX = new Variable("?x");
        Variable vM = new Variable("?m");
        FuzzyResolution fuzzyStateValue1 = null, fuzzyStateValue2 = null;
        ExpressionList row1 = new ExpressionList();
        row1.add( makeTrueCellFluent( vM, a1, vX ));
        row1.add( makeTrueCellFluent( vM, a2, vX ));
        row1.add( makeTrueCellFluent( vM, a3, vX ));
        FuzzySubstitution currentResolutionStage = new FuzzySubstitution();
        currentResolutionStage.addAssociation( vX, aX );

        ArrayList<Expression> guard = new ArrayList<Expression>();
        try {
            fuzzyStateValue1 = row1.fuzzyEvaluate(
                    currentResolutionStage, gameState1Scope,
                    guard, new TimerFlag());
        }
        catch ( InterruptedException e ) {
            logger.error( "interrupted!" );
            e.printStackTrace();
        }
        assertNotNull(fuzzyStateValue1);
        logger.info( "fuzzyStateValue1: "+fuzzyStateValue1 );
        assertTrue(0.0 < fuzzyStateValue1.getFuzzyValue());
        assertTrue(1.0 > fuzzyStateValue1.getFuzzyValue());
        assertTrue(guard.isEmpty());

        try {
            fuzzyStateValue2 = row1.fuzzyEvaluate(
                    currentResolutionStage, gameState2Scope,
                    guard, new TimerFlag());
        }
        catch ( InterruptedException e ) {
            logger.error( "interrupted!" );
        }
        assertNotNull(fuzzyStateValue2);
        logger.info( "fuzzyStateValue1: "+ fuzzyStateValue1.getFuzzyValue());
        logger.info( "fuzzyStateValue2: "+ fuzzyStateValue2.getFuzzyValue());
        assertTrue( 0.0 < fuzzyStateValue2.getFuzzyValue());
        assertTrue( 1.0 > fuzzyStateValue2.getFuzzyValue());
        assertTrue( fuzzyStateValue1.getFuzzyValue() < fuzzyStateValue2.getFuzzyValue() );
        assertTrue(guard.isEmpty());
    }
}
