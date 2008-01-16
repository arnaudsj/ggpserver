package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.astvisitors.AbstractVisitor;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.RuleScope;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

/**
 *
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 *
 */
public class Predicate extends Connective {

    /* Stores the logger for this class */
    public static Logger logger = Logger.getLogger(Predicate.class);

    protected final Atom operator;

    /**
     * A predicate with single argument, i.e. <i>&quot;(cell 1)&quot;</i>
     *
     * @param operator Operator, &quot;cell&quot; in this case
     * @param operand Single operand. In example given above it would be &quot;1&quot;
     */
    public Predicate(Atom operator, Expression operand){
        this.operator = operator;
        operands.add( operand );
    }

    /**
     * A predicate with two arguments, i.e. <i>&quot;(cell 1 2)&quot;</i>
     *
     * @param operator Operator, &quot;cell&quot; in this case
     * @param operand1 First operand. In example given above it would be &quot;1&quot;
     * @param operand2 Second operand. In example given above it would be &quot;1&quot;
     */
    public Predicate(Atom operator, Expression operand1, Expression operand2){
        this.operator = operator;
        operands.add( operand1 );
        operands.add( operand2 );
    }

    /**
     * A predicate with multiple arguments, i.e. <i>&quot;(cell 1 2 b)&quot;</i>
     *
     * @param operator Operator, &quot;cell&quot; in this case.
     * @param operands List of expressions that correspond to all the operands of the
     *                predicate.
     */
    public Predicate(Atom operator, ExpressionList operands) {
        this.operator = operator;
        this.operands.addAll( operands );
    }

    /**
     * @return Returns the operator.
     */
    public Atom getOperator() {
        return operator;
    }

    /**
     * Apply substitution to premises of current predicate,
     * which produces list of new premises. These new premises
     * are used to create new predicate, which is returned
     * as result.
     *
     * @param sigma Substitution to apply
     * @return Returns a new predicate, obtained
     *         from a current one, by application
     *         of substitution.
     */
    @Override
    public Expression apply(Substitution sigma) {
        return new Predicate( operator, operands.apply(sigma) );
    }

    /**
     * <code>Predicate</code> class is actually one of those that make use
     * of GDL rules scopes by getting similar expressions from them.
     * Trick about <code>Predicate</code> is that there might be implication
     * consequences and single predicates (both static and dynamic) recieved
     * after matching with existing data. So major trick is a kind of branching.
     * Another point is that <code>chain</code> method is looking for all
     * possible resolutions. Here is a step by step behavior:
     * <ol>
     *     <li> ask current GDL scope for similar predicates.</li>
     *     <li> iterate through each of the predicates:
     *     <ol>
     *         <li> check type of expression being currently iterated
     *         <ol>
     *             <li> if an implication happens to be retrieved:
     *             <ol>
     *                 <li> ask it's consiquence for a MGU</li>
     *                 <li> proceed to <code>chain</code> of the premises (those are
     *                      stored in an <code>ExpressionList</code></li>
     *                 <li> store the result</li>
     *             </ol></li>
     *             <li> otherwise just as for an MGU with iterated expression</li>
     *         </ol></li>
     *         <li> store any non-empty results</li>
     *     </ol>
     *     </li>
     *     <li> if no results were accumulated, assume it as resolution failure
     *          and return empty list of substitutions</li>
     * </ol>
     *
     * @see de.tu_dresden.inf.ggp06_2.resolver.Term#chainBody(Substitution, RuleScope, TimerFlag) Term.chainBody
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    @Override
    protected List<Substitution> chainBody( Substitution sigma,
                                            RuleScope    scope,
                                            TimerFlag    flag )
    throws InterruptedException {

        if ( scope.isDisproven(this) )
            return new ArrayList<Substitution>();

        else if ( scope.isProven(this) )
            return scope.getProven( sigma, this );

        List<Substitution> answers = chainSimilarExpressions(sigma, scope, flag);

        // Memoize the values returned
        if ( !answers.isEmpty() )
            scope.setProven(this, answers);

        // memoize the failure
        else
            scope.setDisproven(this);

        return answers;
    }

    /**
     * This private method acts only as an entry point for <code>chain</code>
     * method. It hides all the stuff specific to getting similar expressions
     * from the rules scope and iterating over them. Implemented only for the
     * sake of readability.
     *
     * @see de.tu_dresden.inf.ggp06_2.resolver.Predicate#chainBody(Substitution, RuleScope, TimerFlag) Predicate.chainBody
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    private List<Substitution> chainSimilarExpressions( Substitution sigma,
                                                        RuleScope    scope,
                                                        TimerFlag    flag )
    throws InterruptedException {

        List<Substitution> answers = new ArrayList<Substitution>();
        for ( Expression anExpression : scope.getSimilarExpressions(this) )

            if (anExpression instanceof Implication)
                chainImplication( sigma,
                                  scope,
                                  flag,
                                  answers,
                                  (Implication) anExpression );

            else {
                Substitution psi = mgu(anExpression, sigma);
                if (psi != null)
                    answers.add(psi);
            }

        return answers;
    }

    /**
     * This private method acts only as an entry point for
     * <code>chainSimilarExpressions</code> method
     * It takes care of handling any implications that appear during iteration.
     * Implemented for the sake of readability only.
     * sigma
     * @see de.tu_dresden.inf.ggp06_2.resolver.Predicate#chainSimilarExpressions(Substitution, RuleScope, TimerFlag) chainSimilarExpressions
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @param accumulator List of substitutions that actually accumulates any
     *                    successful results
     * @param imp Current implication to be resolved.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    private void chainImplication( Substitution       sigma,
                                   RuleScope          scope,
                                   TimerFlag          flag,
                                   List<Substitution> accumulator,
                                   Implication        imp )
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;

        Substitution psi = mgu(imp.getConsequence(), sigma);
        if (psi != null) {
            List<Substitution> substitutions =
                                    imp.getPremises().chain(psi, scope, flag);

            if ( !substitutions.isEmpty() )
                accumulator.addAll(substitutions);
        }
    }

    /**
     * Trick about <code>Predicate</code> is that there might be implication consequences
     * and single predicates (both static and dynamic) recieved after matching
     * with existing data. So major trick is a kind of branching. Here is a step
     * by step behavior:
     * <ol>
     *     <li> ask current GDL scope for similar predicates.</li>
     *     <li> iterate through each of the predicates:
     *     <ol>
     *         <li>if an implication happens to be retrieved, ask it's
     *             consiquence for a MGU, and proceed to <code>chainOne</code>
     *             of the premises (those are stored in an
     *             <code>ExpressionList</code></li>
     *         <li>otherwise just as for an MGU with iterated expression</li>
     *         <li>if any of iterations produces non-empty result - return it</li>
     *     </ol>
     *     </li>
     *     <li> if no results were returned, assume it as resolution failure
     *          and return null</li>
     * </ol>
     *
     * @see de.tu_dresden.inf.ggp06_2.resolver.Predicate#mgu(Expression, Substitution) mgu
     * @see de.tu_dresden.inf.ggp06_2.resolver.ExpressionList#chainOne(Substitution, RuleScope, TimerFlag) ExpressionList.chainOne
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns one (first) substitution that was produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    @Override
    protected Substitution chainOneBody( Substitution sigma,
                                         RuleScope    scope,
                                         TimerFlag    flag )
    throws InterruptedException {

        // Check for memoized values, first
        if ( scope.isProven(this) ) {
            List<Substitution> allSubs = scope.getProven(sigma,this);
            Substitution psi = allSubs.get(0);
            return psi;

        } else if ( scope.isDisproven(this) )
            return null;

        Substitution psi = null;
        for ( Expression anExpression : scope.getSimilarExpressions(this) ) {

            if (anExpression instanceof Implication) {
                psi = chainOneImplication(
                        sigma, scope,
                        flag, (Implication )anExpression );
            } else
                psi = mgu(anExpression, sigma);

            // Don't set proven for a single unifier--
            // we need a full solution-set
            if (psi != null)
                return psi;
        }

        // But always memorize failure
        scope.setDisproven(this);
        return null;
    }

    /**
     * This method acts only as an entry point for <code>chainOne</code> method.
     * It actually takes care of handling implication, if an implication
     * happens among expression returned by scope.
     *
     * @see de.tu_dresden.inf.ggp06_2.resolver.Predicate#chainOne(Substitution, RuleScope, TimerFlag) chainOne
     * @param sigma State of resolution
     * @param scope GDL rules scope
     * @param flag Timer flag
     * @param implication Actual implication to resolve with
     * @return Returns substitution that resolves current predicate to given
     *         implication, if there is any.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    private Substitution chainOneImplication( Substitution sigma,
                                              RuleScope    scope,
                                              TimerFlag    flag,
                                              Implication  implication )
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;

        Expression   consequence = implication.getConsequence();
        Substitution gamma       = mgu(consequence, sigma);

        return ( gamma != null ) ?
                    implication.getPremises().chainOne(gamma, scope, flag) :
                    null;
    }

    @Override
    protected FuzzyResolution fuzzyEvaluateBody( FuzzySubstitution sigma,
                                                 GameStateScope    scope,
                                                 List<Expression>  guard,
                                                 TimerFlag         flag )
    throws InterruptedException {

        FuzzyResolution sigmas = null;

        if (scope.isProven( this )){
            sigmas = new FuzzyResolution();
            sigmas.addAll( scope.getProvenForFuzzy( sigma, this ) );
            sigmas.setFuzzyValue( Expression.fuzzyOne );
            return sigmas;

        } else if (scope.isFuzzylyEvaluated(this) ) {
            return scope.getFuzzyResolutionStage(sigma, this);

        } else if ( guard.contains(operator) && sigma.isBottom() ){
            sigmas = new FuzzyResolution();
            sigmas.add( sigma );
            sigmas.setFuzzyValue( Expression.fuzzyZero );
            return sigmas;
        }

        int size = guard.size();
        guard.add( size, this.operator);
        sigmas = fuzzyEvaluateSimilarExpressions( sigma, scope, guard, flag );
        guard.remove( size );

        return sigmas;
    }

    private FuzzyResolution fuzzyEvaluateSimilarExpressions(
                                                    FuzzySubstitution sigma,
                                                    GameStateScope    scope,
                                                    List<Expression>  guard,
                                                    TimerFlag         flag )
    throws InterruptedException {
        /*
         * A trick about 'resolved' is that if
         * some predicate at some point is resolved,
         * we want to stop calculating fuzzy value for it.
         * However, we would like to keep collecting successful
         * substitutions. Therefore 'resolved' acts as a flag,
         * indicating that no modifications to fuzzy value of
         * current predicate should be done from now on.
         *
         * For second issue consider following rules:
         * (<= (somepred ?x)
         *     (succ ?x ?y)
         *     (somepred ?y)
         * )
         * (somepred 1)
         * (succ 2 1)
         * (succ 3 2)
         * (succ 4 3)
         * The rules of 'somepred' kind should be treated as if they are
         * combined with a T-Conorm (fuzzy conjunction),
         * whereas the rules of 'succ' kind should result only to fuzzy
         * truth or fuzzy false (no fuzzy conjunciton is assumed)
         *
         * Moreover if some candidate (disreguarding which kind) evaluates to
         * fuzzy true (higher than a threashold), fuzzy true should be
         * returned as the result.
         */
        boolean         noImplications = true;
        ExpressionList  candidates     = scope.getSimilarExpressions(this);
        FuzzyResolution sigmas         = new FuzzyResolution();

        for ( Expression e : candidates ) {

            if (e instanceof Implication) {
                Implication  impl = (Implication) e;
                Substitution psi  = mgu( impl.getConsequence(), sigma );
                noImplications    = false;

                if (null != psi) {
                    FuzzyResolution fuzzy = fuzzyEvaluatePremises(
                                              new FuzzySubstitution(psi, sigma),
                                              scope,
                                              guard,
                                              flag,
                                              impl );
                    sigmas.addAlternativeResolution(fuzzy);
                }

            } else {
                Substitution psi = mgu(e, sigma);
                if (null != psi)
                    evaluatePositively( sigma, sigmas, psi );
                else
                    evaluateNegatively( sigma, sigmas );

            }
        }

        if (! noImplications)
            scope.setFuzzyEvaluationStage(this, sigmas);

        else if (sigmas.getFuzzyValue() < Expression.threshold)
            sigmas.setFuzzyValue( Expression.fuzzyZero );

        return sigmas;
    }

    protected void evaluateNegatively( FuzzySubstitution sigma,
                                       FuzzyResolution   sigmas ) {
        sigma.setBottom( true );
        sigma.tNorm(Expression.fuzzyZero);
        sigmas.add( sigma );
    }

    protected void evaluateToFuzzyFalse( FuzzySubstitution sigma,
                                         FuzzyResolution   sigmas ) {
        sigma.setBottom( true );
        sigma.tNorm(Expression.fuzzyZero);
        sigmas.add( sigma );
        sigmas.setFuzzyValue( Expression.fuzzyZero );
    }

    protected void evaluatePositively( FuzzySubstitution sigma,
                                       FuzzyResolution   sigmas,
                                       Substitution      psi ) {
        FuzzySubstitution fuzzySubstitution = new FuzzySubstitution(psi, sigma);
        fuzzySubstitution.tNorm( Expression.fuzzyOne );
        sigmas.add( fuzzySubstitution );
    }

    private FuzzyResolution fuzzyEvaluatePremises( FuzzySubstitution sigma,
                                                   GameStateScope    scope,
                                                   List<Expression>  guard,
                                                   TimerFlag         flag,
                                                   Implication     implication )
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;
        return implication.getPremises().fuzzyEvaluate( sigma, scope,
                                                        guard, flag );
    }

    @Override
    public Atom firstOperand() {
        return operator;
    }

    /**
     * There are three possibilities in total for any connective.
     * <ul>
     *     <li> Either it is unified with a variable, in this case
     *          unification is just passed to variable itself;</li>
     *     <li> Or with some kind of <code>Predicate</code>. If that
     *          is the case first an MGU of operators (operator names)
     *          is produced and resulting substitution is passed to
     *          produce MGU of operands;</li>
     *     <li> Any other case results into failure.</li>
     * </ul>
     *
     * Method assumes that we do not have a variable as function symbol.
     *
     * @param target Target expression to unify with
     * @param sigma Some current resolution stage
     * @return Returns the most general unifier substitution, w.r.t
     *         current resolution stage.
     */
    @Override
    public Substitution mgu(Expression target, Substitution sigma) {
        if (target instanceof Variable)
            return ((Variable) target).mgu(this, sigma);

        if (target instanceof Predicate) {
            Predicate pTarget = (Predicate) target;

            if ( operator.equals(pTarget.operator) )
                return operands.mgu(pTarget.operands, sigma);
        }
        return null;
    }

    /**
     *  Two predicates are equal, if they have same operator and operands.
     */
    @Override
    public boolean equals(Object obj) {
        return  (obj instanceof Predicate) &&
                operator.equals( ((Predicate) obj).operator ) &&
                operands.equals( ((Predicate) obj).operands );
        }

    /**
     * @return Returns string of form:
     *         &quot;(&quot; + operator + &quot; &quot; + operands + &quot;)&quot;
     */
    @Override
    public String toString() {
        return  (toString == null) ?
                     toString = "(" + operator + " " + operands + ")" :
                     toString;
    }

    @Override
    public boolean isGround() {
        for (Expression exp : operands)
            if ( !exp.isGround() )
                return false;
        return true;
    }

    @Override
    public Atom getKeyAtom() {
        return operator;
    }

    @Override
    public void processVisitor(AbstractVisitor visitor) {
        visitor.visitPredicate(this);
    }

}
