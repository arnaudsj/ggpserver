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
package stanfordlogic.jocular;

import java.util.logging.Level;
import java.util.logging.Logger;

import stanfordlogic.game.GameManager;
import stanfordlogic.jocular.game.GenericGamerFactory;
import stanfordlogic.jocular.game.MinimaxGamer;
import stanfordlogic.jocular.network.RequestHandlerFactory;
import stanfordlogic.network.ConnectionManager;
import stanfordlogic.util.CommandLineParser;
import stanfordlogic.util.LogPropertiesLoader;

/**
 *
 */
public class Main
{
    public final static int DEFAULT_PORT = 4001;
    
    public static void setupLoggerProperties()
    {
        System.setProperty("java.util.logging.config.class", "stanfordlogic.util.LogPropertiesLoader");
        
        Logger.getLogger("stanfordlogic").setUseParentHandlers(false);
        Logger.getLogger("stanfordlogic").addHandler(LogPropertiesLoader.getDefaultHandler());
        
        Logger.getLogger("stanfordlogic.game").setLevel(Level.ALL);
        Logger.getLogger("stanfordlogic.game.search").setLevel(Level.FINE);
        
        Logger.getLogger("stanfordlogic.prover").setLevel(Level.INFO);
        Logger.getLogger("stanfordlogic.prover.unify").setLevel(Level.INFO);
    }

    /**
     * @param args
     */
    public static void main(String [] args)
    {
    	CommandLineParser clp = new CommandLineParser();
        clp.addFlag("--daemon");
        clp.addParam("--port");
        clp.setUsage("usage: <--daemon> <--port='portNum'>");
        
        clp.parse(args);        
        boolean daemonMode = clp.argSpecified("--daemon");
        int port = clp.argSpecified("--port") ? clp.getArgAsInt("--port") : DEFAULT_PORT;
                
        if ( ! daemonMode )
        {
            System.out.println(" ########################################");
            System.out.println(" # Press Enter to shut the player down. #");
            System.out.println(" ########################################");
            System.out.println();
        }
        else
        {
            System.out.println(" ############################################################");
            System.out.println(" # Send \"(KILL abc)\" on port " + port + " to shut the player down. #");
            System.out.println(" ############################################################");
            System.out.println();
        }
        
        setupLoggerProperties();
        
        // TODO: read gamer config from file
        
        GenericGamerFactory factory = new GenericGamerFactory();
        factory.setGamerType(MinimaxGamer.class);
        GameManager.setGamerFactory(factory);
        
        try
        {
            ConnectionManager manager = new ConnectionManager(port, new RequestHandlerFactory());
            manager.start();
            
            if ( !daemonMode )
            {
                // Wait for input to kill the program
                System.in.read();
                manager.shutdown();
            }
            else
                manager.join();

            // All done.
            System.exit(0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
