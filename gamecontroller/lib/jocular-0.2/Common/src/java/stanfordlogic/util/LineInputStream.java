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
package stanfordlogic.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/** Class to read lines. Unlike BufferedReader, the LineReader
 * will not run amok buffering as much as it can. It will only
 * buffer as much as it needs to determine if the character
 * after a \n is a \r or part of the next line. 
 *
 */
public class LineInputStream extends FilterInputStream
{
    private int bufferedChar_;
    private static final int NO_BUF = -2;
    
    public LineInputStream(InputStream r)
    {
        super(r);
        bufferedChar_ = NO_BUF;
    }
    
    @Override
    public int read() throws IOException
    {
        int c;
        if ( (c = bufferedChar_) != NO_BUF )
        {
            bufferedChar_ = NO_BUF;
            return c;
        }
        
        return super.read();
    }
    
    public String readLine() throws IOException
    {
        StringBuilder builder = new StringBuilder(128);
        
        while ( true )
        {
            int c;
            
            // if we didn't have a buffered char, read the next one.
            if ( (c=bufferedChar_) == NO_BUF )
                c = super.read();
            
            // We did have a buffered char, so use it and clear the buffer
            else
                bufferedChar_ = NO_BUF;
            
            if ( c == '\n' || c == '\r' )
            {
                int nextC = super.read();
                
                // munch the following \n or \r, if there is one
                if ( (nextC == '\n' || nextC == '\r') && nextC != c )
                    /* munch */;
                else
                    bufferedChar_ = nextC;
                
                break;
            }
            
            builder.append((char) c);
        }
        
        return builder.toString();
    }
}
