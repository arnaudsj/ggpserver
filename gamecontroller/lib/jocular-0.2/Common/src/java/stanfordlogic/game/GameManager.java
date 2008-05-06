///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

package stanfordlogic.game;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import stanfordlogic.game.Gamer;

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.Parser;
import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.network.RequestHandler;
import stanfordlogic.util.Triple;


/**
 * The Game Manager encapsulates the global program state.
 */
public class GameManager
{
    /** The global parser. Global to avoid several allocations; safe because
     * parser is reentrable. */
    private static Parser              parser_      = new Parser();
    /** The global parser's symbol table. */
    private static SymbolTable         symbolTable_ = parser_.getSymbolTable();
    
    /** The factory used to create games from descriptions. */
    private static GamerFactory gamerFactory_ = null;
    
    /** Maps a game ID to a gamer for currently active games. */
    private static Map<String, Gamer> games_ = new TreeMap<String, Gamer>();
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.game");
    
    // These get automatically initialized to zero.
    final private static long [] statisticsTime_ = new long[5];
    final private static int  [] statisticsNumber_ = new int[5];
    
    public static final int TIME_METAGDL = 0;
    public static final int TIME_GET_AN_ANSWER = 1;
    public static final int TIME_GET_ALL_ANSWERS = 2;
    
    /**
     * Note that we spent <tt>howMuch</tt> nanoseconds in category
     * <tt>category</tt>.
     * 
     * @param category
     *            Category time was spent in.
     * @param howMuch
     *            Time spent in nanoseconds.
     */
    public static void addTime(int category, long howMuch)
    {
        statisticsTime_[category] += howMuch;
        statisticsNumber_[category]++;
    }
    
    public static double getAverageTime(int category)
    {
        if ( statisticsNumber_[category] == 0 )
            return 0;
        else
            return ((double) statisticsTime_[category]) / statisticsNumber_[category];
    }
    
    public static long getTotalTime(int category)
    {
        return statisticsTime_[category];
    }
    
    public static int getNumTime(int category)
    {
        return statisticsNumber_[category];
    }
    
    public static String getCategoryName(int category)
    {
        switch (category)
        {
            case TIME_METAGDL:
                return "MetaGDL";
            case TIME_GET_AN_ANSWER:
                return "GetAnAnswer";
            case TIME_GET_ALL_ANSWERS:
                return "GetAllAnswers";
            default:
                return "Unknown!!";
        }
    }
    
    public static void printTimeStats(PrintStream output)
    {
        for ( int i = 0; i <= TIME_GET_ALL_ANSWERS; i++ )
        {
            output.print(getCategoryName(i));
            output.print(": ");
            output.print( ((double) getTotalTime(i)) / 1000000 );
            output.print(" ms (n = ");
            output.print(getNumTime(i));
            output.print("; average = ");
            output.print( getAverageTime(i) / 1000000 );
            output.println(" ms)");
        }
    }
    
    /**
     * Get the global parser's symbol table.
     * @return The global symbol table.
     */
    public static SymbolTable getSymbolTable()
    {
        return symbolTable_;
    }
    
    /**
     * Get the global parser.
     * @return The global parser.
     */
    public static Parser getParser()
    {
        return parser_;
    }
    
    /**
     * Get the gamer factory in use to create gamers.
     * @return The GamerFactory currently in use.
     */
    public static GamerFactory getGamerFactory()
    {
        return gamerFactory_;
    }

    /**
     * @param gamerFactory The GamerFactory to use for creating new games.
     */
    public static void setGamerFactory(GamerFactory gamerFactory)
    {
        gamerFactory_ = gamerFactory;
    }

    /**
     * Called by the connection manager upon each new incoming connection.
     * 
     *  @param handler The handler that needs to be processed.
     */
    public static void newRequest(RequestHandler handler)
    {
        try
        {
            // Launch the handler thread
            handler.start();
        }
        catch(Exception e)
        {
            logger_.severe("Network failure: " + e.getMessage());
        }
    }
    
    
    /**
     * Starts a new game. Takes the information from the start message
     * and gives it to the gamer factory, which creates an appropriate
     * gamer for the game.
     * 
     * @param gameId The identifier of the game being started.
     * @param role   The role I am playing in the new game.
     * @param description The description (rules) of the game.
     * @param startClock The time given to think about the game.
     * @param playClock The time given to make a move.
     * 
     * @return The Gamer instance created to play the game.
     * 
     * @see stanfordlogic.Gamer
     */
    public static Gamer newGame(String gameId, GdlAtom role, GdlList description,
                                             int startClock, int playClock)
    {
        synchronized(GameManager.class)
        {
            if (gamerFactory_ == null) {
                logger_.severe("No gamer factory set!");
                return null;
            }
        
            // Make sure this game isn't already active:
            if (games_.containsKey(gameId)) {
                logger_.severe("Game already active: " + gameId);
                return null;
            }
        
            logger_.info("");
            logger_.info("-----------------------------------------------");
            logger_.info("NEW GAME!");
            logger_.info("");
            logger_.info("    My role : " + role.toString() );
            logger_.info("Start clock : " + startClock);
            logger_.info(" Play clock : " + playClock);
            logger_.info("");
            
            // put in a temporary game:
            games_.put(gameId, null);
        }
        
        Gamer g = gamerFactory_.makeGamer(gameId, role, description, startClock, playClock);
        
        synchronized(GameManager.class)
        {
            games_.put(gameId, g);
        }
        
        return g;
    }
    
    /**
     * Ends the game specified by <tt>gameId</tt>. The final moves are
     * processed and the payoffs are computed and printed to the logger.
     * 
     * @param gameId Name of the game to terminate.
     * @param prevMoves The last set of moves made in the game.
     */
    public static synchronized void endGame(String gameId, GdlList prevMoves)
    {
        Gamer gamer = games_.get(gameId);
        
        if (gamer == null)
        {
            logger_.severe(gameId + ": WARNING: Attempting to terminate game [" + gameId
                + "], but no such game");
            return;
        }
        
        try
        {
            StringBuilder prevMovesStr = new StringBuilder();
            prevMovesStr.append(" Previous moves: ");
            for ( GdlExpression exp : prevMoves )
            {
                prevMovesStr.append(exp.toString());
                prevMovesStr.append("  ");
            }

            logger_.info(gameId + ": Beginning payoff computation." + prevMovesStr);

            // Get the list of payoffs: <Role, Payoff, IsMe>
            List<Triple<String, Integer, Boolean>> results = gamer.getPayoffs(prevMoves);
            
            // Figure out what the longest role is
            int maxRoleLength = 0;
            for ( Triple<String, Integer, Boolean> res : results )
                maxRoleLength = Math.max(maxRoleLength, res.first.length());

            // Print out the payoffs
            for ( Triple<String, Integer, Boolean> res : results )
            {
                // print the right amount of spaces (so that things line up right)
                StringBuilder spacing = new StringBuilder();
                for ( int i = 0; i < maxRoleLength - res.first.length(); i++ )
                    spacing.append(" ");

                logger_.info("       " + ( res.third ? "->" : "  " ) + " " + res.first
                                + spacing + " " + res.second + " "
                                + ( res.third ? "<-" : "  " ));
            }
        }
        catch (Exception e)
        {
            logger_.severe(gameId + ": Error computing payoff: " + e.getClass().getName() + " - " + e.getMessage());
        }
        
        // tell the game it's time to die.
        gamer.stopIt();
        games_.remove(gameId);
    }
    
    /**
     * Get the Gamer associated with <tt>gameId</tt>.
     * 
     * @param gameId The name of the gamer to get. 
     * @return The Gamer object associated with <tt>gameId</tt>.
     * 
     * @see Gamer
     */
    public static synchronized Gamer getGame(String gameId)
    {
        return games_.get(gameId);
    }
    
}
