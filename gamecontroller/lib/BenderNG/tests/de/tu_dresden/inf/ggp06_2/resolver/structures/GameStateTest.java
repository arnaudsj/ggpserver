package de.tu_dresden.inf.ggp06_2.resolver.structures;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;

public class GameStateTest {
    
    private static final Logger logger = Logger.getLogger( GameStateTest.class );

    // Terms
    private final static Atom aCell = new Atom("CELL");
    private final static Atom aGold = new Atom("GOLD");
    private final static Atom aStep = new Atom("STEP");
    private static final Atom aC = new Atom("C");
    private static final Atom a1 = new Atom("1");
    private static final Atom a2 = new Atom("2");

    final static Variable vY = new Variable("?Y");
    final static Variable vX = new Variable("?X");

    // Predicates
    Predicate  trueCellA;
    Predicate  trueGoldC;
    Predicate  trueStep1;
    Predicate  trueStep2;
    Predicate  trueStepX;
    List<Substitution> allPovenSigmas;
    GameState gameState;
    
    @BeforeClass
    public static void putTitle(){
        logger.info( "\n=== GameStateTest ===" );
    }

    @Before
    public void setUp(){
        trueCellA = new Predicate( Const.aTrue, 
                new Predicate(aCell, new Atom("A") ) );

        trueGoldC = new Predicate( Const.aTrue, 
                new Predicate(aGold, aC ) );

        trueStep1 = new Predicate(Const.aTrue, 
                new Predicate(aStep, a1 ) );

        trueStep2 = new Predicate( Const.aTrue, 
                new Predicate(aStep, a2 ) );

        trueStepX = new Predicate( Const.aTrue,
                new Predicate(aStep, vX));
        
        Substitution sigmaStep = new Substitution();
        sigmaStep.addAssociation( vX, a1 );
        allPovenSigmas = new ArrayList<Substitution>();
        allPovenSigmas.add( sigmaStep );
        
        gameState = new GameState(new HashMap<Atom,ExpressionList>());        
        gameState.setProven( trueStepX, allPovenSigmas );
    }

    @Test
    public final void testEqualsObject() {
        HashMap<Atom,ExpressionList> dummy;

        dummy = new HashMap<Atom,ExpressionList>();
        dummy.put( aCell, new ExpressionList( new Expression[] {trueCellA} ) );
        dummy.put( aGold, new ExpressionList( new Expression[] {trueGoldC} ) );
        dummy.put( aStep, new ExpressionList( new Expression[] {trueStep1} ) );
        GameState state0 = new GameState(dummy);

        dummy = new HashMap<Atom,ExpressionList>();
        dummy.put( aCell, new ExpressionList( new Expression[] {trueCellA} ) );
        dummy.put( aGold, new ExpressionList( new Expression[] {trueGoldC} ) );
        dummy.put( aStep, new ExpressionList( new Expression[] {trueStep2} ) );
        GameState state1 = new GameState(dummy);

        dummy = new HashMap<Atom,ExpressionList>();
        dummy.put( aCell, new ExpressionList( new Expression[] {trueCellA} ) );
        dummy.put( aGold, new ExpressionList( new Expression[] {trueGoldC} ) );
        dummy.put( aStep, new ExpressionList( new Expression[] {trueStep2} ) );
        GameState state2 = new GameState(dummy);

        assertFalse( state0.equals( state1 ) );
        assertTrue ( state1.equals( state2 ) );
    }
}
