///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

/**
 * 
 */
package stanfordlogic.test.prover;

import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.Parser;
import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.prover.Fact;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.Substitution;
import stanfordlogic.prover.TermVariable;
import stanfordlogic.prover.Unifier;
import stanfordlogic.prover.VariableFact;
import junit.framework.TestCase;

/**
 *
 */
public class TestUnifier extends TestCase
{
    
    Parser parser_;
    SymbolTable symbolTable_;
    
    static TestUnifier currentTest;
    
    public static SymbolTable getTable()
    {
        return currentTest.symbolTable_;
    }
    
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        parser_ = new Parser();
        symbolTable_ = parser_.getSymbolTable();
        
        currentTest = this;
    }

    /*
     * Test method for 'camembert.prover.Unifier.mgu(Fact, Fact)'
     */
    public void testBasicMgu()
    {
        GdlList l1, l2;
        Fact f1, f2;
        Substitution sigma;
        
        
        l1 = parser_.parse("foo baz bar");
        l2 = parser_.parse("foo baz bar");
        f1 = VariableFact.fromList(l1);
        f2 = GroundFact.fromList(l2);
        
        sigma = Unifier.mgu(f1, f2);
        assertNotNull(sigma);
        assertEquals(0, sigma.numMappings());
        
        
        l1 = parser_.parse("foo ?x bar");
        l2 = parser_.parse("foo baz bar");
        f1 = VariableFact.fromList(l1);
        f2 = GroundFact.fromList(l2);
        
        sigma = Unifier.mgu( f1, f2 );
        assertNotNull(sigma);
        assertEquals( f2.getTerm( 0 ),
                sigma.getMapping( (TermVariable) f1.getTerm( 0 ) ) );
        assertEquals(1, sigma.numMappings());
        
        
        l1 = parser_.parse("foo ?x ?x");
        l2 = parser_.parse("foo baz ?y");
        f1 = VariableFact.fromList(l1);
        f2 = VariableFact.fromList(l2);
        
        sigma = Unifier.mgu(f1,f2);
        assertNotNull(sigma);
        assertEquals( f2.getTerm(0), sigma.getMapping( (TermVariable) f1.getTerm(0)) );
        assertEquals( f2.getTerm(0), sigma.getMapping( (TermVariable) f1.getTerm(1)) );
        assertEquals( f2.getTerm(0), sigma.getMapping( (TermVariable) f2.getTerm(1)) );
        assertEquals(2, sigma.numMappings());
    }
    
    public void testFunctionMgu()
    {
        GdlList l1, l2;
        Fact f1, f2;
        Substitution sigma;
        
        l1 = parser_.parse("foo (f a b)");
        l2 = parser_.parse("foo ?x");
        f1 = GroundFact.fromList(l1);
        f2 = VariableFact.fromList(l2);
        
        sigma = Unifier.mgu(f1, f2);
        assertNotNull(sigma);
        assertEquals( f1.getTerm(0), sigma.getMapping( (TermVariable) f2.getTerm(0)) );
        assertEquals(1, sigma.numMappings());
    }
    
    public void testBadMgu()
    {
        GdlList l1, l2;
        Fact f1, f2;
        Substitution sigma;
        
        // Now try one that will fail
        l1 = parser_.parse("foo ?x ?x");
        l2 = parser_.parse("foo bar baz");
        f1 = VariableFact.fromList(l1);
        f2 = VariableFact.fromList(l2);
        
        sigma = Unifier.mgu(f1,f2);
        assertNull(sigma);
        
        
        l1 = parser_.parse("foo ?x b ?x");
        l2 = parser_.parse("foo ?y b (f ?y)");
        f1 = VariableFact.fromList(l1);
        f2 = VariableFact.fromList(l2);
        
        sigma = Unifier.mgu(f1, f2);
        assertNull(sigma);
    }

    public void testDifferentFunctions()
    {
        GdlList l1, l2;
        Fact f1, f2;
        Substitution sigma;
        
        l1 = parser_.parse("foo ?x ?x");
        l2 = parser_.parse("toto bar baz");
        f1 = VariableFact.fromList(l1);
        f2 = VariableFact.fromList(l2);
        
        sigma = Unifier.mgu(f1,f2);
        assertNull(sigma);
    }
    
    public void testDifferentArity()
    {
        GdlList l1, l2;
        Fact f1, f2;
        Substitution sigma;
        
        l1 = parser_.parse("foo ?x ?y");
        l2 = parser_.parse("foo ?x ?y bar");
        f1 = VariableFact.fromList(l1);
        f2 = VariableFact.fromList(l2);
        
        sigma = Unifier.mgu(f1,f2);
        assertNull(sigma);
    }
    
    public void testIdenticalFunction()
    {
        GdlList l1, l2;
        Fact f1, f2;
        Substitution sigma;
        
        l1 = parser_.parse("foo ?x");
        l2 = parser_.parse("foo ?x");
        f1 = VariableFact.fromList(l1);
        f2 = VariableFact.fromList(l2);
        
        sigma = Unifier.mgu(f1,f2);
        assertNotNull(sigma);
    }
    
    public void testIdenticalVariables()
    {
        GdlList l1, l2;
        Fact f1, f2;
        Substitution sigma;
        
        l1 = parser_.parse("foo ?x ?x");
        l2 = parser_.parse("foo ?x ?x");
        f1 = VariableFact.fromList(l1);
        f2 = (VariableFact) VariableFact.fromList(l2).uniquefy();
        
        sigma = Unifier.mgu(f1,f2);
        assertNotNull(sigma);
        assertEquals(1, sigma.numMappings());
    }
    
    public void testIdenticalVariableNames()
    {
        GdlList l1, l2;
        Fact f1, f2;
        Substitution sigma;
        
        l1 = parser_.parse("foo ?x ?y");
        l2 = parser_.parse("foo ?y ?y");
        f1 = VariableFact.fromList(l1);
        f2 = (VariableFact) VariableFact.fromList(l2).uniquefy();
        
        sigma = Unifier.mgu(f1,f2);
        assertNotNull(sigma);
        assertEquals(2, sigma.numMappings());
    }
    
    public void testRecursiveSub()
    {
        GdlList l1, l2;
        Fact f1, f2;
        Substitution sigma;
        
        l1 = parser_.parse("foo ?x ?x ?z");
        l2 = parser_.parse("foo  a ?y ?y");
        f1 = VariableFact.fromList(l1);
        f2 = VariableFact.fromList(l2);
        
        sigma = Unifier.mgu(f1,f2);
        assertNotNull(sigma);
        assertTrue(sigma.numMappings() == 3);
    }
}
