package de.tu_dresden.inf.ggp06_2.simulator;

import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;

public class SimulationTest {

    private GameManager gameManager = new GameManager();

    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void testSimulation() {
        ExpressionList expList = Parser.parseFile("./testdata/maze.kif");
        Game           game    = gameManager.getGameByGDL( expList.toString() );
        Simulation     sim     = new Simulation( game );
        sim.run();

    }
}
