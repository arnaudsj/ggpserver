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


public class SinglePlayerFuzzySearch extends AbstractStrategy{

    private final FuzzySubstitution empty = new FuzzySubstitution();
    private final List<Expression>  guard = new ArrayList<Expression>();

    private TimerFlag                       flag;
    private int depthTreshold = 4;
    private Map<Expression, Long> historyHeuristic;
    private int depthIncrement = 1;
    private int availableBoosts = 3;
    private StrategyMixin mixin;

    public SinglePlayerFuzzySearch(Game game, String role, StrategyMixin mixin){
        super(game,role);
        historyHeuristic = new HashMap<Expression, Long>();
        flag = new TimerFlag();
        this.mixin = mixin;
    }
    public SinglePlayerFuzzySearch(Game game, String role, StrategyMixin mixin, TimerFlag flag){
        this(game, role, mixin);
        this.timerFlag = flag;
    }

    @Override
    public Expression pickMove(GameNode node){
        return doTheSearch( node );
    }

    private Expression doTheSearch(GameNode node) {
        Expression bestDirectMove;
        try{
            ExpressionList moves = this.game.getLegalMoves( role, node.getState(), timerFlag );
            if ( moves.size() == 1 )
                return moves.get(0);
            bestDirectMove  = moves.get( random.nextInt(moves.size())  );

        } catch (InterruptedException ex) {


            return new Predicate(Const.aDoes, role, Const.aNoop);
        }
        for(GameNode aNode : this.game.stateTree.getChildren( node )){
            if(aNode.getNodeValue() == node.getNodeValue()){
                bestDirectMove = aNode.getMoves().get( 0 );
                break;
            }
        }
        int incr = 0;
        double nodeValue =0.0;
        while ( !(nodeValue == 1.0) ){
            try{

                nodeValue = doSearchIteration(node, incr*depthIncrement);
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
        for(GameNode aNode : this.game.stateTree.getChildren( node )){
            if(aNode.getNodeValue() == nodeValue){
                bestDirectMove = aNode.getMoves().get( 0 );
                break;
            }
        }

        return bestDirectMove;
    }

    private double doSearchIteration(
            GameNode node, int depth)
    throws InterruptedException {
        //logger.info( node );
        double value = 0.0;
        if (node.getState().isTerminal()){
            //logger.info( " terminal case " );
            value = heurisicValue(node);
            storeNodeValue(node, depth, value);
        } else if (wasPriorVisited(node)) {
            value = priorValue(node);
        } else if (0 == depth){
            //logger.info( " 0==depth case " );
            value = heurisicValue(node);
            storeNodeValue(node, depth, value);
        } else {

            Set<Expression> moves = new HashSet<Expression>();
            ExpressionList legalMoves = this.game.getLegalMoves( this.role, node.getState(), this.flag);
            for (Expression exp : legalMoves)
                moves.add( exp );
            value = processAvailableMoves( node, depth, moves, legalMoves );
            storeNodeValue(node, depth, value);
        }

        return value;
    }

    private double priorValue(GameNode node) {
        List<GameNode> nodes = game.stateTree.gsHash2Nodes.get( node.getState().hashCode() );
        for (GameNode aNode : nodes){
            int previousSearchDepth = aNode.getSearchDepth();
            if (previousSearchDepth > node.getSearchDepth())
                return aNode.getNodeValue();
        }
        return node.getNodeValue();
    }

    private void storeNodeValue(GameNode node, int depth, double value) {
        node.setSearchDepth( depth );
        node.setNodeValue( value );
    }

    private boolean wasPriorVisited(GameNode node) {
        List<GameNode> nodes = game.stateTree.gsHash2Nodes.get( node.getState().hashCode() );
        for (GameNode aNode : nodes){
            int previousSearchDepth = aNode.getSearchDepth();
            if (previousSearchDepth > node.getSearchDepth())
                return true;
        }
        return false;
    }

    private double processAvailableMoves(
            GameNode node, int depth,
            Set<Expression> moves, ExpressionList legalMoves)
    throws InterruptedException {

        double tmpValue, currentBest = -1;
        Expression currentBestMove = legalMoves.get( 0 );
        int childOrder = moves.size();

        while( ! moves.isEmpty()){
            Expression aMove = extractMoveAccordingToHH(moves);
            GameNode nextNode = this.game.produceNextNode( node, new ExpressionList(aMove), timerFlag );
            boolean boostingPerformed = false;
            if ( depth == 1 &&
                    node.getDepth() > this.depthTreshold &&
                    availableBoosts > 0){
                depth = childOrder*depthIncrement;
                availableBoosts--;
                boostingPerformed = true;
                //logger.info(" boosting! ");
            }
            tmpValue = doSearchIteration( nextNode, depth-1 );
            //logger.info( "tmpValue: "+tmpValue );
            if ( boostingPerformed ){
                availableBoosts++;
            }
            if ( tmpValue > currentBest ){
                currentBest = tmpValue;
                currentBestMove = aMove;
            }
            if (1.0 == currentBest){
                break;
            }
            childOrder--;
        }

        //node.getState().setFuzzyValueForRole( role, currentBest );
        updateHistoryHeuristic(currentBestMove, node.getDepth() - this.game.stateTree.getRootNode().getDepth());
        return currentBest;
    }

    private Expression extractMoveAccordingToHH(Set<Expression> moves) {
        Iterator<Expression> iter = moves.iterator();
        Expression extractedMove = iter.next();
        long hhValueOfExtractedMove = (historyHeuristic.containsKey( extractedMove )) ?
                historyHeuristic.get( extractedMove ) : 0;
        while (iter.hasNext()){
            Expression iteratedMove = iter.next();
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

    private void updateHistoryHeuristic(
            Expression currentBestMove, int importance) {
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
        //if (  state.isTerminal()){

        //} else if ( state.isRoleGoalValue( this.role )){
        //    if (state.isTerminal()){
        //        logger.info( " GOT solution! "+state +state.getRoleGoalValue( this.role )/100.0);
        //        return state.getRoleGoalValue( this.role )/100.0;
        //    }
          //  if (intermediateStateValues.containsKey(state)) {
          //      return intermediateStateValues.get( state );
          //  }
        //    logger.info( " GOAL state! "+state +state.getRoleGoalValue( this.role ));
        //    return (state.getRoleGoalValue( this.role ) - 1.0)/100.0;
        //}


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
