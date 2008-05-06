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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import stanfordlogic.game.GameManager;


public class ConnectionManager extends Thread 
{
    private ServerSocket                server;
    private boolean                     running;
    
    private RequestHandlerFactory factory_;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.network");
    
    /**
     * Creation of the http server.
     * 
     * @param port Which port to open the player on.
     * @param factory The RequestHandler factory used to create request handlers.
     * 
     * @throws IOException If something goes wrong opening the server socket.
     */
    public ConnectionManager(int port, RequestHandlerFactory factory) throws IOException
    {
        server = new ServerSocket(port);
        factory_ = factory;
        logger_.info("Listening on port " + port + ".");
    }
    
    /**
     * Server loop: looks for incoming connections and pass them on to a request handler
     * which is further handed over to the game manager.
     */
    @Override
    public void run()
    {
        running = true;
        try
        {
            logger_.info("Awaiting incoming connections...");
            
            Socket incoming;
            while((incoming = server.accept()) != null)
            {
                String hostname = incoming.getInetAddress().getHostName();
                logger_.info("Incoming connection from " + hostname);
                
                
                try {
                    // Create the request handler object
                    RequestHandler handler = factory_.createRequestHandler(this, incoming);
                
                    // Tell the game manager to deal with the new request
                    GameManager.newRequest(handler);
                }
                catch (Exception e) {
                    logger_.severe("Error handling request from " + hostname );
                    e.printStackTrace();
                }
            }
        }
        catch(IOException e)
        {
            if(running)
            {
                logger_.severe("General network failure, argh");
                e.printStackTrace();
            }
        }
    }

    /**
     * Safely shuts down the server.
     */
    public void shutdown()
    {
        if ( !running )
            return;
        running = false;
        try
        {
            server.close();
            this.join();
        }
        catch ( Exception e )
        {
        }
    }
}
