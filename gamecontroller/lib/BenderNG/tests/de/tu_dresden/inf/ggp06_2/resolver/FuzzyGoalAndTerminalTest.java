package de.tu_dresden.inf.ggp06_2.resolver;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class FuzzyGoalAndTerminalTest {

    private static final Logger logger = Logger.getLogger( FuzzyGoalAndTerminalTest.class );

    private GameManager gameManager = new GameManager();

    private static final Atom aWhite = new Atom("white");
    private static final Atom a100 = new Atom("100");

    private static final FuzzySubstitution emptySubstitution = new FuzzySubstitution();
    private static final Expression goalWhite100 = new Predicate(Const.aGoal, aWhite , a100);

    Set<FuzzySubstitution> accumulator;
    GameStateScope initialStateScope;
    Game game;
    GameState initial;


    @Before
    public void setUp(){
        Theory theory;

        ExpressionList gameRules = Parser.parseFile("./testdata/games/Chess.kif");
        game = gameManager.getGameByGDL( gameRules.toString() );
        initial = game.getInitialNode().getState();

        theory = new Theory(gameRules);
        initialStateScope  = new GameStateScope(theory, initial);
        accumulator = new HashSet<FuzzySubstitution>();
    }

    @Test
    public void goal100State(){

        FuzzyResolution fuzzyGoalValue = null;
        try {
            for (Atom role : game.getRoleNames())
                game.getLegalMoves( role, initial, new TimerFlag() );

            fuzzyGoalValue = goalWhite100.fuzzyEvaluate(
                    emptySubstitution, initialStateScope,
                    new ArrayList<Expression>(), new TimerFlag() );

        }
        catch ( InterruptedException e ) {
            logger.error ( " interrupted! " );
            fail();
        }
        assertNotNull(fuzzyGoalValue);
        assertTrue(0 < fuzzyGoalValue.getFuzzyValue());
        assertTrue(1 > fuzzyGoalValue.getFuzzyValue());
        logger.info( "goal evaluated to:"+fuzzyGoalValue.getFuzzyValue() );
    }

}
