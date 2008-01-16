package de.tu_dresden.inf.ggp06_2.simulator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;

public class KIFParsingTest {
    
    @Test
    public void parseButtonsAndLights(){
        boolean wantDebugPrintouts = false;
        String gameFile = "./testdata/buttons_lights.kif";        
        
        GameSimulator gameSimulator = 
            new GameSimulator(wantDebugPrintouts);
        gameSimulator.parseFileIntoTheory(gameFile );
        gameSimulator.simulateStart();
        
        ExpressionList roles = gameSimulator.getRoles();
        assertNotNull(roles);
        assertTrue(0 < roles.size());
        
        ExpressionList moves = gameSimulator.getLegalMoves( roles.get( 0 ) );
        assertNotNull(moves);
        assertTrue(0 < moves.size());
    }
    
    /**
     * Problem with this method is, that jUnit does not find the file.
    @Test
    public void parseBlockWorld() {        
        boolean wantDebugPrintouts = false;
        String gameFile = "./testdata/block_world.kif";        
        
        GameSimulator gameSimulator = new GameSimulator(wantDebugPrintouts);        
        gameSimulator.parseFileIntoTheory(gameFile );
        
        ExpressionList roles = gameSimulator.getRoles();
        assertNotNull(roles);
        assertTrue(0 < roles.size());
        
        ExpressionList moves = gameSimulator.getLegalMoves( roles.get( 0 ) );
        assertNotNull(moves);
        assertTrue(0 < moves.size());        
    }
    */
}
