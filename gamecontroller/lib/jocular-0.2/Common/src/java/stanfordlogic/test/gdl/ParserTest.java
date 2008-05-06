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
package stanfordlogic.test.gdl;

import java.io.ByteArrayInputStream;

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.GdlVariable;
import stanfordlogic.gdl.Parser;
import stanfordlogic.gdl.SymbolTable;


import junit.framework.TestCase;

/** Simple test cases for GDL parser.
 * 
 */
public class ParserTest extends TestCase
{
    Parser parser_;
    SymbolTable symbolTable_;
    
        @Override
    protected void setUp() throws Exception
    {
        parser_ = new Parser();
        symbolTable_ = parser_.getSymbolTable();
    }

    private GdlAtom atom(String name)
    {
        return new GdlAtom(symbolTable_, symbolTable_.get(name));
    }
    
    private GdlVariable var(String name)
    {
        return GdlVariable.getGdlVariable(symbolTable_, symbolTable_.get(name));
    }

    /*
	 * Test method for 'camembert.gdl.Parser.parse(InputStream)'
	 */
	public void testParse()
	{
		String str = "(true (control xplayer)) (true (cell 2 2 b)) (role xplayer)" +
				"(<= (next (control ?x)) (control ?y) (distinct ?x ?y))";
		ByteArrayInputStream in = new ByteArrayInputStream( str.getBytes() );
		
		GdlList gdl = parser_.parse( in );
        
        assertEquals(4, gdl.getSize());
        
        for ( GdlExpression elem : gdl )
            assertEquals(GdlList.class, elem.getClass());
        
        GdlList elem1 = (GdlList) gdl.getElement(0);
        GdlList elem2 = (GdlList) gdl.getElement(1);
        GdlList elem3 = (GdlList) gdl.getElement(2);
        GdlList elem4 = (GdlList) gdl.getElement(3);
        
        assertEquals(2, elem1.getSize());
        assertEquals(2, elem2.getSize());
        assertEquals(2, elem3.getSize());
        assertEquals(4, elem4.getSize());
        
        GdlList l;
        
        assertEquals( atom( "true" ), elem1.getElement( 0 ) );
        l = new GdlList( symbolTable_, new GdlExpression [] { atom( "control" ),
                atom( "xplayer" ) } );
        assertEquals( l, elem1.getElement( 1 ) );

        assertEquals( atom( "true" ), elem2.getElement( 0 ) );
        l = new GdlList( symbolTable_, new GdlExpression [] { atom( "cell" ),
                atom( "2" ), atom( "2" ), atom( "b" ) } );
        assertEquals( l, elem2.getElement( 1 ) );

        assertEquals( atom( "role" ), elem3.getElement( 0 ) );
        assertEquals( atom( "xplayer" ), elem3.getElement( 1 ) );

        assertEquals( atom( "<=" ), elem4.getElement( 0 ) );
        l = new GdlList( symbolTable_, new GdlExpression [] {
                atom( "next" ),
                new GdlList( symbolTable_, new GdlExpression [] { atom( "control" ),
                        var( "x" ) } ) } );
        assertEquals( l, elem4.getElement( 1 ) );
        l = new GdlList( symbolTable_, new GdlExpression [] { atom( "control" ),
                var( "y" ) } );
        assertEquals( l, elem4.getElement( 2 ) );
        l = new GdlList( symbolTable_, new GdlExpression [] { atom( "distinct" ),
                var( "x" ), var( "y" ) } );
        assertEquals( l, elem4.getElement( 3 ) );		
	}
    
    public void testParse2()
    {
        String str =
                "(true ((control xplayer)) (true (cell 2 2 b)) (role xplayer)"
                        + "(<= (next (control ?x)) (control ?y) (distinct ?x ?y))";
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());

        // this should not parse (one too many parens before control):
        try {
            parser_.parse(in);
            
            assertTrue(false);
        }
        catch (Exception e) {
            // ok, we got an exception
        }
    }

}
