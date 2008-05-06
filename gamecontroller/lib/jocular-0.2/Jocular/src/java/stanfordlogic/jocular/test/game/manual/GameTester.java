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
package stanfordlogic.jocular.test.game.manual;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.Parser;
import stanfordlogic.game.GameManager;
import stanfordlogic.jocular.Main;
import stanfordlogic.jocular.game.GenericGamerFactory;
import stanfordlogic.jocular.game.MinimaxGamer;
import stanfordlogic.jocular.network.RequestHandlerFactory;
import stanfordlogic.network.RequestHandler;
import stanfordlogic.util.StringSocket;


public class GameTester
{
    public Parser parser_;

    public GameTester( String kifFile, String role, int startClock, int playClock ) throws Exception
    {
        parser_ = GameManager.getParser();

        // Load up the kif
        GdlList gameDesc = parser_.parse( new FileInputStream( kifFile ) );
        
        // if there is no role, find the first 'role' expression and use that
        if ( role.length() == 0 )
        {
            for ( GdlExpression exp : gameDesc )
            {
                if ( exp instanceof GdlList )
                {
                    GdlList l = (GdlList) exp;
                    if ( l.getElement(0).equals( new GdlAtom(parser_.getSymbolTable(), parser_.TOK_ROLE) ) )
                    {
                        GdlAtom roleAtom = (GdlAtom) l.getElement(1);
                        role = roleAtom.toString();
                        break;
                    }
                }
            }
        }

        // Create the start message
        String msg = makeMessage( "(start foo " + role + " " + gameDesc
                + " " + startClock + " " + playClock + ")" );

        // Send the play message
        sendMessage(msg);
    }
    
    public void sendMessage(String msg) throws Exception
    {
        // THINK: do we need a dummy connection manager instead of null?
        RequestHandler req = new RequestHandlerFactory().createRequestHandler(null, new StringSocket(msg, System.out));
        
        GameManager.newRequest(req);
    }

    public String makeMessage( String content )
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "POST / HTTP/1.0\n" );
        sb.append( "Accept: text/delim\n" );
        sb.append( "Sender: GAMEMASTER\n" );
        sb.append( "Receiver: GAMEPLAYER\n" );
        sb.append( "Content-type: text/acl\n" );
        sb.append( "Content-length: " + content.length() + "\n" );

        sb.append( "\n" );

        sb.append( content );

        return sb.toString();
    }
    
    private static String [] getArgs(BufferedReader input) throws IOException
    {
        ArrayList<String> args = new ArrayList<String>();

        // Get the game's .kif file
        System.out.print( "Game file (game-defs/*.kif): " );
        String game = input.readLine();
        args.add( "game-defs/" + game + ".kif" );

        System.out.print( "Role (default: 1st role): " );
        args.add( input.readLine() );
        
        //System.out.print( "Log file (default: logs/[game]-debug.log): ");
        //String log = input.readLine();
        
        /*if ( log.equals("") )
            args.add("logs/" + game + "-debug.log");
        else
            args.add(log);*/
        args.add("");
        
        System.out.print( "Start clock (default: 100): " );
        String start = input.readLine();
        
        if ( start.length() == 0 )
            args.add("100");
        else
            args.add(start);
        
        System.out.print( "Play clock (default: 60): " );
        String play = input.readLine();
        
        if ( play.length() == 0 )
            args.add("60");
        else
            args.add(play);
        
        return args.toArray( new String[0] );
    }

    public static void main( String [] args ) throws Exception
    {
        Main.setupLoggerProperties();
        
        GenericGamerFactory factory = new GenericGamerFactory();
        factory.setGamerType(MinimaxGamer.class);
        GameManager.setGamerFactory(factory);
        
        
        String kif, role;
        //String logFile;
        int startClock = 100;
        int playClock = 60;
        
        BufferedReader input = new BufferedReader( new InputStreamReader( System.in ) );
        
        if ( args.length == 0 )
            args = getArgs(input);

        kif = args[0];
        role = args[1];
        //logFile = args[2];
        startClock = Integer.parseInt( args[3] );
        playClock = Integer.parseInt( args[4] );

        // Set up debug file
        //PrintStream debugStream = new PrintStream(logFile);
        //GameManager.debugStream_ = debugStream;

        GameTester tester = new GameTester( kif, role, startClock, playClock );
        
        // Go into main loop.
        while ( true )
        {
            String line = input.readLine();
            
            if ( line.equals("quit") )
                break;
            
            else if ( line.equals("stats") )
                GameManager.printTimeStats(System.out);
            
            else if ( line.equals("play") )
                tester.sendMessage( tester.makeMessage("(play foo nil)"));
            
            else
            {
                String msg = tester.makeMessage(line);
                tester.sendMessage(msg);
            }
        }
        
        tester.sendMessage(tester.makeMessage("(stop foo)"));
    }
}
