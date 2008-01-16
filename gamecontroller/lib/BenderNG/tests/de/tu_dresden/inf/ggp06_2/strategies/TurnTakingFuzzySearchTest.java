package de.tu_dresden.inf.ggp06_2.strategies;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;
import de.tu_dresden.inf.ggp06_2.strategies.mixins.StubMixin;

public class TurnTakingFuzzySearchTest {
    private static final Logger logger = Logger.getLogger(TurnTakingFuzzySearchTest.class);

    AbstractStrategy  turnTakingFuzzySearch;
    GameNode gameNode;
    GameManager manager = new GameManager();
    Game game;

    //@Test
    public void pickMoveTicTacToe(){
        ExpressionList gameRules = Parser.parseFile( "./testdata/games/Tictactoe1.kif" );
        logger.info( ""+gameRules );
        game = manager.getGameByGDL( gameRules.toString() );
        turnTakingFuzzySearch  = new TurnTakingFuzzySearch( game, "xplayer", new StubMixin() , new TimerFlag());

        logger.info( "=== Tic-Tac-Toe ===" );
        Expression aMove = turnTakingFuzzySearch.pickMove(game.getInitialNode());

        assertNotNull ( aMove                              );
        assertTrue    ( aMove instanceof Predicate         );
        logger.info( aMove );
    }


}
