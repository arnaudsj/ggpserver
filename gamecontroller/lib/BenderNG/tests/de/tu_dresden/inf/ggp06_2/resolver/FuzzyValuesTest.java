package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;


public class FuzzyValuesTest {

    private static final Logger logger = Logger.getLogger( FuzzyValuesTest.class );

    private static final Atom aCell = new Atom("cell");
    private static final Atom a1 = new Atom("1");
    private static final Atom a2 = new Atom("2");
    private static final Atom aX = new Atom("x");
    private static final Atom aB = new Atom("b");

    GameStateScope gameStateScope;
    GameStateScope gameStateScope2;
    Expression expression;
    TimerFlag timerFlag;
    FuzzySubstitution empty;
    Set<FuzzySubstitution> resolutions;
    Expression expression1;
    Expression expression2;

    @Before
    public void setUp(){
        expression = new Predicate(aCell, a1, aX);

        expression1 = new Predicate(aCell, a1, new Variable("?x"));
        expression2 = new Predicate(aCell, a2, new Variable("?x"));

        ExpressionList expList = new ExpressionList();
        expList.add( new Predicate(aCell, a1, aB) );
        Theory theory = new Theory( expList );
        gameStateScope = new GameStateScope(theory, new GameState(expList));

        ExpressionList expList2 = new ExpressionList();
        expList2.add(new Predicate(aCell, a1, aX));
        Theory theory2 = new Theory(expList2);
        gameStateScope2 = new GameStateScope(theory2, new GameState(expList2));

        timerFlag = new TimerFlag();
        empty = new FuzzySubstitution();
        resolutions = new HashSet<FuzzySubstitution>();
    }

    @Test
    public void almostSame(){
        FuzzyResolution fuzzyValue = null;

        try {
            fuzzyValue = expression.fuzzyEvaluate(
                    empty, gameStateScope, new ArrayList<Expression>(), timerFlag );
        }
        catch ( InterruptedException e ) {
            logger.error(" interrupted !");
        }
        assertNotNull(fuzzyValue);
        assertEquals(1, fuzzyValue.size());
        assertTrue(-1 != fuzzyValue.getFuzzyValue() );
        assertTrue(Expression.fuzzyZero <= fuzzyValue.getFuzzyValue());

        logger.info(" fuzzyValue: "+fuzzyValue);
    }

    @Test
    public void exaclySame(){
        FuzzyResolution fuzzyValue = null;

        try {
            fuzzyValue = expression.fuzzyEvaluate(
                    empty, gameStateScope2, new ArrayList<Expression>(), timerFlag );
        }
        catch ( InterruptedException e ) {
            logger.error(" interrupted !");
        }
        assertNotNull(fuzzyValue);
        assertEquals( 1, fuzzyValue.size() );
        assertTrue(-1 != fuzzyValue.getFuzzyValue() );
        assertEquals(Expression.fuzzyOne, fuzzyValue.getFuzzyValue());

        logger.info(" fuzzyValue: "+fuzzyValue);
    }

    @Test
    public void resolvable(){
        FuzzyResolution fuzzyValue = null;

        try {
            fuzzyValue = expression1.fuzzyEvaluate(
                    empty, gameStateScope2, new ArrayList<Expression>(), timerFlag );
        }
        catch ( InterruptedException e ) {
            logger.error(" interrupted !");
        }
        assertNotNull(fuzzyValue);
        assertEquals( 1, fuzzyValue.size() );
        assertTrue(-1 != fuzzyValue.getFuzzyValue() );
        assertEquals(Expression.fuzzyOne, fuzzyValue.getFuzzyValue());

        logger.info(" fuzzyValue: "+fuzzyValue);
    }

    @Test
    public void unresolvable(){
        FuzzyResolution fuzzyValue = null;

        try {
            fuzzyValue = expression2.fuzzyEvaluate(
                    empty, gameStateScope2, new ArrayList<Expression>(), timerFlag );
        }
        catch ( InterruptedException e ) {
            logger.error(" interrupted !");
        }
        assertNotNull(fuzzyValue);
        assertEquals( 1, fuzzyValue.size() );
        assertTrue(-1 != fuzzyValue.getFuzzyValue() );
        logger.info(" fuzzyValue: "+fuzzyValue+" fuzzyZero: "+Expression.fuzzyZero);
        assertTrue(Expression.fuzzyZero >= fuzzyValue.getFuzzyValue());


    }

    @Test
    public void list(){
        List<Integer> aList = new ArrayList<Integer>();
        aList.add( 2 );
        aList.add( 3 );

        Integer a = aList.get( 0 );
        logger.info( "initail value: "+a );
        assertEquals( 2, a );
        aList.remove( a );
        aList.add( 5 );
        a = aList.get( 0 );
        logger.info( "second value: "+a );
        assertEquals( 3, a );

        logger.info( aList );
    }

}
