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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Fake socket that simulates its connection data from a string.
 */
public class StringSocket extends Socket
{
    InputStream input_;
    OutputStream output_;
    
    public StringSocket(String input, OutputStream output)
    {
        input_ = new ByteArrayInputStream(input.getBytes());
        output_ = output;
    }
    
    
    @Override
    public synchronized void close() throws IOException
    {
        input_ = null;
        // Don't close the output stream! Just make it null.
        output_ = null;
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return input_;
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        return output_;
    }

    @Override
    public boolean isClosed()
    {
        return input_ != null;
    }

    @Override
    public boolean isConnected()
    {
        return input_ != null;
    }
    
}
