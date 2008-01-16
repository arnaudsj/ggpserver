package de.tu_dresden.inf.ggp06_2.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class MobilityTest {

    private static final Logger logger = Logger.getLogger(MobilityTest.class);

    private static final Atom role     = new Atom("a");
    private static final Atom opponent = new Atom("b");
    private static final Atom a1       = new Atom("1");
    private static final Atom a4       = new Atom("4");
    private static final Atom pick     = new Atom("pick");
    private static final Atom pickedby = new Atom("pickedby");
    private static final Atom control  = new Atom("control");

    final Predicate pPick1 = new Predicate(pick, a1);

    final Predicate pPick3 = new Predicate(pick, new Atom("3"));

    final Predicate pPick5 = new Predicate(pick, new Atom("5"));

    final Predicate pPick7 = new Predicate(pick, new Atom("7"));

    final Predicate pPick9 = new Predicate(pick, new Atom("9"));

    final Expression pNoop = new Atom("noop");

    final Predicate pPickedByA1 = new Predicate(
            Const.aTrue, new Predicate(pickedby, role, a1));

    final Predicate pPickedByA4 = new Predicate(
            Const.aTrue, new Predicate(pickedby, role, a4));

    final Predicate pControlB = new Predicate(
            Const.aTrue, new Predicate(control, opponent));

    AbstractStrategy  mobility;
    GameNode gameNode;
    GameManager manager = new GameManager();

    @Before
    public void setUp(){
        ExpressionList gameRules = Parser.parseFile( "./testdata/mobility.kif" );
        Game game = manager.getGameByGDL( gameRules.toString() );
        mobility  = new Mobility( game, role.toString(), new TimerFlag() );
        gameNode = game.getInitialNode();
    }

    @Test
    public void pickMove(){
        Expression aMove = mobility.pickMove(gameNode);

        assertNotNull ( aMove                              );
        assertTrue    ( aMove instanceof Predicate         );

        Expression action = ((Predicate) aMove).getOperands().get( 1 );
        assertEquals  ( pick,  action.firstOperand()  );
        if ( logger.isTraceEnabled() )
             logger.trace(aMove);

        assertTrue( pPick1.equals( action ) ||
                    pPick3.equals( action ) ||
                    pPick5.equals( action ) ||
                    pPick7.equals( action ) ||
                    pPick9.equals( action ) );
    }
}
