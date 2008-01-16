package de.tu_dresden.inf.ggp06_2.connection;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PlayerServer extends NanoHTTPD {

    /* Stores the logger for this class */
    public static Logger logger = Logger.getLogger(PlayerServer.class);

    /* Stores the player of this server */
    Player player;

    /* Internal used strings */
    final static String STR_SERVER_START = "Started a PlayerServer: ";
    final static String STR_ERROR1 = "Message is empty!";
    final static String STR_ERROR2 = "Unknown command ";
    final static String STR_ERROR3 = "Unknown message state ";

    public PlayerServer(int port) throws IOException {
        super(port);
        player = new Player();
        logger.info( STR_SERVER_START + this);
    }

    @Override
    public Response serve( String uri, String method, Properties header, Properties parms, String data ) {
        try {
            String response_string = "";

            if ( data != null ) {
                logger.info( "Command: " + data );

                // parse message
                Message message = new Message(data);

                // switch to correct function
                switch ( message.type ) {
                case Message.START:
                    player.commandStart( message );
                    response_string = "READY";
                    break;
                case Message.PLAY:
                    response_string = player.commandPlay( message );
                    break;
                case Message.STOP:
                    player.commandStop( message );
                    response_string = "DONE";
                    break;
                case Message.EMPTY:
                    throw( new IllegalArgumentException(STR_ERROR1) );
                case Message.ERROR:
                    throw( new IllegalArgumentException(STR_ERROR2 + data) );
                default:
                    throw( new IllegalArgumentException(STR_ERROR3 + data) );
                }
            } else {
                logger.warn( "Empty message received!" );
            }

            logger.info( "Response:" + response_string );

            Response response = new Response( Response.HTTP_OK, "text/acl", response_string );
            response.addHeader( "Content-Length", "" + response_string.length() );
            return response;

        // error handling
        } catch ( IllegalArgumentException ex ) {
            logger.error("IllegalArgumentException:", ex);
            return new Response( Response.HTTP_BADREQUEST, "text/acl", "NIL" );
        }
    }


    /**
    * wait for server thread to exit
    *
    */
    public void waitForExit(){
        try {
            server_thread.join();
        } catch (Exception ex) {
            logger.error( "", ex );
        }
    }

    /**
    * starts the game player and waits for messages from the game master <br>
    * Command line options: [port]
    */
    public static void main(String[] args){
        try {
            int port = 4001;
            if ( args.length >= 1 ) {
                port=Integer.parseInt(args[0]);
            }
            PlayerServer server = new PlayerServer(port);
            server.waitForExit();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
