package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.helper.ResolutionHelper;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.TheoryScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class RecursionTest {
    
    private static final  Logger logger = Logger.getLogger( RecursionTest.class );
    
    Theory theory;
    TheoryScope theoryScope;
    TimerFlag flag;
    
    private Atom a0 = new Atom("0");
    private Atom a100 = new Atom("100");
    static final Atom aA = new Atom("a");
    
    private Atom aStep = new Atom("step");
    private Atom aConnected = new Atom("connected");
    private Atom aStart = new Atom("start");
    private Atom aEnd = new Atom("end");
    private Variable vY = new Variable("?y");
    private Variable vX = new Variable("?x");
    private Variable vZ = new Variable("?z");
    private Atom aLink = new Atom("link");
    private Atom aSucc = new Atom("succ");
    private Atom aConnect = new Atom("connect");
    private Atom aPath = new Atom("path");
    private Atom aNode2 = new Atom("node2");
    private Atom aNode1 = new Atom("node1");
    
    private String gameDescription = 
        "(role a)                         " +   // rules and universals
        "(<= (goal a 100)                 " +
        "    (connected start end) )      " +
        "(<= (goal a 0)                   " +
        "    (not (connected start end)) )" +
        "(<= terminal                     " +
        "    (connected start end) )      " +
        "(<= terminal                     " +
        "    (true (step 5)) )            " +
//      "(<= (connected ?x ?y)            " +
//      "    (true (link ?x ?y)) )        " +
//      "(<= (connected ?x ?y)            " +
//      "    (true (link ?x ?y))          " +
//      "    (connected ?x ?y) )          " +
        "" +
        "" +
        "(<= (next (link ?x ?y))          " +
        "    (does a (connect ?x ?y)) )   " +
        "" +
        "(init (step 0))                  " +   // transposition
        "(path start node1)               " +   // other rules
        "(path node1 node2)               " +
        "(path node2 end  )";
    
    @BeforeClass
    public static void putTitle(){
        logger.info( "\n=== RecursionTest ===" );
    }
    
    @Before
    public void setUp() {        
        ExpressionList gameRules = Parser.parseGDL(gameDescription);
        
        gameRules.add( new Implication( 
                new Predicate(aConnected, vX, vY),
                new Predicate(Const.aTrue, new Predicate(aLink, vX, vY))) );
        
        gameRules.add( new Implication(
                new Predicate(aConnected, vX, vY),
                new Predicate(Const.aTrue, new Predicate(aLink, vX, vZ)),
                new Predicate(aConnected, vZ , vY)
        ));
        
        gameRules.add( new Implication(
                new Predicate(Const.aNext, new Predicate(aStep, vY)),
                new Predicate(Const.aTrue, new Predicate(aStep, vX)),
                new Predicate(aSucc, vX, vY)
        ));        
        gameRules.add( new Implication( 
                new Predicate(Const.aNext, new Predicate(aLink, vX, vY)),
                new Predicate(Const.aTrue, new Predicate(aLink, vX, vY))
        ));
        
        gameRules.add( new Implication(
                new Predicate(Const.aLegal, aA, new Predicate(aConnect, vX, vY)),
                new Predicate(aPath, vX, vY),
                new NotOperator(new Predicate(Const.aTrue, new Predicate(aLink, vX, vY)))
        ));
        
        theory      = new Theory(gameRules);
        theoryScope = new TheoryScope(theory);
        flag        = new TimerFlag();
    }
    
    @Test
    public void zeroGoal(){
        ExpressionList values = null;
        try {
            values = ResolutionHelper.resolveAndApply( 
                    vX, 
                    new Predicate(Const.aGoal, aA, vX), 
                    theoryScope,
                    flag);
        }
        catch ( InterruptedException e ) {
        }
        
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals(a0, values.get( 0 ));
    }
    
    @Test
    public void oneStep(){
        ExpressionList stateList = new ExpressionList();
        stateList.add( new Predicate( Const.aTrue, 
                                      new Predicate(aLink, aStart, aNode1) ) );
        GameState      state0 = new GameState(stateList);
        GameStateScope scope0 = new GameStateScope(theory, state0);
        
        ExpressionList values = null;
        try {
            values = ResolutionHelper.resolveAndApply( 
                    vX, 
                    new Predicate(Const.aGoal, aA, vX), 
                    scope0,
                    flag );
        }
        catch ( InterruptedException e ) {
        }
        
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals(a0, values.get( 0 ));
    }
    
    @Test
    public void finalGoal(){
        ExpressionList stateList = new ExpressionList();
        stateList.add( new Predicate(Const.aTrue, 
                new Predicate(aLink, aStart, aNode1)) );
        stateList.add( new Predicate(Const.aTrue, 
                new Predicate(aLink, aNode1, aNode2)) );
        stateList.add( new Predicate(Const.aTrue, 
                new Predicate(aLink, aNode2, aEnd)) );
        GameState state0 = new GameState(stateList);
        GameStateScope scope0 = new GameStateScope(theory, state0);
        
        ExpressionList values = null;
        try {
            values = ResolutionHelper.resolveAndApply( 
                    vX, 
                    new Predicate(Const.aGoal, aA, vX), 
                    scope0,
                    flag);
        }
        catch ( InterruptedException e ) {
        }
        
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals(a100, values.get( 0 ));
    }
    
    @Test
    public void initialMoves(){        
        ExpressionList values = null;
        Expression legal = new Predicate(Const.aLegal, aA, vX);
        try {
            values = ResolutionHelper.resolveAndApply( 
                    legal, legal, theoryScope, flag );
        }
        catch ( InterruptedException e ) {
        }
        
        assertNotNull(values);
        for(Expression legalMove : values ){
            logger.info( legalMove );
        }
    }
}
