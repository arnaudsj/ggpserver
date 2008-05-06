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
package stanfordlogic.test.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import stanfordlogic.util.LengthInputStream;
import junit.framework.TestCase;

/**
 *
 */
public class LengthInputStreamTest extends TestCase
{

    /*
     * Test method for 'camembert.util.LengthInputStream.read()'
     */
    public void testRead() throws IOException
    {
        String buffer = "Hello this is a very friendly little buffer";
        ByteArrayInputStream input = new ByteArrayInputStream(buffer.getBytes());
        
        LengthInputStream lis = new LengthInputStream(input, 20);
        
        StringBuilder result = new StringBuilder();
        
        int c;
        while ( (c = lis.read()) != -1 )
        {
            result.append( (char) c);
        }
        
        assertEquals( buffer.substring(0, 20), result.toString() );
    }

}
