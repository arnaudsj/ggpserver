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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author David
 *
 */
public class GdlTests
{

	public static Test suite()
	{
        TestSuite suite = new TestSuite( "Test for stanfordlogic.gdl.test" );
		//$JUnit-BEGIN$
		suite.addTestSuite( LexerTest.class );
		suite.addTestSuite( ParserTest.class );
		//$JUnit-END$
		return suite;
	}

}
