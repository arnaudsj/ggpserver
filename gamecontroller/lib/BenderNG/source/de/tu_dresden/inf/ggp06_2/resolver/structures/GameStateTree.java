package de.tu_dresden.inf.ggp06_2.resolver.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;

/**
 * The GameStateTree contains the tree of already resolved game nodes.
 * @author Ingo Keller
 */
public class GameStateTree {

    /* Mapping ParentNode -> [ChildNode]* */
    private final HashMap<GameNode, List<GameNode>> structure;

    /* Mapping: GameState.hashCode -> [Node]+ */
    public final HashMap<Integer, List<GameNode>>  gsHash2Nodes;

    /* List: GameState.hashCode if GameState == Terminal */
    private final List<Integer>                     terminalStates;

    /* List: GameState.hashCode if GameState == Goal */
    private final List<Integer>                     goalStates;

    /* Root Node of the whole tree */
    private final GameNode                          rootNode;

    /**
     * This constructor creates an empty game state tree.
     */
    public GameStateTree(GameState rootState) {
        structure      = new HashMap<GameNode, List<GameNode>>();
        gsHash2Nodes   = new HashMap<Integer,  List<GameNode>>();
        terminalStates = new ArrayList<Integer>();
        goalStates     = new ArrayList<Integer>();

        // add root to content mapping
        rootNode = new GameNode(rootState);
        ArrayList<GameNode> tmpList = new ArrayList<GameNode>();
        tmpList.add( rootNode );
        gsHash2Nodes.put( rootState.hashCode(), tmpList );

        // add to structure mapping
        structure.put( rootNode, new ArrayList<GameNode>() );
    }


    /**
     * This method adds a child to a parent.
     * @param parent
     * @param child
     */
    public GameNode addChild(GameState child, GameNode parent, ExpressionList moves) {

        // without a child we do nothing
        if (child == null)
            return null;

        // create new node
        GameNode newNode = new GameNode(child, parent, moves);

        // we check the the content mapping
        if ( !gsHash2Nodes.containsKey( child.hashCode() ) ) {

            ArrayList<GameNode> tmpList = new ArrayList<GameNode>();
            tmpList.add( newNode );
            gsHash2Nodes.put( child.hashCode(), tmpList);

            if ( child.isGoal() )
                goalStates.add( child.hashCode() );

            if ( child.isTerminal() )
                terminalStates.add( child.hashCode() );

        } else {
            gsHash2Nodes.get( child.hashCode() ).add( newNode );
        }

        // we check the presence within structure mapping
        if ( !structure.containsKey(newNode) )
            structure.put( newNode, new ArrayList<GameNode>() );

        // finally we add the link between parent and child
        structure.get(parent).add(newNode);

        return newNode;
    }


    /**
     * This method returns a list of known children of the given game state. The
     * list can contain null values if a particular game state already went to
     * the bin.
     * @param parent
     * @return
     */
    public List<GameNode> getChildren(GameNode parent) {
        List<GameNode> tmpList = new ArrayList<GameNode>();
        tmpList.addAll( structure.get(parent) );
        return tmpList;
    }


    /**
     * This methods returns all terminal states within the game tree.
     * @return
     */
    public List<GameState> getTerminalStates() {
        List<GameState> tmpList = new ArrayList<GameState>();
        for (Integer gsHash : terminalStates)
            tmpList.add( gsHash2Nodes.get(gsHash).get(0).getState() );
        return tmpList;
    }


    /**
     * This methods returns all goal states within the game tree.
     * @return
     */
    public final GameState[] getGoalStates() {
        GameState[] tmpArray = new GameState[goalStates.size()];
        int         index    = 0;
        for (Integer gsHash : goalStates)
            tmpArray[index++] = gsHash2Nodes.get(gsHash).get(0).getState();
        return tmpArray;
    }

    /**
     * This method returns the initial game state.
     * @return
     */
    public GameState getRoot() {
        return rootNode.getState();
    }

    /**
     * This method returns the initial game state.
     * @return
     */
    public GameNode getRootNode() {
        return rootNode;
    }

    /**
     * This method returns the number of children of a game state.
     */
    public int getChildrenCount(GameNode node) {
        return structure.containsKey(node) ?
                                        structure.get(node).size() :
                                        0;
    }

    /**
     * This method returns whether we have a child node derived through moves.
     */
    public boolean hasNextNode(GameNode parent, ExpressionList move) {
        if ( structure.containsKey( parent) )
            for ( GameNode node : structure.get(parent) )
                if ( node.getMoves().equals( move ) )
                    return true;
        return false;
    }

    /**
     * This method returns whether we have a child node derived through moves.
     */
    public GameNode getNextNode(GameNode parent, ExpressionList move) {
        if ( structure.containsKey( parent) )
            for ( GameNode node : structure.get(parent) )
                if ( node.getMoves().equals( move ) )
                    return node;
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GameStateTree:");
        sb.append(" [").append(structure.toString()).append("] ");
        return sb.toString();
    }

    /**
     * This method iterates over all nodes known to the tree and returns the
     * nodes of the same depth as the given node.
     * @param node
     * @return
     */
    public Set<GameNode> getNodesOfSameDepth( GameNode node ) {
        Set<GameNode> nodes = new HashSet<GameNode>();
        int           depth = node.getDepth();
        for ( GameNode otherNode : structure.get(node) )

            if ( otherNode.getDepth() == depth && otherNode != node )
                nodes.add( otherNode );

        return nodes;
    }

    /**
     * This method returns a flat set of nodes that are below the given node.
     * @param node
     * @return
     */
    public Set<GameNode> getSubTreeNodes( GameNode node ) {
        Set<GameNode> nodes = new HashSet<GameNode>();

        // call getSubTreeNodes on children and add the result
        for (GameNode child : structure.get(node) )
            nodes.addAll(getSubTreeNodes(child));

        return nodes;
    }

    /**
     * This method returns the intersection of two game node sets.
     * @param set1
     * @param set2
     * @return
     */
    public Set<GameNode> getIntersection( Set<GameNode> set1,
                                          Set<GameNode> set2 ) {
        Set<GameNode> intersect = new HashSet<GameNode>();
        for (GameNode node : set1)
            if ( set2.contains(node) )
                intersect.add( node );
        return intersect;
    }

}
