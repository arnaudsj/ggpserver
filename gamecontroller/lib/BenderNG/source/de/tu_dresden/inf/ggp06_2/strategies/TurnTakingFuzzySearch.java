package de.tu_dresden.inf.ggp06_2.strategies;
import java.util.*;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;
import de.tu_dresden.inf.ggp06_2.strategies.mixins.StrategyMixin;


public class TurnTakingFuzzySearch extends AbstractStrategy{

    private final FuzzySubstitution empty = new FuzzySubstitution();
    private final List<Expression>  guard = new ArrayList<Expression>();

    private TimerFlag                       flag;
    private int depthTreshold = 6;
    private Map<ExpressionList, Long> historyHeuristic;
    private int depthIncrement = 1;
    private int initialDepth = 0;
    private int availableBoosts = 5;
    private int ourTurn;
    private int numberOfPlayers;
    private StrategyMixin mixin;

    public TurnTakingFuzzySearch(Game game, String role, StrategyMixin mixin){
        super(game,role);
        historyHeuristic = new HashMap<ExpressionList, Long>();
        flag = new TimerFlag();
        //ourTurn = this.game.getOwnTurn(this.role);
        ourTurn = 1;
        numberOfPlayers = this.game.getRoleNames().size();
        this.mixin = mixin;
    }
    public TurnTakingFuzzySearch(Game game, String role, StrategyMixin mixin, TimerFlag flag){
        this(game, role, mixin);
        this.timerFlag = flag;
    }

    @Override
    public Expression pickMove(GameNode node){
        return doTheSearch( node );
    }

    /*private Expression doTheSearch(GameNode node) {
        double tmpChildValue, bestChildNodeValue = -1;
        Expression bestDirectMove  = new Predicate(Const.aDoes, role, Const.aNoop);
        ExpressionList legalMoves;
        try {
            legalMoves = this.game.getLegalMoves( this.role, node.getState(), this.flag);
        }
        catch ( InterruptedException e ) {
            return bestDirectMove;
        }
        int incr = 0;
        while (incr < 40 ){
            Set<Expression> moves = new HashSet<Expression>();
            moves.addAll( legalMoves );
            while( ! moves.isEmpty()){
                Expression aMove = extractMoveAccordingToHH(moves);
                try {
                    GameNode nextNode = this.game.produceNextNode(
                            node, new ExpressionList(aMove), timerFlag );
                    tmpChildValue = doSearchIteration(nextNode, initialDepth+incr);
                    //logger.info( "tmpChildValue: "+tmpChildValue+" for "+nextNode+" to depth "+(initialDepth+incr) );
                } catch (InterruptedException ex) {
                    return bestDirectMove;
                }
                if (tmpChildValue > bestChildNodeValue){
                    bestChildNodeValue = tmpChildValue;
                    bestDirectMove = aMove;
                    if (1.0 == bestChildNodeValue){
                        return bestDirectMove;
                    }
                }
            }
            incr++;
            updateHistoryHeuristic( bestDirectMove, availableBoosts*(initialDepth + 1));
        }
        return bestDirectMove;
    }
*/
    private Expression doTheSearch(GameNode node) {
        Expression bestDirectMove;
        try{
            ExpressionList moves = this.game.getLegalMoves( role, node.getState(), timerFlag );
            if ( moves.size() == 1 )
                return moves.get(0);
            bestDirectMove  = moves.get( random.nextInt(moves.size())  );

        } catch (InterruptedException ex) {
            System.err.println("Timer Interrupt");

            return new Predicate(Const.aDoes, role, Const.aNoop);
        }
        int incr = 1;
        double nodeValue =0.0;
        while ( !(nodeValue == 1.0) ){
            try{
                for(GameNode aNode : this.game.stateTree.getChildren( node )){
                    if(aNode.getNodeValue() == node.getNodeValue()){
                        bestDirectMove = aNode.getMoves().get( 0 );
                        break;
                    }
                }
                nodeValue = doSearchIteration(node, incr*depthIncrement, -1000.0, 1000.0);
                //logger.info( ""+this.game.stateTree );
                    //logger.info( "tmpChildValue: "+tmpChildValue+" for "+nextNode+" to depth "+(initialDepth+incr) );
                for(GameNode aNode : this.game.stateTree.getChildren( node )){
                    if(aNode.getNodeValue() == node.getNodeValue()){
                        bestDirectMove = aNode.getMoves().get( 0 );
                        break;
                    }
                }
                } catch (InterruptedException ex) {


                    return bestDirectMove;
                }
            incr++;
        }
        return bestDirectMove;
    }

    private double doSearchIteration(
            GameNode node, int depth, double alpha, double beta)
    throws InterruptedException {

        double value = 0.0;
        if(node.getNodeValue() == 1.0){
            return 1.0;
        }
        if (node.getState().isTerminal()){
            //logger.info( " terminal case " );
            value = heurisicValue(node);
            storeNodeValue(node, depth, value);
        //} else if (wasPriorVisited(node)) {
          //  value = priorValue(node);
        } else if (0 == depth){
            //logger.info( " 0==depth case " );
            value = heurisicValue(node);
            storeNodeValue(node, depth, value);
        } else {

            Set<ExpressionList> moves = new HashSet<ExpressionList>();
            List<ExpressionList> legalMoves = this.game.getCombinedLegalMoves(
                    node.getState(), this.flag);
            moves.addAll( legalMoves );
//            value = processAvailableMoves( node, depth, moves, legalMoves, alpha, beta );
            double tmpValue, currentBest = -2000.0;
            ExpressionList currentBestMove = legalMoves.get( 0 );
            int childOrder = moves.size();


            while( ! moves.isEmpty()){
                ExpressionList aMove = extractMoveAccordingToHH(moves);
                GameNode nextNode = this.game.produceNextNode( node, aMove, this.timerFlag );
                boolean boostingPerformed = false;
                if ( depth == 1 &&
                        node.getDepth() > this.depthTreshold &&
                        availableBoosts > 0){
                    depth = childOrder*initialDepth;
                    availableBoosts--;
                    boostingPerformed = true;
                    //logger.info(" boosting! ");
                }
                if (((node.getDepth() % numberOfPlayers == ourTurn) ||
                        (node.getDepth() % numberOfPlayers == (ourTurn-1+numberOfPlayers)%numberOfPlayers))
                        && (node.getDepth() > 0)){
                    tmpValue = - doSearchIteration( nextNode, depth-1, -beta, -alpha );
                }else{
                    tmpValue = doSearchIteration( nextNode, depth-1, alpha, beta );
                }
                /*
                i = (i>0)? i-1: 0;
                if (result > score) score = result;
                if (score >= beta){
                    bestMove = key;
                    break;
                }
                alpha = (alpha > score)? alpha : score;
            }
                 */
                //logger.info( "tmpValue: "+tmpValue );
                if ( boostingPerformed ){
                    availableBoosts++;
                }
                if ( tmpValue > currentBest ){
                    currentBest = tmpValue;
                }
                if (currentBest >= beta){
                    currentBestMove = aMove;
                    break;
                }
                alpha = (alpha > currentBest)? alpha : currentBest;
                childOrder--;
            }
            node.getState().setFuzzyValueForRole( role, currentBest );

            updateHistoryHeuristic(currentBestMove, depth*availableBoosts);
            value = currentBest;
            if (depth == initialDepth) {
                //logger.info( " expansion case for : "+depth+" value "+value );
            }
            storeNodeValue(node, depth, value);
        }
        //logger.info( node );
        return value;
    }

    private void storeNodeValue(GameNode node, int depth, double value) {
        node.setSearchDepth( depth );
        node.setNodeValue( value );
    }

    private ExpressionList extractMoveAccordingToHH(Set<ExpressionList> moves) {
        Iterator<ExpressionList> iter = moves.iterator();
        ExpressionList extractedMove = iter.next();
        long hhValueOfExtractedMove = (historyHeuristic.containsKey( extractedMove )) ?
                historyHeuristic.get( extractedMove ) : 0;
        while (iter.hasNext()){
            ExpressionList iteratedMove = iter.next();
            if (historyHeuristic.containsKey( iteratedMove )){
                long hhValueOfIteratedMove = historyHeuristic.get( iteratedMove );
                if (hhValueOfExtractedMove < hhValueOfIteratedMove){
                    extractedMove = iteratedMove;
                    hhValueOfExtractedMove = hhValueOfIteratedMove;
                }
            }
        }
        moves.remove( extractedMove );
        return extractedMove;
    }

    private void updateHistoryHeuristic(ExpressionList currentBestMove, int importance) {
        long increment = Math.round( Math.pow( importance, 2 ) );
        if (historyHeuristic.containsKey( currentBestMove )){
            long i = historyHeuristic.get( currentBestMove );
            historyHeuristic.put( currentBestMove, i+increment );
        } else {
            historyHeuristic.put(currentBestMove, increment);
        }
    }

    private double heurisicValue(GameNode node)
        throws InterruptedException{

        GameState state = node.getState();

       if (timerFlag.interrupted()) throw new InterruptedException();

        double terminalFuzzyValue = 0.0;
        // First of all get terminal value, which does not
        // depend on any role
        GameStateScope scope = new GameStateScope(game.getTheory(), state);

        FuzzyResolution terminal = Const.aTerm.fuzzyEvaluate( empty, scope, guard, timerFlag);
        terminalFuzzyValue = terminal.getFuzzyValue();


        // Then evaluate current state for our role
        if (!state.containsFuzzyValueForRole( role )){
            double fuzzyValue = fuzzyEvaluateStateForRole(role, state, scope, terminalFuzzyValue);
            state.setFuzzyValueForRole( role, fuzzyValue );
            state.flushFuzzyMemorizations();
            return fuzzyValue;
        }
        return state.getFuzzyValueForRole( role );

    }

    private double fuzzyEvaluateStateForRole(
            Atom role, GameState state,
            GameStateScope scope, double terminalFuzzyValue)
    throws InterruptedException {
        List<Predicate> goals = game.getGoalsForRole( role );
        double allGoalValues = 0;
        double allGoalsFuzzyValues = 0;
        for(Predicate aGoal : goals) {
            Expression eGoalValue = aGoal.getOperands().get( 1 );
            if ( eGoalValue instanceof Atom ) {
                Atom aGoalValue = (Atom) eGoalValue;
                int value = Integer.parseInt( aGoalValue.toString() );
                if ( 0 != value ){
                    allGoalValues += value;
                    double oneGoalFuzzyValue = evaluateOneGoal( role, state, scope,
                            terminalFuzzyValue, aGoal, value );
                    oneGoalFuzzyValue = oneGoalFuzzyValue * value * 0.01;
                    allGoalsFuzzyValues = (0 == allGoalsFuzzyValues ) ?
                            oneGoalFuzzyValue :
                                Expression.tConorm( allGoalsFuzzyValues, oneGoalFuzzyValue );
                }

            } else if (eGoalValue instanceof Variable){
                mixin.preStateEvaluation(state, game, timerFlag);
                FuzzyResolution resolution = aGoal.fuzzyEvaluate( empty, scope, guard, timerFlag);
                for(FuzzySubstitution aSub : resolution){
                    Predicate newGoal = (Predicate) aGoal.apply( aSub );
                    Expression aNewGoalValue = newGoal.getOperands().get( 1 );
                    if (aNewGoalValue instanceof Atom){
                        Atom aGoalValue = (Atom) aNewGoalValue;
                        int value = Integer.parseInt( aGoalValue.toString() );
                        if ( 0 != value ){
                            allGoalValues += value;
                            double oneGoalFuzzyValue = aSub.getFuzzyValue();
                            if (state.isRoleGoalValue( role ) &&
                                    state.getRoleGoalValue( role ) == value) {
                                 oneGoalFuzzyValue = Expression.tConorm( oneGoalFuzzyValue, terminalFuzzyValue );
                            } else {
                                oneGoalFuzzyValue = Expression.tNorm( oneGoalFuzzyValue, terminalFuzzyValue );
                            }


                            oneGoalFuzzyValue = oneGoalFuzzyValue * value * 0.01;
                            allGoalsFuzzyValues = (0 == allGoalsFuzzyValues ) ?
                                    oneGoalFuzzyValue :
                                        Expression.tConorm( allGoalsFuzzyValues, oneGoalFuzzyValue );
                        }
                    } else {
                        double oneGoalFuzzyValue = aSub.getFuzzyValue();
                        allGoalsFuzzyValues = (0 == allGoalsFuzzyValues ) ?
                                oneGoalFuzzyValue :
                                    Expression.tConorm( allGoalsFuzzyValues, oneGoalFuzzyValue );
                    }
                }
            }

        }
        double fuzzyValue = allGoalsFuzzyValues / allGoalValues;
        return fuzzyValue;
    }

    private double evaluateOneGoal(
            Atom role, GameState state, GameStateScope scope,
            double terminalFuzzyValue, Predicate aGoal, int value)
    throws InterruptedException {
        boolean reached = state.isRoleGoalValue( role ) &&
        state.getRoleGoalValue( role ) == value ;
        double gv_z = 0.0;
        if (timerFlag.interrupted()) throw Const.interrupt;

        mixin.preStateEvaluation(state, game, timerFlag);
        FuzzyResolution resolution = aGoal.fuzzyEvaluate( empty, scope, guard, timerFlag);
        gv_z = resolution.getFuzzyValue();

        if (reached) {
            return Expression.tConorm( gv_z, terminalFuzzyValue );
        }
        return Expression.tNorm( gv_z, terminalFuzzyValue );
    }

}


/*package de.tu_dresden.inf.ggp06_2.strategies;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import static java.util.Collections.sort;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;



public class TurnTakingSearch extends AbstractStrategy{
    private static final Logger logger = Logger.getLogger(Novelty.class);

    private HashMap<ExpressionList, Integer> historyHeuristic;

    private TimerFlag                       flag;

    private int depthTreshold = 8;
    private int iteratedDeepeningStep = 2;
    private int searchDepthCounter = 1;
    private HashMap<GameNode, Double> nodeValues;
    private int numberOfPlayers;
    private int ourTurn;

    private void calculateOurTurn(){
        GameState state;
        int index;
        for (index = 0; index<this.game.getRoleNames().size(); index++)
            if( this.game.getRoleNames().get( index ).equals( this.role ))
                break;
        state = this.game.getInitialNode().getState();
        for(ourTurn=0; ourTurn < numberOfPlayers; ourTurn++){
            ExpressionList moves =
                this.game.getCombinedLegalMoves( state, flag ).get( 0 );
            if(!(moves.get(index).equals( Const.aNoop )))
                break;
            state = this.game.produceNextNode( new GameNode(state), moves).getState();

        }
    }

    public TurnTakingSearch(Game game, String role){
        super(game,role);
        numberOfPlayers = this.game.getRoleNames().size();
        calculateOurTurn();
        historyHeuristic =
            new HashMap<ExpressionList, Integer>();
        flag = new TimerFlag();

    }

    private Double evaluatePossition(GameNode node){
        *//**
         * TODO: call a propper method
         *//*
        if(node.getState().isTerminal())
            return 1.0;
        return 0.0;
    }



    private Double doTheSearchIteration(GameNode node, int depth, Double alpha, Double beta){
        HashMap<ExpressionList, Integer> rating = new HashMap<ExpressionList, Integer>();

        //node.setVisited( node.getVisited()+1 );

        *//**
         * use transposition tables
         *//*
        for (GameNode sameState :
            this.game.stateTree.gsHash2Nodes.get( node.getState() )){
            if((sameState.getVisited() > node.getVisited()) &&
                    ((sameState.depth % this.game.getRoleNames().size())
                            == (sameState.depth % this.game.getRoleNames().size())) ){
                nodeValues.put( node, nodeValues.get( sameState ) );
                return nodeValues.get( sameState );
            }

        }


        Double score = evaluatePossition(node);

        if(node.getState().isTerminal()){
            node.setVisited( node.depth - depth );
            nodeValues.put( node, score );
            return score;
        }

        if(node.depth == depth){
            node.setVisited( node.depth - depth );
            nodeValues.put( node, score );
            return score;
        }

        List<ExpressionList> moves =
                    this.game.getCombinedLegalMoves( node.getState(), this.flag);
        if(moves.isEmpty()){
            node.getState().setTerminal();
            nodeValues.put( node, score );
            return score;
        }
        for(ExpressionList key : moves){
            if (historyHeuristic.containsKey( key )){
                rating.put( key, historyHeuristic.get( key ) );
            } else{
                historyHeuristic.put( key, 0 );
                rating.put( key, 0);
            }
        }
    *//**
     * TODO: Sort the moves
     *//*
        ArrayList<ExpressionList> movesList =
            new ArrayList<ExpressionList>();
        ArrayList<Integer> movesValues =
            new ArrayList<Integer>(rating.values());
        sort(movesValues);
        for(Integer val : movesValues){
            for (ExpressionList key : rating.keySet()){
                if((val == rating.get( key )) &&
                        (!movesList.contains( key ))){
                    movesList.add( key );
                }
            }
        }
        int i = 3;
        Double result = 0.0;
        score = -1000.0;
        boolean computed = false;
        ExpressionList bestMove = new ExpressionList(new Predicate(Const.aDoes, Const.aNoop));

        for(ExpressionList key : movesList){
            for(GameNode child : this.game.stateTree.getChildren( node ))
                if (child.moves.contains( key )){
                    if ((node.depth % numberOfPlayers == ourTurn) ||
                            (node.depth % numberOfPlayers == ourTurn-1)){
                        if (node.depth < depthTreshold){
                            result = -doTheSearchIteration(child, depth, -beta, -alpha);
                        }else result = -doTheSearchIteration(child, depth + i*iteratedDeepeningStep, -beta, -alpha);
                    }else{
                        if (node.depth < depthTreshold){
                            result = doTheSearchIteration(child, depth, alpha, beta);
                        }else result = doTheSearchIteration(child, depth + i*iteratedDeepeningStep, alpha, beta);
                    }
                    computed = true;
                    break;
                }
            if(!computed){
                GameNode newNode =
                        this.game.produceNextNode(
                                node, new ExpressionList(movesList.get( 0 )) );
                newNode = this.game.stateTree.addChild( newNode.getState(), node,
                            new ExpressionList(moves.get( 0 )) );
                if ((node.depth % numberOfPlayers == ourTurn) ||
                            (node.depth % numberOfPlayers == ourTurn-1)){
                    if (node.depth < depthTreshold){
                        result = -doTheSearchIteration(newNode, depth, -beta, -alpha);
                        }else result = -doTheSearchIteration(newNode, depth + i*iteratedDeepeningStep, -beta, -alpha);
                    }else{
                        if (node.depth < depthTreshold){
                            result = doTheSearchIteration(newNode, depth, alpha, beta);
                        }else result = doTheSearchIteration(newNode, depth + i*iteratedDeepeningStep, alpha, beta);
                    }
             }

            i = (i>0)? i-1: 0;
            if (result > score) score = result;
            if (score >= beta){
                bestMove = key;
                break;
            }
            alpha = (alpha > score)? alpha : score;
        }
        int value = historyHeuristic.get( bestMove );
        value += Math.round(Math.pow(2, node.depth));
        historyHeuristic.put( bestMove, value );
        node.setVisited( node.depth - depth );
        nodeValues.put( node, score );
        return score;
    }
    private Double doTheSearch(GameNode node){
        while(!flag.interrupted()){
            doTheSearchIteration(node, searchDepthCounter*iteratedDeepeningStep, -1000.0, 1000.0);
            searchDepthCounter++;
        }
        return nodeValues.get( node );
    }

    public Expression pickMove(GameNode node){
        if(this.game.stateTree.getRoot() == null){
            node = this.game.stateTree.addRootState( node.getState() );
        }
        Double bestNodeValue = doTheSearch(node);
        for (GameNode child : this.game.stateTree.getChildren( node )){
            if(this.nodeValues.get( child ) == bestNodeValue)
                return child.moves.get( 0 );
        }
        return new Predicate(Const.aDoes, Const.aNoop);
    }
}
*/
