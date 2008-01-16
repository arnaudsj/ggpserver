package de.tu_dresden.inf.ggp06_2.resolver.structures;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;

public class GameStateTreeTest {

    GameStateTree   staticTree;
    GameState       state1; // root
    GameState       state2;
    GameState       state3; // terminal
    GameState       state4; // goal
    GameState       state5;
    GameState       state6;

    GameNode        root;
    GameNode        stateNode2, stateNode4;

    @Before
    public void setUp() throws Exception {

        // creating some nodes
        state1 = new GameState( new ExpressionList( new Atom("A") ) );
        state2 = new GameState( new ExpressionList( new Atom("B") ) );
        state3 = new GameState( new ExpressionList( new Atom("C") ) );
        state4 = new GameState( new ExpressionList( new Atom("D") ) );
        state5 = new GameState( new ExpressionList( new Atom("E") ) );
        state6 = new GameState( new ExpressionList( new Atom("F") ) );

        state3.setTerminal();
        state4.setGoal();

        // building up the tree
        staticTree = new GameStateTree(state1);
        root = staticTree.getRootNode();
        stateNode2 = staticTree.addChild( state2, root,   null );
        staticTree.addChild( state3, root,   null );
        stateNode4 = staticTree.addChild( state4, stateNode2, null );
        staticTree.addChild( state5, stateNode2, null );
        staticTree.addChild( state6, stateNode2, null );
    }

    @Test
    public final void testAddChild() {
        staticTree.addChild( new GameState(
                                     new ExpressionList( new Atom("G") ) ),
                             root,
                             null );

        assertEquals(3, staticTree.getChildrenCount( root ));
    }

    @Test
    public final void testGetChildren() {
        assertEquals(3, staticTree.getChildrenCount( stateNode2 ));

        List<GameState> test = new ArrayList<GameState>();
        test.add( state4 );
        test.add( state5 );
        test.add( state6 );

        List<GameNode> children = staticTree.getChildren( stateNode2 );
        assertEquals( test.size(), children.size() );
        for (GameNode aNode : children)
            assertTrue( test.contains( aNode.getState() ) );
    }

    @Test
    public final void testGetTerminalStates() {

        List<GameState> test = new ArrayList<GameState>();
        test.add( state3 );

        assertEquals( test, staticTree.getTerminalStates() );
    }

    //Test
    //public final void testGetGoalStates() {
    //
    //    List<GameState> test = new ArrayList<GameState>();
    //    test.add( state4 );
    //
    //    assertEquals( test, staticTree.getGoalStates() );
    //}

    @Test
    public final void testGetRoot() {
        assertEquals( state1, staticTree.getRoot() );
    }

    @Test
    public final void testGetChildrenCount() {
        assertTrue(staticTree.getChildrenCount( stateNode4 ) == 0);
    }
}
