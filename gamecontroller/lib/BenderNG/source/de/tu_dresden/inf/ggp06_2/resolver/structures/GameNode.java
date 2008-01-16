package de.tu_dresden.inf.ggp06_2.resolver.structures;

import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;

/**
 * The GameNode class represents nodes in the GameStateTree. It contains a
 * game state and information corresponding to the node for the tree handling.
 * 
 * @author Ingo Keller
 */
public class GameNode {
    
    /**
     * This attribute contains the game state.
     */
    private GameState state;

    /**
     * This attribute contains the list of moves from the parent to the child.
     */
    private final ExpressionList moves;

    /**
     * This attribute contains the depth of the node within the game state tree.
     */
    private final int depth;

    /**
     * This attribute contains the hash code of the game state which was given
     * to the constructor. Default value is 0.
     */
    private final int sHashCode;

    /**
     * TODO: This attributes should be within the strategy classes
     */
    private int      searchDepth; // the depth to which this node was searched
    private double   nodeValue;   // heuristic (or not) value of the node

    /**
     * This constructor is for a root node of a GameStateTree.
     * @param newState
     */
    public GameNode ( GameState newState ) {
        state       = newState;
        sHashCode   = newState.hashCode();
        depth       = 0;
        searchDepth = 0;
        moves       = new ExpressionList();
    }
    
    /**
     * This constructor should be used if the node is not a root node.
     * @param newState
     * @param moves
     * @param parent
     */
    public GameNode (GameState newState, GameNode parent, ExpressionList move) {
        state     = newState;
        sHashCode = newState.hashCode();
        moves     = move;
        depth     = parent.depth + 1;
    }

    public final int getDepth() {
        return depth;
    }

    public final double getNodeValue() {
        return nodeValue;
    }

    public final void setNodeValue(double nodeValue) {
        this.nodeValue = nodeValue;
    }

    public final int getSearchDepth() {
        return searchDepth;
    }

    public final void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    public final GameState getState() {
        return state;
    }

    /**
     * This method sets the game state of this node to \a state. The state is 
     * only setted to the node if it is the same than the one of the creation 
     * time.
     * 
     * @param state
     */
    public final void setState(GameState newState) {
        state = (sHashCode == newState.hashCode()) ? newState : state;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GameNode");
        sb.append(" [").append(depth).append(" ").append(this.getState()).append(" ").append(this.nodeValue).append("] ");
        return sb.toString();
    }

    public final ExpressionList getMoves() {
        return moves;
    }

}
