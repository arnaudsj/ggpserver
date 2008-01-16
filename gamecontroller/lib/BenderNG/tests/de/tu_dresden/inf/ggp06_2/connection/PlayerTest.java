package de.tu_dresden.inf.ggp06_2.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;

public class PlayerTest {

    // The maze game from stanford
    final static String mazeGame = "(role robot) (init (cell a)) (init (gold c)) (init (step 1)) (<= (next (cell ?y)) (does robot move) (true (cell ?x)) (adjacent ?x ?y)) (<= (next (cell ?x)) (does robot grab) (true (cell ?x))) (<= (next (cell ?x)) (does robot drop) (true (cell ?x))) (<= (next (gold ?x)) (does robot move) (true (gold ?x))) (<= (next (gold i)) (does robot grab) (true (cell ?x)) (true (gold ?x))) (<= (next (gold i)) (does robot grab) (true (gold i))) (<= (next (gold ?y)) (does robot grab) (true (cell ?x)) (true (gold ?y)) (distinct ?x ?y)) (<= (next (gold ?x)) (does robot drop) (true (cell ?x)) (true (gold i))) (<= (next (gold ?x)) (does robot drop) (true (gold ?x)) (distinct ?x i)) (<= (next (step ?y)) (true (step ?x)) (succ ?x ?y)) (adjacent a b) (adjacent b c) (adjacent c d) (adjacent d a) (succ 1 2) (succ 2 3) (succ 3 4) (succ 4 5) (succ 5 6) (succ 6 7) (succ 7 8) (succ 8 9) (succ 9 10) (<= (legal robot move)) (<= (legal robot grab) (true (cell ?x)) (true (gold ?x))) (<= (legal robot drop) (true (gold i))) (<= (goal robot 100) (true (gold a))) (<= (goal robot 0) (true (gold ?x)) (distinct ?x a)) (<= terminal (true (step 10))) (<= terminal (true (gold a)))";

    // Atoms
    final static Atom aCell = new Atom("CELL");
    final static Atom aGold = new Atom("GOLD");
    final static Atom aStep = new Atom("STEP");


    // Predicates
    Predicate  trueCellA = new Predicate( Const.aTrue,
                                          new Predicate(
                                                  aCell,
                                                  new Atom("A") ) );

    Predicate  trueCellB = new Predicate( Const.aTrue,
                                          new Predicate(
                                                  aCell,
                                                  new Atom("B") ) );

    Predicate  trueCellC = new Predicate( Const.aTrue,
                                          new Predicate(
                                                  aCell,
                                                  new Atom("C") ) );

    Predicate  trueCellD = new Predicate( Const.aTrue,
                                          new Predicate(
                                                  aCell,
                                                  new Atom("D") ) );

    /* Gold should be indicated by the 'gold' predicate, not the 'cell' one. */
    Predicate  trueGoldC = new Predicate( Const.aTrue,
                                          new Predicate(
                                                 aGold,
                                                 new Atom("C") ) );

    Predicate  trueStep1 = new Predicate( Const.aTrue,
                                          new Predicate(
                                                  aStep,
                                                  new Atom("1") ) );

    Predicate  trueStep2 = new Predicate( Const.aTrue,
                                          new Predicate(
                                                  aStep,
                                                  new Atom("2") ) );

    Predicate  trueStep3 = new Predicate( Const.aTrue,
                                          new Predicate(
                                                  aStep,
                                                  new Atom("3") ) );

    Player player;
    GameState  state0;
    GameState  state1;
    GameState  state2;

    @Before
    public void setUp() throws Exception {

        player = new Player();
        HashMap<Atom,ExpressionList> dummy;

        // model initial state
        dummy = new HashMap<Atom,ExpressionList>();
        dummy.put( aCell, new ExpressionList( new Expression[] {trueCellA} ) );
        dummy.put( aGold, new ExpressionList( new Expression[] {trueGoldC} ) );
        dummy.put( aStep, new ExpressionList( new Expression[] {trueStep1} ) );
        state0 = new GameState(dummy);

        // model state 1 after a move action by player
        dummy = new HashMap<Atom,ExpressionList>();
        dummy.put( aCell, new ExpressionList( new Expression[] {trueCellB} ) );
        dummy.put( aGold, new ExpressionList( new Expression[] {trueGoldC} ) );
        dummy.put( aStep, new ExpressionList( new Expression[] {trueStep2} ) );
        state1 = new GameState(dummy);

        // model state 2 after a move action by player
        dummy = new HashMap<Atom,ExpressionList>();
        dummy.put( aCell, new ExpressionList( new Expression[] {trueCellC} ) );
        dummy.put( aGold, new ExpressionList( new Expression[] {trueGoldC} ) );
        dummy.put( aStep, new ExpressionList( new Expression[] {trueStep3} ) );
        state2 = new GameState(dummy);

        // Initialize game
        player.commandStart( new Message( "(START match1 robot (" +
                                          mazeGame +
                                          ") 10 10)" ) );
    }

    @Test
    public final void testCommandStart() {

        // check if message parsing works
        assertEquals( new Atom(player.realMatch.info.getRole()),
                      new Atom("robot")  );

        // some checks if game description was read correctly
        assertEquals( player.realMatch.strategy.getGame().getInitialNode().getState(), state0 );

    }

    @Test
    public final void testCommandPlay() {

        // start game
        assertEquals( "MOVE",
                player.commandPlay( new Message("(PLAY match1 NIL)") ) );

        assertEquals( state0,
                player.realMatch.getState());

        // after step 1
        assertEquals( "MOVE",
                player.commandPlay( new Message("(PLAY match1 (MOVE))") ) );

        assertEquals( state1, player.realMatch.getState() );

        // after step 2
        String playerMove = player.commandPlay( new Message("(PLAY match1 (MOVE))") );
        assertTrue( "GRAB".equals( playerMove ) ||
                    "MOVE".equals( playerMove )
                );
        assertEquals( state2, player.realMatch.getState() );
    }

    @Test
    public final void testCommandStop() {

        // start game
        assertEquals( "MOVE",
                player.commandPlay( new Message("(PLAY match1 NIL)") ) );

        assertEquals( state0, player.realMatch.getState() );

        // stop step
        player.commandStop( new Message("(STOP match1 (MOVE))") );
        assertEquals( state1, player.realMatch.getState() );
    }
}
