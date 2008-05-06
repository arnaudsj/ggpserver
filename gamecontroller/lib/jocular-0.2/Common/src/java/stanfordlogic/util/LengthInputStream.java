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

/** An input stream wrapper that stops reading after a certain
 * number of characters have been read.
 *
 */
public class LengthInputStream extends FilterInputStream
{

	private int readSoFar_; // how much we've read so far
	private int length_; // how much to read in total
	
	public static final int EOF = -1;

	public LengthInputStream(InputStream stream, int length)
	{
		super(stream);
		length_ = length;
        readSoFar_ = 0;        
	}

	@Override
	public int read() throws IOException
	{
		// have we read our entire stream already?
		if ( readSoFar_ < length_ )
		{
			readSoFar_++;
			return super.read();
		}
		
		// else, we've read everything; return end of file.
		return EOF;
	}
    
    
    @Override
    public boolean markSupported()
    {
        return false;
    }

}
