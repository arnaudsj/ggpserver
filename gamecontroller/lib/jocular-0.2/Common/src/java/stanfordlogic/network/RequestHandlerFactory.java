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
package stanfordlogic.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Logger;

import stanfordlogic.game.GameManager;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.network.RequestHandler.HttpHeader;
import stanfordlogic.util.LengthInputStream;
import stanfordlogic.util.LineInputStream;

/**
 *
 */
public abstract class RequestHandlerFactory
{
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.network");
    
    synchronized public RequestHandler createRequestHandler(
                                                                ConnectionManager manager,
                                                                Socket socket )
            throws Exception
    {
        LineInputStream input = new LineInputStream(socket.getInputStream());

        // The first thing is to read the header from the socket.
        HttpHeader header = readHeader(input);
        // HttpHeader header = readHeader( reader );

        // Now parse the content, only reading as much as contentLength
        InputStream contentInput;

        contentInput = new LengthInputStream(input, header.contentLength_);

        GdlExpression content = GameManager.getParser().parse(contentInput);

        RequestHandler handler;

        // Find out what kind of message it was, and create the appropriate
        // RequestHandler subclass
        if ( content instanceof GdlList )
            handler = createFromList(socket, header, (GdlList) content);
        else
        {
            throw new IllegalArgumentException("Can't handle gdl expression of type "
                                               + content.getClass().getName()
                                               + " during request handling");
        }

        handler.setManager(manager);
        return handler;
    }

    private HttpHeader readHeader( LineInputStream input ) throws IOException
    {
        HttpHeader result = new HttpHeader();

        String line;

        logger_.fine("Parsing message header.");

        while ( ( line = input.readLine() ) != null )
        {
            logger_.finer("Got line: " + line);

            if ( line.trim().compareTo("") == 0 )
                break;
            if ( line.startsWith("Sender:") )
                result.receiver_ = line.substring(8);
            else if ( line.startsWith("Receiver:") )
                result.receiver_ = line.substring(10);
            else if ( line.startsWith("Content-length:")
                      || line.startsWith("Content-Length:") )
                result.contentLength_ = Integer.parseInt(line.substring(16));
        }

        logger_.fine("Done parsing message header. Content length: "
                     + result.contentLength_);

        return result;
    }

    protected abstract RequestHandler createFromList( Socket socket, HttpHeader header,
                                                      GdlList list ) throws Exception;
}
