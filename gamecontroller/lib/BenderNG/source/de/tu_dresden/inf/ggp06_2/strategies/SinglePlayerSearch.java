package de.tu_dresden.inf.ggp06_2.strategies;
import java.util.*;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;



public class SinglePlayerSearch extends AbstractStrategy {
    
    private Map<Expression, Long> historyHeuristic; 

    private int depthTreshold   = 4;
    private int depthIncrement  = 1;
    private int availableBoosts = 3;
    private boolean someGoalReached;
    public  int iteration;
    
    public SinglePlayerSearch(Game game, String role){
        super(game, role);
        historyHeuristic = new HashMap<Expression, Long>();
        someGoalReached  = false;
    }
    
    public SinglePlayerSearch(Game game, String role, TimerFlag flag){
        this(game, role);
        this.timerFlag = flag;
    }
    
    public boolean isSomeGoalReached() {
        return someGoalReached;
    }

    @Override
    public Expression pickMove(GameNode node){
        return doTheSearch( node );
    }
    
    private Expression doTheSearch(GameNode node) {
        Expression bestDirectMove;
        try {
            bestDirectMove = game.getLegalMoves(role, node.getState(), timerFlag).get(0);
        } catch (InterruptedException ex) {
            return new Predicate(Const.aDoes, role, Const.aNoop);
        }
        
        double nodeValue = node.getNodeValue();
        for ( GameNode aNode : game.stateTree.getChildren(node) )
            if ( aNode.getNodeValue() == nodeValue ) {
                bestDirectMove = aNode.getMoves().get( 0 );
                break;
            }

        int incr  = 1;
        nodeValue = 0.0;
        try {
            while ( !(nodeValue == 1.0) ) {
                nodeValue = doSearchIteration( node, incr * depthIncrement );
                List<GameNode> nodeList = game.stateTree.getChildren(node);
                for ( GameNode aNode : nodeList )
                    if ( node.getNodeValue() == aNode.getNodeValue() ) {
                        bestDirectMove = aNode.getMoves().get( 0 );
                        break;
                    }
                incr++;
            }
        } catch (InterruptedException ex) {
            return bestDirectMove;
        }               

        for ( GameNode aNode : game.stateTree.getChildren(node) )
            if ( aNode.getNodeValue() == nodeValue ) {
                bestDirectMove = aNode.getMoves().get(0);
                break;
            }

        return bestDirectMove;
    }

    private double doSearchIteration( GameNode node, int depth ) 
    throws InterruptedException {
        iteration++;
        
        if ( node.getNodeValue() == 1.0 ) 
            return 1.0;
        
        double value = 0.0;

        if ( node.getState().isTerminal() ) {
            value = heuristicValue(node);
            storeNodeValue(node, depth, value);

        } else if ( wasPriorVisited(node) ) {
            value = priorValue(node);

        } else if (0 == depth) {
            value = heuristicValue(node);
            storeNodeValue(node, depth, value);
        
        } else {
            Set<Expression> moves = new HashSet<Expression>();
            ExpressionList legalMoves = game.getLegalMoves( role, node.getState(), timerFlag);
            for (Expression exp : legalMoves)
                moves.add( exp );
            value = processAvailableMoves( node, depth, moves, legalMoves );
            storeNodeValue(node, depth, value);
        }

        return value;
    }

    private double priorValue(GameNode node) {
        List<GameNode> nodes = game.stateTree.gsHash2Nodes.get( node.getState().hashCode() );
        int nodeDepth = node.getSearchDepth();
        for (GameNode aNode : nodes)
            if ( aNode.getSearchDepth() > nodeDepth )
                return aNode.getNodeValue();
        
        return node.getNodeValue();        
    }

    private void storeNodeValue(GameNode node, int depth, double value) {
        node.setSearchDepth( depth );
        node.setNodeValue( value );
    }

    private boolean wasPriorVisited(GameNode node) {
        List<GameNode> nodes = game.stateTree.gsHash2Nodes.get( node.getState().hashCode() );
        int nodeDepth = node.getSearchDepth();
        for (GameNode aNode : nodes)
            if ( aNode.getSearchDepth() > nodeDepth )
                return true;

        return false;
    }

    
    private double processAvailableMoves( GameNode        node, 
                                          int             depth, 
                                          Set<Expression> moves, 
                                          ExpressionList  legalMoves ) 
    throws InterruptedException {
        
        double     tmpValue, currentBest = -1;
        Expression currentBestMove       = legalMoves.get(0);
        int        childOrder            = moves.size();

        while( !moves.isEmpty() ) {

            Expression aMove    = extractMoveAccordingToHH(moves);
            GameNode   nextNode = game.produceNextNode( node, 
                                                        new ExpressionList(aMove), 
                                                        timerFlag );
            boolean boostingPerformed = false;

            if ( depth == 1 && 
                 node.getDepth() > depthTreshold &&
                 availableBoosts > 0 ) {
                depth = childOrder * depthIncrement;
                availableBoosts--;
                boostingPerformed = true;
            }
            
            tmpValue = doSearchIteration( nextNode, depth-1 );

            if ( boostingPerformed )
                availableBoosts++;
            
            if ( tmpValue > currentBest ){
                currentBest = tmpValue;
                currentBestMove = aMove;
            }
            
            if ( 1.0 == currentBest )
                break;
            
            childOrder--;
        }

        updateHistoryHeuristic( currentBestMove, 
                                node.getDepth() - game.stateTree.getRootNode().getDepth() );
        return currentBest;
    }

    private Expression extractMoveAccordingToHH(Set<Expression> moves) {
        Iterator<Expression> iter   = moves.iterator();
        Expression extractedMove    = iter.next();
        long hhValueOfExtractedMove = historyHeuristic.containsKey(extractedMove) ?
                                          historyHeuristic.get(extractedMove) : 
                                          0;

        while ( iter.hasNext() ) {
            Expression iteratedMove = iter.next();
            if ( historyHeuristic.containsKey(iteratedMove) ) {
                long hhValueOfIteratedMove = historyHeuristic.get(iteratedMove);
                if ( hhValueOfExtractedMove < hhValueOfIteratedMove ) {
                    extractedMove          = iteratedMove;
                    hhValueOfExtractedMove = hhValueOfIteratedMove;
                }
            }
        }

        moves.remove(extractedMove);
        return extractedMove;
    }

    private void updateHistoryHeuristic( Expression currentBestMove,
                                         int        importance ) {
        
        long increment = Math.round( Math.pow( importance, 2 ) );
        long i = historyHeuristic.containsKey(currentBestMove) ?
                     historyHeuristic.get(currentBestMove) :
                     0;

        historyHeuristic.put(currentBestMove, i + increment);
    }

    /**
     * This method returns a neuristic value for the game node.
     * @param node
     * @return Returns an int value from the interval [0.0, 1.0].
     */
    private final double heuristicValue(GameNode node) {
        GameState state = node.getState();
        if ( !state.isRoleGoalValue(role) )
            return 0.0;

        someGoalReached = true;
        return state.isTerminal() ? state.getRoleGoalValue(role) / 100.0 :
                                   (state.getRoleGoalValue(role) - 1.0) / 100.0;
    }
}
