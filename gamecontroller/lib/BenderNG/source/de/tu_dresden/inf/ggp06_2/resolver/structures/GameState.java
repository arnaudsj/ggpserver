package de.tu_dresden.inf.ggp06_2.resolver.structures;

import gnu.trove.THashMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;

/**
 * The GameState class stores all information corresponding to a single state in
 * a match/game.
 *
 * The game state is characterized by fluents. The fluents are organized by the
 * first occurring atom that appear in the TRUE expressions.
 *
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 * @author Novak Novakovic - General Game Playing course student at TUD
 *
 *
 */
public class GameState extends THashMap<Atom, ExpressionList> {

    private static final Logger logger = Logger.getLogger( GameState.class );

    private boolean                terminal   = false;
    private boolean                goal       = false;
    private boolean                played     = false;

    final Map<Atom, ExpressionList>  legalMoves         = new HashMap<Atom, ExpressionList>();
    final ExpressionList             linearLegalMoves   = new ExpressionList();
    final List<ExpressionList>       combinedLegalMoves = new ArrayList<ExpressionList>();
    final ResolutionMemorizer        memorizer          = new ResolutionMemorizer();

    final Map<Atom, Double>          fuzzyGoalValues    = new HashMap<Atom, Double>();
    final FuzzyResolutionMemorizer   fuzzyMomrizer      = new FuzzyResolutionMemorizer();

    //Map<Atom, Set<Predicate>>  reachedGoals       = new HashMap<Atom, Set<Predicate>>();
    final Map<Atom, Integer>         roleGoalValues     = new HashMap<Atom, Integer>();

    final Map<Atom, Atom>            counters          = new HashMap<Atom, Atom>();


    public Map<String, List<Atom>> structureContent =
                                            new HashMap<String, List<Atom>>();

    /**
     * This constructor creates an empty game state.
     */
    public GameState() {
        logger.trace( "game state created" );
    }

    /**
     * Constructor creates a new game state object based on a given
     * transposition.
     * @param objState
     */
    public GameState( Map<Atom, ExpressionList> stateMap ) {
        putAll(stateMap);
        logger.trace( "game state created" );
    }

    /**
     * Constructor creates a new game state object based on a given
     * transposition (usually recieved from Theory).
     * @param incommingFluents
     */
    public GameState(ExpressionList incommingFluents){

        for (Expression fluent : incommingFluents) {
            Atom secondOperand = (Atom) fluent.secondOperand();
            ExpressionList typedFluents = null;

            if ( containsKey(secondOperand)) {
                typedFluents = get(secondOperand);
            } else {
                typedFluents = new ExpressionList();
                put( secondOperand, typedFluents );
            }
            typedFluents.add( fluent );
        }
    }

    @Override
    public ExpressionList get(Object key) {
        return containsKey(key) ? super.get(key) : new ExpressionList();
    }

    /**
     * This method returns the legal moves for a player. It is meant to be a
     * cache.
     * @param role
     */
    public ExpressionList getLegalMoves(Atom role) {
        return legalMoves.get(role);
    }

    /**
     * This method returns wether the cache contains the legal moves of a
     * player.
     * @param role
     */
    public boolean hasLegalMoves(Atom role) {
        return legalMoves.containsKey(role);
    }

    /**
     * This method sets the legal moves for a player. It is meant to be a cache.
     * @param role
     * @param list
     */
    public void setLegalMoves( Atom role, ExpressionList list ) {
        legalMoves.put(role, list);
        linearLegalMoves.addAll( list );
    }

    /**
     * Returns the branching factor of this game state
     * @return
     */
    public int getBranchingFactor() {
        int factor = 1;
        for (ExpressionList list : legalMoves.values() )
            factor *= ( list.size() + 1);
        return factor;
    }

    public ExpressionList getLinearLegalMoves() {
        return linearLegalMoves;
    }

    public List<Substitution> getProven( Substitution sigma, Expression e ) {
        return memorizer.getProven( sigma, e );
    }

    public List<FuzzySubstitution> getProvenForFuzzy(Substitution sigma, Expression expression) {
        final List<Substitution> proven = memorizer.getProven( sigma, expression );
        List<FuzzySubstitution> fuzzyProven = new ArrayList<FuzzySubstitution>();
        for (Substitution aSub : proven){
            fuzzyProven.add(new FuzzySubstitution(aSub));
        }
        return fuzzyProven;
    }

    public boolean isDisproven(Expression expression) {
        return memorizer.isDisproven( expression );
    }

    public boolean isProven(Expression e){
        return memorizer.isProven( e );
    }

    public void setDisproven(Expression expression){
        memorizer.setDisproven( expression );
    }

    public void setProven(Expression e, List<Substitution> subs){
        memorizer.setProven( e, subs );
    }

    public void setTerminal() {
        terminal = true;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setGoal() {
        goal = true;
    }

    public boolean isGoal() {
        return goal;
    }

    public boolean isFuzzylyEvaluated(Expression expression) {
        return this.fuzzyMomrizer.isFuzzylyEvaluated( expression );
    }

    public FuzzyResolution getFuzzyResolutionStage(FuzzySubstitution sigma, Expression expression) {
        return this.fuzzyMomrizer.getFuzzyResolutionStage( sigma, expression );
    }

    public void setFuzzyEvaluationStage(Expression expression, FuzzyResolution sigmas) {
        this.fuzzyMomrizer.setFuzzyEvaluationStage( expression, sigmas );
    }

    public List<ExpressionList> getCombinedLegalMoves(){

        return this.combinedLegalMoves;
    }

    public void setCombinedLegalMoves(List<ExpressionList> legals){
        this.combinedLegalMoves.addAll(legals);
    }
    public boolean hasCombinedLegalMoves(){
        return !combinedLegalMoves.isEmpty();
    }

    @Override
    public String toString() {
        return "GameState" + super.toString() + " "+roleGoalValues;
    }

    public boolean wasPlayed() {
        return played;
    }

    /**
     * This method sets the played attribute to true. It does it only once since
     * this information changes only from false to true in a game tree.
     */
    public void setPlayed() {
        played = true;
    }

    public final void putRoleGoalValue(Atom role, int value){
        roleGoalValues.put( role, value );
    }

    public final boolean isRoleGoalValue(Atom role){
        return roleGoalValues.containsKey( role );
    }

    public final int getRoleGoalValue(Atom role){
        return roleGoalValues.get( role );
    }

    public final double getFuzzyValueForRole(Atom role){
        return this.fuzzyGoalValues.get( role );
    }

    public final void setFuzzyValueForRole(Atom role, double value){
        this.fuzzyGoalValues.put( role, value );
    }

    public final boolean containsFuzzyValueForRole(Atom role){
        return this.fuzzyGoalValues.containsKey( role );
    }

    public final void putCounterValue(Atom funciton, Atom value){
        this.counters.put( funciton, value );
    }

    public final boolean containsCounter(Atom function){
        return this.counters.containsKey( function );
    }

    public final Atom getCounterValue(Atom function){
        return this.counters.get(function);
    }

    public final Set<Atom> getCounterNames(){
        return this.counters.keySet();
    }

    public void flushFuzzyMemorizations() {
        this.fuzzyMomrizer.clear();
        this.linearLegalMoves.clear();
    }
}
