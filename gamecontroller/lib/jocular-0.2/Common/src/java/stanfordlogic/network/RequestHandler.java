///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

package stanfordlogic.network;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Logger;


public abstract class RequestHandler extends Thread
{
    protected static final String           replyHeader = 
            "HTTP/1.0 200 OK\n" +
            "Content-type: text/acl\n" +
            "Content-length: ";
    protected static final String           separator = 
            "\n";
    
    private Socket                          socket_;
    private PrintStream                     writer_;
    
    protected String                        gameId_;

	protected ConnectionManager             manager_;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.network");
    
    public static class HttpHeader
    {
    	String         sender_;
    	String         receiver_;
    	int            contentLength_;
    }
    
    private HttpHeader                      header_;
    
    /**
     * Constructor.
     * 
     * @param socket The socket connection used for this handler.
     * @param header The HTTP header sent with this request.
     * @param gameId The game this connection is operating on.
     * 
     * @throws IOException If something goes wrong with the socket.
     */
    protected RequestHandler(Socket socket, HttpHeader header, String gameId) throws IOException
    {
        header_ = header;
        
        socket_ = socket;
        writer_ = new PrintStream(socket_.getOutputStream());
        
        gameId_ = gameId;
    }
    
    public String getGameId()
    {
        return gameId_;
    }
    
    public HttpHeader getHeader()
    {
        return header_;
    }

	public void setManager(ConnectionManager manager)
	{
		manager_ = manager;
	}
    
    /**
     * Mark request as 'finished' i.e. handled. This closes the socket.
     * 
     * @throws IOException If something goes wrong closing the socket.
     */
    public void finish() throws IOException
    {
        socket_.close();
        
        socket_ = null;
        writer_ = null;
    }

    /**
     * Thread function: 'execute' is run in an independent thread
     */
    @Override
    public void run()
    {
        try
        {
            execute();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Main function; contains the normal execution of a request handler. The
     * request handler is responsible for taking appropriate action and sending
     * an answer back to the game master.
     * 
     * @throws IOException
     *             If something goes wrong with the socket during processing.
     */
    protected abstract void execute() throws IOException;

    /**
     * Sends an answer to the game master.
     * Prints the basic reply header first, then the content length and finally the content itself.
     * 
     * The socket should probably be closed after the answer is sent.
     * 
     *  @param answer The answer to send over the socket.
     *  
     *  @throws IOException If something goes wrong writing to the socket.
     */
    protected void sendAnswer(String answer) throws IOException
    {
        writer_.print(replyHeader);
        writer_.print(answer.length() + separator + separator + answer + separator);
        writer_.flush();
        
        logger_.info(gameId_ + ": Replied with: " + answer);
    }
}
