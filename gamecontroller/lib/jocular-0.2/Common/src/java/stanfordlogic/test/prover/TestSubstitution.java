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

import stanfordlogic.gdl.Parser;
import stanfordlogic.prover.Fact;
import stanfordlogic.prover.Substitution;
import stanfordlogic.prover.Term;
import stanfordlogic.prover.TermFunction;
import stanfordlogic.prover.TermObject;
import stanfordlogic.prover.TermVariable;
import stanfordlogic.game.GameManager;
import junit.framework.TestCase;

/**
 *
 */
public class TestSubstitution extends TestCase
{
    Parser parser_;

    @Override
    protected void setUp() throws Exception
    {
        parser_ = GameManager.getParser();
    }

    public void testSubstitution()
    {
        Substitution s = new Substitution();
        
        TermVariable v1 = TermVariable.makeTermVariable();
        TermVariable v2 = TermVariable.makeTermVariable();
        TermObject t = TermObject.makeTermObject(256);
        
        s.addMapping( v1, v2 );
        s.addMapping( v2, t );
        
        assertEquals(2, s.numMappings() );
        assertEquals(t, s.getMapping(v1) );
        assertEquals(t, s.getMapping(v2) );
    }
    
    public void testFunctionVarMapping()
    {
        Fact f1 = makeFact("legal ?x");
        
        TermFunction tf = getTermFunction("mark ?y ?z");
        
        TermVariable varX = getVariable("?x");
        TermVariable varY = getVariable("?y");
        TermVariable varZ = getVariable("?z");
        
        Substitution s = new Substitution();
        s.addMapping(varX, tf);
        s.addMapping(varY, getTermObject("1"));
        s.addMapping(varZ, getTermObject("2"));
        
        
        Fact result = (Fact) f1.applySubstitution(s);
        
        assertEquals( makeFact("legal (mark 1 2)"), result );
    }
    
    
    private Fact makeFact(String str)
    {
        return Fact.fromExpression( parser_.parse(str) );
    }
    
    private TermFunction getTermFunction(String str)
    {
        return (TermFunction) Term.buildFromGdl(parser_.parse(str));
    }
    
    private TermObject getTermObject(String str)
    {
        return TermObject.makeTermObject( parser_.getSymbolTable().get(str) );
    }
    
    private TermVariable getVariable(String str)
    {
        return new TermVariable( parser_.getSymbolTable().get(str.substring(1)) );
    }

}
