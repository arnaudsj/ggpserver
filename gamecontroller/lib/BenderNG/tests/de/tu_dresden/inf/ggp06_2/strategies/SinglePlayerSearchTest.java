package de.tu_dresden.inf.ggp06_2.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class SinglePlayerSearchTest {
    private static final Logger logger = Logger.getLogger(SinglePlayerSearchTest.class);

    private static final Atom aA = new Atom("a");

    AbstractStrategy  singlePlayerSearch;
    GameNode gameNode;
    GameManager manager = new GameManager();
    Game game;

    @Test
    public void pickMoveButtonsLights(){
        ExpressionList gameRules = Parser.parseFile( "./testdata/buttons_lights.kif" );
        logger.info( ""+gameRules );
        game = manager.getGameByGDL( gameRules.toString() );
        singlePlayerSearch  = new SinglePlayerSearch( game, "robot", new TimerFlag() );

        logger.info( "=== Button lights ===" );
        Expression aMove = singlePlayerSearch.pickMove(game.getInitialNode());

        assertNotNull ( aMove                              );
        assertTrue    ( aMove instanceof Predicate         );
        Atom light = (Atom) ((Predicate) aMove).getOperands().get( 1 );
        assertEquals  ( aA, light );

        logger.info( aMove );
    }
    //Test
    public void pickMove8Puzzel(){
        ExpressionList gameRules = Parser.parseFile( "./testdata/games/8Puzzel.kif" );
        logger.info( ""+gameRules );
        game = manager.getGameByGDL( gameRules.toString() );
        singlePlayerSearch  = new SinglePlayerSearch( game, "player", new TimerFlag() );

        logger.info( "=== 8Ppuzzel ===" );
        Expression aMove = singlePlayerSearch.pickMove(game.getInitialNode());

        assertNotNull ( aMove                              );
        assertTrue    ( aMove instanceof Predicate         );
        Atom light = (Atom) ((Predicate) aMove).getOperands().get( 1 );
        assertEquals  ( aA, light );

        logger.info( aMove );
    }

    //Test
    public void pickMoveHanoi(){
        ExpressionList gameRules = Parser.parseFile( "./testdata/games/Hanoi.kif" );
        logger.info( ""+gameRules );
        game = manager.getGameByGDL( gameRules.toString() );
        singlePlayerSearch  = new SinglePlayerSearch( game, "player", new TimerFlag() );
        logger.info( "=== Hanoi ===" );
        Expression aMove = singlePlayerSearch.pickMove(game.getInitialNode());

        assertNotNull ( aMove                              );
        assertTrue    ( aMove instanceof Predicate         );
        logger.info( "picked move: "+aMove );
    }
}
