package de.tu_dresden.inf.ggp06_2.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.strategies.StrategyFactory;

public class Simulation extends Thread {
    
    /* Stores the logger for this class */
    public static final Logger logger = Logger.getLogger(Simulation.class);

    private static Random  random = new Random();

    private Game           game;
    private List<Match>    matches;
    
    public Simulation( Game simGame ) {
        game    = simGame;
        matches = new ArrayList<Match>();

        // create a match for each player
        for ( Atom role : game.getRoleNames() ) {
            
            matches.add( 
                    new Match( "simMatch_" + random.nextInt(),
                               StrategyFactory.createStrategy( game, 
                                                               role.toString(), 
                                                               0 
                                                               ),
                               role.toString()
                              )
                    );        
        
            logger.info( "Player " + role + "created RunnableMatch" );
        }   

        logger.info( "Initial State " + game.getInitialNode() );
    }
    
    
    /**
     * This method runs the game till it is finished
     */
    @Override
    public void run() {

        ExpressionList moves    = new ExpressionList();
        GameState      tmpState = null;

        // looks if the first player says that the game is finished or
        // if the state does not change anymore
        while ( !matches.get(0).currentNode.getState().isTerminal() ) {
            
            // generate moves for all players
            for (Match match : matches)
                moves.add( match.strategy.pickMove( match.currentNode ) );
            
            logger.info( "Current "         + tmpState );
            logger.info( "Next moves are: " + moves    );            

            // Apply previous moves to the matches
            for (Match match : matches)
                match.makeTurn( moves );
                        
            // keep track of moves
            tmpState = matches.get(0).currentNode.getState();
            
            // Delete previous moves
            moves.clear();
            
        }
        
        logger.info( "Simulation finished" );
        
    }
    
}
