package de.tu_dresden.inf.ggp06_2.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import de.tu_dresden.inf.ggp06_2.gamedb.objects.GameInformation;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Implication;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.Term;
import de.tu_dresden.inf.ggp06_2.resolver.helper.ResolutionHelper;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.MovesScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.RuleScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.TheoryScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameStateTree;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

/**
 * The Game class contains global information for a single game that does not
 * change during a match.
 *
 * The information stored within a game object are as follows:
 * <ul>
 *   <li>the description</li>
 *   <li>the roles</li>
 *   <li>the goals</li>
 *   <li>the initial state</li>
 * </ul>
 *
 * Caching is done for roles, goals and the inital state. That means all of this
 * will be generated only once on the first call of the correspondig getter.
 *
 * @see RunnableMatch
 *
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 * @author Novak Novakovic - General Game Playing course student at TUD
 */
public final class Game {

    private static final Substitution empty = new Substitution();
    public GameInformation info;

    /* Game State Tree contains all game states */
    public final GameStateTree      stateTree;

    /**
     * All the templates of type
     *          <i>&quot; LEGAL &lt;role&gt; &lt;action&gt;&quot;</i>
     * are stored in internal hash map.
     */
    private final Map<Atom, Predicate> legalityTemplates;

    /**
     * All the templates of type
     *          <i>&quot; DOES &lt;role&gt; &lt;action&gt;&quot;</i>
     * are stored in internal hash map.
     */
    private final Map<Atom, Predicate> actionTemplates;

    /**
     * All the templates of type
     *          <i>&quot; GOAL &lt;role&gt; &lt;variable&gt;&quot;</i>
     * are stored in internal hash map.
     */
    private final Map<Atom, Predicate> goalTemplates;

    final  Theory          theoryObj;
    final  TheoryScope     theoryScope;

    /* cache variables */
    List<Atom>                      roleNames; // role names      ALICE
    Map<Atom, List<Predicate>>      goals;     // ( role, goal list )
    GameState                       initialState;

    /* TODO: game and match should not share the flags
     *      but this implies having a separate timer
     *      thread in each of those classes. Could we
     *      avoid this?
     */
    public boolean wasInterrupted = false;

    public Game(GameInformation gameInfo) {
        info              = gameInfo;
        roleNames         = new ArrayList<Atom>();
        Parser.parseGDL( info.getGdl() );
        theoryObj         = Parser.getFullTheory();

        theoryScope       = new TheoryScope(theoryObj);
        actionTemplates   = new HashMap<Atom, Predicate>();
        legalityTemplates = new HashMap<Atom, Predicate>();
        goalTemplates     = new HashMap<Atom, Predicate>();
        goals             = new HashMap<Atom, List<Predicate>>();


        initialState        = new GameState();
        TimerFlag timerFlag = new TimerFlag();
        try {

            /**
             * Generating the initialState
             */
            ExpressionList list = ResolutionHelper.resolveAndApply( Const.pTrue,
                                                                    Const.pInit,
                                                                    theoryScope,
                                                                    timerFlag );

            // add all expressions to the initial state
            Atom atom;
            for ( Expression exp : list ) {
                atom = (Atom) exp.secondOperand();
                // check if we already have a list there
                if ( !initialState.containsKey(atom) )
                    initialState.put(atom, new ExpressionList());

                initialState.get(atom).add( exp );
            }

        }

        // we do not expect to be stopped here by the timer
        catch (InterruptedException e) {}


        // info == not valid, means we have to generate all the game information
        if ( info.isValid() )
            initialiseGameWithInformation();
        else
            initialiseGameWithoutInformation(timerFlag);

        /**
         * Generating all the legal moves of the initial state
         */
        try {
            for (Atom role: roleNames)
                getLegalMoves( role, initialState, timerFlag );
        }
        catch ( InterruptedException e ) {}

        stateTree = new GameStateTree(initialState);
    }


    /**
     * This method initialises a game and its information through a given game
     * information object.
     *
     * Following attributes are set through this method.
     * - roleNames
     *
     * @param info GameInformation object from the game db
     */
    private void initialiseGameWithInformation() {

        for ( String sRole : info.getRoles() )
            roleNames.add( new Atom(sRole) );

    }


    /**
     * This method initialises a game and its information by resolving all facts
     * by its own since we did not got a filled game information object.
     *
     * Following attributes are resolved through this method.
     * - roleNames
     * @param timerFlag
     *
     */
    private void initialiseGameWithoutInformation(TimerFlag timerFlag) {
        /**
         * Initialising the roleNames
         */
        extractRoles(timerFlag);

        /**
         * Initialising the templates: does, legal and goal
         */
        initializeTemplates();

        /**
         * Initialising goal statements
         */
        extractGoals();

    }

    /**
     * @param timerFlag
     *
     *
     */
    private void extractRoles(TimerFlag timerFlag) {
        List<String>   tmpList = new ArrayList<String>();
        try {
            // filling the roles
            ExpressionList roles = ResolutionHelper.resolveAndApply( Const.pRole,
                                                      Const.pRole,
                                                      theoryScope,
                                                      timerFlag );
            for ( Expression pRole : roles ) {
                Atom  tmpAtom = (Atom) ((Predicate) pRole).secondOperand();
                tmpList.add( tmpAtom.toString() );
                roleNames.add( tmpAtom );
            }
        }

        // we do not expect to be stopped here by the timer
        catch ( Exception e ) {}

        // push the information to the info object
        info.setRoles( tmpList );
    }

    /**
     *
     *
     */
    private void extractGoals() {
        ExpressionList availableGoals = theoryObj.get( Const.aGoal );
        for(Expression aGoal : availableGoals){
            Implication impl = (Implication) aGoal;
            Predicate head = (Predicate) impl.getConsequence();
            Term role = head.secondOperand();
            if ( role instanceof Atom ) {
                Atom roleName = (Atom) role;
                addToGoals( head, roleName );
            } else {
                for (Atom aRoleName : roleNames){
                    addToGoals( head, aRoleName );
                }
            }
        }
    }


    private void addToGoals(Predicate goalHead, Atom roleName) {
        if (goals.containsKey( roleName )){
            List<Predicate> goalsForRole = goals.get(roleName);
            goalsForRole.add( goalHead );
        } else {
            List<Predicate> goalsForRole = new ArrayList<Predicate>();
            goalsForRole.add( goalHead );
            goals.put( roleName, goalsForRole );
        }
    }


    private void initializeTemplates() {
        /**
         * Generating the templates
         */
        for (Atom aRole : roleNames ) {

            // creating action templates
            actionTemplates.put( aRole,
                                 new Predicate(Const.aDoes, aRole, Const.vX) );

            // creating legal templates
            legalityTemplates.put( aRole,
                                 new Predicate(Const.aLegal, aRole, Const.vX) );

            goalTemplates.put( aRole,
                                new Predicate(Const.aGoal,  aRole, Const.vX) );
        }
    }

    /**
     * This method returns the identifier of the available player.
     *
     * The resulting expressions are the role atoms e.g. ROBOT. If you want the
     * role predicates use getRoles.
     * @return
     * @throws InterruptedException
     */
    public List<Atom> getRoleNames() {
        return roleNames;
    }


    /**
     * This method returns the initial game state.
     */
    public GameNode getInitialNode() {
        return stateTree.getRootNode();
    }

    /**
     * This method returns the turnTaking indicator.
     */
    public final boolean isTurnTaking() {
        return info.isTurnTaking();
    }

    /**
     * This method returns the singlePlayer indicator.
     */
    public final boolean isSinglePlayer() {
        return roleNames.size() == 1;
    }

    public final Theory getTheory(){
        return theoryObj;
    }

    /**
     * This method is responsible for obtaining all possible legal moves from
     * current game state with respect to initial game rules.
     *
     * @param role      Particular role for which legal moves are resolved.
     * @param gameState Particular game state.
     * @param flag      Timer flag, which is set once time is out.
     * @return List of legal moves, where each of legal moves is
     *         in form &quot;DOES &lt;role&gt; &lt;move&gt; &quot;
     */
    public ExpressionList getLegalMoves( Atom      aRole,
                                         GameState gameState,
                                         TimerFlag timerFlag )
    throws InterruptedException {

        // we already know the legale moves of this state/role combination
        if ( gameState.hasLegalMoves(aRole) )
            return gameState.getLegalMoves(aRole);

        // we have to produce the legal moves for this state/role combination
        List<Substitution> legalList;
        legalList = legalityTemplates.get(aRole).chain(
                                       new Substitution(),
                                       new GameStateScope(theoryObj, gameState),
                                       timerFlag );

        /* TODO: ResolutionHelper.produceDerivativesFromSubstitutions
         *       should be replaced by some nice-and-straightforward
         *       iteration on list of substitutions.
         */
        ExpressionList legalMoves;
        legalMoves = ResolutionHelper.produceDerivativesFromSubstitutions(
                                                    actionTemplates.get(aRole),
                                                    legalList );
        gameState.setLegalMoves( aRole, legalMoves );
        return legalMoves;
    }

    /**
     * This method produces a list of lists of combined legal moves of
     * all players in a single turn in a game state gameState
     *
     * @param gameState
     * @param timerFlag
     * @return
     */
    public List<ExpressionList> getCombinedLegalMoves( GameState gameState,
                                                       TimerFlag extTimerFlag )
    throws InterruptedException {

        // the combined legal moves are already computed
        if ( gameState.hasCombinedLegalMoves() )
            return gameState.getCombinedLegalMoves();

        // we have to produce the combined legal moves from the existing
        // individual legal moves
        List<ExpressionList> legals = new ArrayList<ExpressionList>();

        for (Atom role : roleNames)
            legals.add( getLegalMoves( role, gameState, extTimerFlag ) );

        List<ExpressionList> combinedLegals = combine(legals);
        gameState.setCombinedLegalMoves( combinedLegals );
        return combinedLegals;
    }

    static List<ExpressionList> combine(List<ExpressionList> legals){
        List<ExpressionList> chosen = new ArrayList<ExpressionList>();
        Iterator<ExpressionList> iter = legals.iterator();
        ExpressionList firstPlayerMoves = iter.next();
        for (Expression move : firstPlayerMoves)
            chosen.add( new ExpressionList(move) );

        while ( iter.hasNext() ) {
            final List<ExpressionList> previouslyChosen = chosen;
            chosen = new ArrayList<ExpressionList>();
            ExpressionList aPlayerMoves = iter.next();
            for (Expression newPlayerMove : aPlayerMoves){
                for (ExpressionList exp : previouslyChosen){
                    ExpressionList copy = new ExpressionList(exp);
                    copy.add( newPlayerMove );
                    chosen.add( copy );
                }
            }

        }

        return chosen;
    }

    /**
     * This method returns a game state which is reachable from a given one by
     * list of moves performed by each player.
     *
     * It also keeps track of adding new states to the game state tree.
     *
     * @param gameNode GameState given game state
     * @param moves     Moves list of moves performed by each player.
     * @return New game state.
     */
    public GameNode produceNextNode(
            GameNode gameNode, ExpressionList moves, TimerFlag timerFlag )
    throws InterruptedException {

        // we already know the next node for this state/move combination
        GameNode newChild = stateTree.getNextNode( gameNode, moves );
        if (newChild != null && newChild.getState() != null)
            return newChild;

        GameState parentState = gameNode.getState();

        // we have to produce the next state for this state/move combination
        List<Substitution> nextStateSubs = Const.pNext.chain(
                    new Substitution(),
                    new MovesScope(theoryObj, parentState, moves),
                    timerFlag);

        ExpressionList trueFluents =
            ResolutionHelper.produceDerivativesFromSubstitutions(
                    Const.pTrue, nextStateSubs );

        // new game state
        GameState newState = new GameState(trueFluents);
        GameStateScope gameStateScope = new GameStateScope(theoryObj, newState);

        // test new game state terminality
        if ( isTerminalState(gameStateScope, timerFlag) )
            newState.setTerminal();

        // test new game state goal values
        checkForGoalValues(newState, gameStateScope, timerFlag);

        if ( newChild != null ) {
            newChild.setState( newState );
            return newChild;
        }

        // add the new state to the tree
        return stateTree.addChild( newState, gameNode, moves);
    }


    private void checkForGoalValues(
            GameState state, RuleScope scope, TimerFlag timerFlag)
    throws InterruptedException {
        Substitution initial = empty;
        for(Atom aRole : goalTemplates.keySet()){
            Predicate pGoal = goalTemplates.get( aRole );

            Substitution result = pGoal.chainOne( initial, scope, timerFlag );
            if (null !=  result){
                Expression goalValue = result.get( Const.vX );
                if ( goalValue instanceof Atom ) {
                    Atom aGoalValue = (Atom) goalValue;
                    int intGoalValue = Integer.parseInt( aGoalValue.toString() );
                    if ( 0 != intGoalValue){
                        state.putRoleGoalValue(aRole, intGoalValue);
                    }
                }
            }
        }
    }

    public boolean isTerminalState(
            RuleScope gameStateScope, TimerFlag timerFlag )
    throws InterruptedException {
            return ResolutionHelper.isResolvable(
                    Const.aTerm,
                    gameStateScope,
                    timerFlag );
    }

    public final List<Predicate> getGoalsForRole(Atom roleName){
        return goals.get( roleName );
    }

}
