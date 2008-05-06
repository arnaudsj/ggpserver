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
import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.prover.Fact;
import stanfordlogic.game.GameManager;
import junit.framework.TestCase;

/**
 *
 */
public class TestFacts extends TestCase
{
    
    Parser parser_;
    SymbolTable symtab_;
    
    
    @Override
    protected void setUp() throws Exception
    {
        parser_ = GameManager.getParser();
        symtab_ = parser_.getSymbolTable();
    }

    public void testEqualsMvr()
    {
        String s1 = "count 1 1 ?var11583106 ?var11583106";
        String s2 = "count 1 1 ?var11580908 ?var11580909";
        
        Fact f1 = Fact.fromExpression( parser_.parse(s1) );
        Fact f2 = Fact.fromExpression( parser_.parse(s2) );
        
        // Make sure f1 can't map to f2
        assertFalse(f1.canMapVariables(f2));
        
        // But, that f2 can map to f1
        assertTrue(f2.canMapVariables(f1));
    }
    
}
