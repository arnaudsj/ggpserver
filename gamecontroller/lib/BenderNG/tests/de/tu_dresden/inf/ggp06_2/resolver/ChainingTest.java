package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.scope.TheoryScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class ChainingTest {
    
    private static final Logger logger = Logger.getLogger( ChainingTest.class );
    
    private Theory      theory;
    private TheoryScope theoryScope;

    private static final TimerFlag flag = new TimerFlag();
    private static String gameRulesGDL;
    
    @BeforeClass
    public static void putTitle(){
        logger.info("\n=== ChainingTest ===");
    }

    @Before
    public void setUp(){
        
        gameRulesGDL = "(role a)                       " +  // UNIVERSALS
                       "(colour white)                 " +
                       "(colour black)                 " +                                
                       "(init (cell empty))            " +  // FLUENT
                       "(<= (terminal)                 " +  // RULES
                       "    (not (true (cell empty))) )" +
                       "(<= (goal a 100)               " +
                       "    (true (cell white)) )      " +
                       "(<= (goal a 0)                 " +
                       "    (true (cell black)) )      " +
                       "(<= (next (cell ?x) )          " +  // nextStateRules
                       "    (does a (mark ?x))         " +
                       "    (color ?x)                 " +
                       "    (true (cell empty)) )      " +
                       "(<= (legal a (mark ?y))        " +  // legalRules
                       "    (true (cell empty))        " +
                       "    (color ?y) )               ";
        
        ExpressionList gameRules  = Parser.parseGDL(gameRulesGDL);        
        theory                    = new Theory(gameRules);
        theoryScope               = new TheoryScope(theory);
    }
    
    @Test
    public void variableChaining(){
        List<Substitution> sigmas = null;
        try {
            sigmas = ( new Variable("?X") ).chain( new Substitution(), 
                                                   theoryScope,
                                                   flag );
            fail("Should throw ClassCastException!");
        
        } catch ( InterruptedException e ) {
            //logger.error(" interrupted !");
        
        } catch ( ClassCastException e){
            assertTrue( sigmas == null );
        }        
    }
    
    @Test
    public void atomChaining(){
        List<Substitution> sigmas = null;
        try {
            sigmas = ( new Atom("role") ).chain( new Substitution(), 
                                                 theoryScope,
                                                 flag );
        } catch ( InterruptedException e ) {
            fail("No failure was expected during simple chain of 'role'");
        }        
        assertTrue( sigmas != null && sigmas.isEmpty() );
    }
    
    @Test
    public void javaKnowledgeTest(){
        Map<Atom, ExpressionList> fluents = new GameState();
        ExpressionList values = new ExpressionList();
        fluents.put( Const.aTrue, values );
        values.add(new Predicate(Const.aTrue, new Predicate(Const.aLegal, Const.aRole)));
        
        ExpressionList result = fluents.get( Const.aTrue );
        
        assertNotNull(result);
        assertTrue(0 < result.size());
        assertEquals(Const.aTrue,  result.get( 0 ).firstOperand());
        assertEquals(Const.aLegal, result.get( 0 ).secondOperand());
    }
}
