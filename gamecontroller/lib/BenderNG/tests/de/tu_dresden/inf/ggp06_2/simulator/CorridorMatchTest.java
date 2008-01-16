package de.tu_dresden.inf.ggp06_2.simulator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;
import de.tu_dresden.inf.ggp06_2.strategies.AbstractStrategy;
import de.tu_dresden.inf.ggp06_2.strategies.RandomStrategy;

public class CorridorMatchTest {

    private GameManager gameManager = new GameManager();
    private Match corridorMatch = null;
    private Theory theory = null;

    private static final Logger logger = Logger.getLogger( CorridorMatchTest.class );
    private static final Atom a50 = new Atom("50");
    private static final Atom a100 = new Atom("100");

    @Before
    public void setUp(){
        ExpressionList chessRules = Parser.parseFile("./testdata/games/Corridor.kif");
        Game           game       = gameManager.getGameByGDL( chessRules.toString() );
        AbstractStrategy dummy    = new RandomStrategy(game, "white");
        corridorMatch = new Match("0", dummy, "0");
        theory = corridorMatch.strategy.getGame().getTheory();
    }

    @Test
    public void turnMaking(){
        FuzzySubstitution emptySub = new FuzzySubstitution();
        ArrayList<Expression> guard = new ArrayList<Expression>();

        GameState initial = corridorMatch.getState();
        logger.info( " before " );
        for (Atom role : corridorMatch.strategy.getGame().getRoleNames()){
            try {
                corridorMatch.strategy.getGame().getLegalMoves(
                        role, initial, new TimerFlag() );
            }
            catch ( InterruptedException e ) {
                logger.error( "error during legal moves generation!" );
                e.printStackTrace();
                fail();
            }
        }
        GameStateScope initialScope = new GameStateScope(theory, initial);
        for (Atom role : corridorMatch.strategy.getGame().getRoleNames()){
            Predicate pGoalWhite50 = new Predicate(Const.aGoal, role, a50);
            FuzzyResolution fuzzyResolution = null;
            try {
                fuzzyResolution = pGoalWhite50.fuzzyEvaluate(
                        emptySub, initialScope, guard, new TimerFlag() );
            }
            catch ( InterruptedException e ) {
                logger.error( " interrrupted! " );
                e.printStackTrace();
            }
            assertNotNull(fuzzyResolution);
            double fuzzyValue = fuzzyResolution.getFuzzyValue();
            assertTrue(-1 != fuzzyValue);
            assertTrue(0 < fuzzyValue);
            assertTrue(1 > fuzzyValue);
            assertTrue(guard.isEmpty());

            Predicate pGoalWhite100 = new Predicate(Const.aGoal, role, a100);
            try {
                fuzzyResolution = pGoalWhite100.fuzzyEvaluate(
                        emptySub, initialScope, guard, new TimerFlag() );
            }
            catch ( InterruptedException e ) {
                logger.error( " interrrupted! " );
                e.printStackTrace();
            }
            assertNotNull(fuzzyResolution);
            fuzzyValue = fuzzyResolution.getFuzzyValue();
            assertTrue(-1 != fuzzyValue);
            assertTrue(0 < fuzzyValue);
            assertTrue(1 > fuzzyValue);
            assertTrue(guard.isEmpty());

            try {
                fuzzyResolution = Const.aTerm.fuzzyEvaluate(
                        emptySub, initialScope, guard, new TimerFlag() );
            }
            catch ( InterruptedException e ) {
                logger.error( " interrrupted! " );
                e.printStackTrace();
            }
            assertNotNull(fuzzyResolution);
            fuzzyValue = fuzzyResolution.getFuzzyValue();
            assertTrue(-1 != fuzzyValue);
            assertTrue(0 < fuzzyValue);
            assertTrue(1 > fuzzyValue);
            assertTrue(guard.isEmpty());
        }

    }
}
