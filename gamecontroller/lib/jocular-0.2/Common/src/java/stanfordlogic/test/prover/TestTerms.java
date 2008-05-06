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

import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlVariable;
import stanfordlogic.gdl.Parser;
import stanfordlogic.prover.Term;
import stanfordlogic.prover.TermVariable;
import stanfordlogic.game.GameManager;
import junit.framework.TestCase;

/**
 *
 */
public class TestTerms extends TestCase
{
    Parser parser_;

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        parser_ = GameManager.getParser();
    }
    
    
    public void testTotalColumns()
    {
        assertEquals(1, makeTerm("object").getTotalColumns());
        assertEquals(1, makeTerm("?x").getTotalColumns());
        assertEquals(2, makeTerm("(f a)").getTotalColumns());
        assertEquals(3, makeTerm("(f a b)").getTotalColumns());
        assertEquals(3, makeTerm("(f (g b))").getTotalColumns());
        assertEquals(5, makeTerm("(f (g b) (h c))").getTotalColumns());
        assertEquals(5, makeTerm("(f (g ?x) (h ?y))").getTotalColumns());
    }
    
    private Term makeTerm(String str)
    {
        GdlExpression exp = parser_.parse(str).getElement(0);
        
        if ( exp instanceof GdlVariable )
            return TermVariable.makeTermVariable();
        else
            return Term.buildFromGdl(exp);
    }

}
