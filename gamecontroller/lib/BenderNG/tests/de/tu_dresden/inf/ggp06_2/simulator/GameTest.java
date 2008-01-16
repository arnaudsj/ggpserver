package de.tu_dresden.inf.ggp06_2.simulator;

import static org.junit.Assert.*;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.connection.Message;
import de.tu_dresden.inf.ggp06_2.connection.Player;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;

/**
 * The PuzzelTest class plays the 8Puzzel game from stanford server. This test 
 * class is meant to be a real life test based on a real life game.
 *
 * @author ingo
 */
public class GameTest {

    private static final Logger logger = Logger.getLogger(GameTest.class);
    String     game;
    String     response;    
    Player player;
    String     matchName  = "match.test";
    Match      match;
    Atom       role;
    int        playClock  = 10;
    int        startClock = 10;
    
    @BeforeClass
    public static void putTitle(){
        logger.info( "\n=== GameTest ===" );
    }

    @Test
    public void testCorridor() {

        // load puzzel Game
        game = Parser.parseFile("testdata/games/Corridor.kif").toString();

    }
    
    @Test
    public void test8Puzzel() {

        // load puzzel Game
        game = Parser.parseFile("testdata/games/8Puzzel.kif").toString();
        
        // initialise game
        player = new Player();
        
        player.commandStart( new Message( "(START "   + 
                                          matchName   +
                                          " player (" +  
                                          game        + 
                                          ") "        +
                                          startClock  +
                                          " "         +
                                          playClock   + ")" ) );

        // play message
        player.commandPlay(new Message("(PLAY " + matchName + " NIL)"));        
        
        assertEquals( player.getMatch().getState(),
                      player.getMatch().strategy.getGame().getInitialNode().getState() );

    }
    
    @Test
    public void testButtons() {

        // load buttons game
        game = Parser.parseFile("testdata/games/Buttons.kif").toString();
        
        // initialise game
        player = new Player();   
        player.commandStart( new Message( "(START "  + 
                                          matchName  +
                                          " robot (" +  
                                          game       + 
                                          ") "       +
                                          startClock +
                                          " "        +
                                          playClock  + ")" ) );

        // play message
        player.commandPlay( new Message( "(PLAY " + 
                                         matchName + 
                                         " NIL)" ) );        
        
        assertEquals( player.getMatch().getState(),
                      player.getMatch().strategy.getGame().getInitialNode().getState() );

    }

    @Test
    public void testHanoi() {

        // load puzzel Game
        game = Parser.parseFile("testdata/games/Hanoi.kif").toString();
        
        // initialise game
        player = new Player();
        player.commandStart( new Message( "(START "   + 
                                          matchName   +
                                          " player (" +  
                                          game        + 
                                          ") "        +
                                          startClock  +
                                          " "         +
                                          playClock   + ")" ) );

        // play message
        player.commandPlay( new Message( "(PLAY " + 
                                         matchName + 
                                         " NIL)" ) );
        
        assertEquals( player.getMatch().getState(),
                      player.getMatch().strategy.getGame().getInitialNode().getState() );

    }

    @Test
    public void testTictactoe1() {

        // load puzzel Game
        game = Parser.parseFile("testdata/games/Tictactoe1.kif").toString();

        // initialise game
        player = new Player();
        player.commandStart( new Message( "(START "    + 
                                          matchName    +
                                          " xplayer (" +  
                                          game         + 
                                          ") "         +
                                          startClock   +
                                          " "          +
                                          playClock    + ")" ) );

        // play message
        response = player.commandPlay( new Message( "(PLAY " + 
                                                    matchName + 
                                                    " NIL)" ) );        

        // current state tests
        match = player.getMatch();
        assertEquals ( match.getState(), 
                       match.strategy.getGame().getInitialNode().getState() );
        assertTrue   ( response.startsWith("(MARK " )                  );
        
        // play message
        response = player.commandPlay( new Message( "(PLAY "  + 
                                                    matchName + 
                                                    " ("      + 
                                                    response  +
                                                    " NOOP))" ) );
        
        // current state tests
        match = player.getMatch();
        assertEquals ( response, "NOOP"                      );

        
        // play message
        response = player.commandPlay( new Message( "(PLAY "  + 
                                                    matchName + 
                                                    " ("      + 
                                                    response  +
                                                    " (MARK 3 2)))" ) );
        // current state tests
        match = player.getMatch();
    
    }

}
