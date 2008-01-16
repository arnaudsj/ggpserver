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



public class SimultaneousFuzzySearch extends AbstractStrategy{

    private final FuzzySubstitution empty = new FuzzySubstitution();
    private final List<Expression>  guard = new ArrayList<Expression>();

    private TimerFlag                       flag;
    private int depthTreshold = 6;
    private Map<ExpressionList, Long> historyHeuristic;
    private int initialDepth = 1;
    private int availableBoosts = 5;
    private int ourRoleIndex;
    private StrategyMixin mixin;

    public SimultaneousFuzzySearch(Game game, String role, StrategyMixin mixin){
        super(game,role);
        historyHeuristic = new HashMap<ExpressionList, Long>();
        flag = new TimerFlag();
        ourRoleIndex = 0;
        for(Atom a : this.game.getRoleNames()){
            if(a.equals( this.role )) break;
            ourRoleIndex++;
        }
        this.mixin = mixin;
    }
    public SimultaneousFuzzySearch(Game game, String role, StrategyMixin mixin, TimerFlag flag){
        this(game, role, mixin);
        this.timerFlag = flag;
    }

    @Override
    public Expression pickMove(GameNode node){
        return doTheSearch( node );
    }

    private Expression doTheSearch(GameNode node) {
        Expression bestDirectMove;
        try {
            ExpressionList moves = this.game.getLegalMoves( role, node.getState(), timerFlag );
            if ( moves.size() == 1 )
                return moves.get(0);
            bestDirectMove  = moves.get( random.nextInt(moves.size())  );

        } catch (InterruptedException ex) {


            return new Predicate(Const.aDoes, role, Const.aNoop);
        }

        int incr = 1;
        double nodeValue =0.0;
        while ( !(nodeValue == 1.0) && (incr < 11)){
            try{
                nodeValue = doSearchIteration(node, incr*initialDepth);
                    //logger.info( "tmpChildValue: "+tmpChildValue+" for "+nextNode+" to depth "+(initialDepth+incr) );
                for(GameNode aNode : this.game.stateTree.getChildren( node )){
                    if(aNode.getNodeValue() == node.getNodeValue()){
                        bestDirectMove = aNode.getMoves().get( ourRoleIndex );
                        break;
                    }
                }
                } catch (InterruptedException ex) {


                    return bestDirectMove;
                }
            incr++;
        }
        for(GameNode aNode : this.game.stateTree.getChildren( node )){
            if(aNode.getNodeValue() == node.getNodeValue()){
                bestDirectMove = aNode.getMoves().get( ourRoleIndex );
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
        if (node.getNodeValue() == 1.0) return 1.0;
        if (node.getState().isTerminal()){
            //logger.info( " terminal case " );
            value = heurisicValue(node, this.role);
            storeNodeValue(node, depth, value);
        } else if (wasPriorVisited(node)) {
            value = priorValue(node);
        } else if (0 == depth){
            //logger.info( " 0==depth case " );
            value = heurisicValue(node, this.role);
            storeNodeValue(node, depth, value);
        } else {

            Set<ExpressionList> moves = new HashSet<ExpressionList>();
            List<ExpressionList> legalMoves = this.game.getCombinedLegalMoves( node.getState(), this.flag);
            moves.addAll( legalMoves );
            value = processAvailableMoves( node, depth, moves, legalMoves );
            if (depth == initialDepth) {
                //logger.info( " expansion case for : "+depth+" value "+value );
            }
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
            Set<ExpressionList> moves, List<ExpressionList> legalMoves)
    throws InterruptedException {
        double currentBest = -1;
        ExpressionList currentBestMove = legalMoves.get( 0 );
        int childOrder = moves.size();
        HashMap<ExpressionList, Double>childrenValues = new HashMap<ExpressionList, Double>();
        while( ! moves.isEmpty()){
            ExpressionList aMove = extractMoveAccordingToHH(moves);
            GameNode nextNode = this.game.produceNextNode( node, aMove, timerFlag );
            boolean boostingPerformed = false;
            if ( depth == 1 &&
                    node.getDepth() > this.depthTreshold &&
                    availableBoosts > 0){
                depth = childOrder*initialDepth;
                availableBoosts--;
                boostingPerformed = true;
                //logger.info(" boosting! ");
            }
            childrenValues.put( aMove, doSearchIteration( nextNode, depth-1 ));
            //logger.info( "tmpValue: "+tmpValue );
            if ( boostingPerformed ){
                availableBoosts++;
            }
            currentBest = chooseBestMove(childrenValues);
            if (1.0 == currentBest){
                break;
            }
            childOrder--;
        }
        node.getState().setFuzzyValueForRole( role, currentBest );
        updateHistoryHeuristic(currentBestMove, depth*availableBoosts);
        return currentBest;
    }

    private double chooseBestMove(HashMap<ExpressionList, Double> childrenValues) {
        HashMap<Expression, Double> maximizeMe = new HashMap<Expression, Double>();
        double bestValue = -1000.0;
        Set<ExpressionList> keySet = childrenValues.keySet();
        for (ExpressionList ourMoves : keySet){
            if(maximizeMe.containsKey( ourMoves.get( ourRoleIndex ) )){
                if(maximizeMe.get( ourMoves.get( ourRoleIndex ) ) > childrenValues.get( ourMoves ))
                    maximizeMe.put( ourMoves.get( ourRoleIndex ), childrenValues.get( ourMoves ) );
            } else{
                maximizeMe.put( ourMoves.get( ourRoleIndex ), childrenValues.get( ourMoves ) );
            }
        }
        Set<Expression> keySet2 = maximizeMe.keySet();
        for(Expression aMove: keySet2){
            if (maximizeMe.get( aMove ) > bestValue)
                bestValue = maximizeMe.get( aMove );
        }

        return bestValue;
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

    private double heurisicValue(GameNode node, Atom player)
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
        if (!state.containsFuzzyValueForRole( player )){
            double fuzzyValue = fuzzyEvaluateStateForRole(player, state, scope, terminalFuzzyValue);
            state.setFuzzyValueForRole( player, fuzzyValue );
            state.flushFuzzyMemorizations();
            return fuzzyValue;
        }
        return state.getFuzzyValueForRole( player );
    }

    private double fuzzyEvaluateStateForRole( Atom           role,
                                              GameState      state,
                                              GameStateScope scope,
                                              double         terminalFuzzyValue)
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
                    }  else {
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

    private double evaluateOneGoal( Atom           role,
                                    GameState      state,
                                    GameStateScope scope,
                                    double         terminalFuzzyValue,
                                    Predicate      aGoal,
                                    int            value )
    throws InterruptedException {
        if (timerFlag.interrupted()) throw Const.interrupt;

        boolean reached = state.isRoleGoalValue(role) &&
                          state.getRoleGoalValue(role) == value ;

        mixin.preStateEvaluation(state, game, timerFlag);
        FuzzyResolution resolution = aGoal.fuzzyEvaluate( empty, scope, guard, timerFlag);
        double gv_z = resolution.getFuzzyValue();

        if (reached)
            return Expression.tConorm( gv_z, terminalFuzzyValue );

        return Expression.tNorm( gv_z, terminalFuzzyValue );
    }

}
