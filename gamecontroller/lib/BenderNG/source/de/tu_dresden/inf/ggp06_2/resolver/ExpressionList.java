package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.ArrayList;
import java.util.Iterator;
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
public class ExpressionList extends ArrayList<Expression>
                            implements Iterable<Expression> {

    private static final long serialVersionUID = 7416224217531412192L;

    private static final Logger logger = Logger.getLogger(ExpressionList.class);
    private List<Variable> cachedVariables = null;

    /**
     * Constructor creates a new empty ExpressionList object.
     */
    public ExpressionList() {
        super();
    }

    /**
     * Constructor creates a new ExpressionList object and add the
     * given expression.
     *
     * This method is useful if you create a predicate with only one operand.
     *
     * @param expression single expression to be added to
     *                   <code>ExpressionList</code> after it's creation.
     */
    public ExpressionList( Expression expression ) {
        super();
        if ( expression != null )
            add( expression );
    }

    /**
     * Constructor creates a new ExpressionList object and adds all expressions
     * from the given expression array.
     *
     * @param expressions Array of expressions to be added after to
     *                    <code>ExpressionList</code> after it's creation.
     */
    public ExpressionList( Expression[] expressions ) {
        super();
        if ( expressions != null )
            for ( Expression expression : expressions )
                add( expression );
    }

    /**
     * Constructor creates a new ExpressionList object with the given collection
     * elements.
     *
     * @param expressions List of expressions that are to be added to
     *                    <code>ExpressionList</code> after it's creation.
     */
    public ExpressionList( ExpressionList expressions ) {
        super();
        if ( expressions != null )
            addAll( expressions );
    }

    /**
     * Adds a expression to a given expression list.
     * This method is used by the parser.
     * @param expList ExpressionList to be appended.
     * @param expression Expression to append
     * @return Returns a newly updated <code>ExpressionList</code>
     */
    public static ExpressionList addToList( ExpressionList expList,
                                            Expression     expression ) {

        // create a new one if we not already have one
        if ( expList == null )
            expList = new ExpressionList();

        expList.add( expression );
        return expList;
    }

    /**
     * Appends given expression array to current expression list.
     * @param expressionArray Array of expressions to be appended.
     */
    public void addAll(Expression[] expressionArray) {
        if ( expressionArray == null )
            return;

        for (Expression expression : expressionArray)
            add(expression);
    }

    /**
     * Appends content of given <code>ExpressionList</code> to current one.
     * @param expressionList ExpressionList to be appended.
     */
    public void addAll(ExpressionList expressionList) {
        if ( expressionList == null )
            return;

        super.addAll( expressionList );
    }

    /**
     * This method is another entering point for a chain resolution, but this
     * time only one (first) solution is reported.
     * @see de.tu_dresden.inf.ggp06_2.resolver.Expression#chain(Substitution, RuleScope, TimerFlag) chain
     * @param gamma Current state of resulution
     * @param scope Scope of GDL rules used for resulution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns a substitution that was produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires
     *                              during procedure.
     */
    public Substitution chainOne( Substitution gamma,
                                  RuleScope    scope,
                                  TimerFlag    flag )
    throws InterruptedException{
        if ( flag.interrupted() ) throw Const.interrupt;

        if ( isEmpty() )
            return gamma;

        List<Substitution> psis = resolveThroughList( gamma, scope, flag );

        return psis.isEmpty() ?
                    null :
                    (size() == 1) ?
                        psis.get(0) :
                        pickOneSubstitution(scope, flag, psis);

    }

    /**
     * This private method acts as entry point for <code>chainOne</code> method.
     * It's purpose is to find first substitutions that satisfies last
     * expression of the list.
     * @param scope Scope of GDL rules used for resulution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @param psis Current state of resulution
     * @return Returns a substitution that was produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires
     *                              during procedure.
     */
    private Substitution pickOneSubstitution( RuleScope          scope,
                                              TimerFlag          flag,
                                              List<Substitution> psis )
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;

        // It's not that much of a savings, but
        // for the last expression,
        // we can stop as soon as we find a single
        // working unifier if we're only looking for
        // one answer.
        Expression lastExp = get(size() - 1);
        for (Substitution aSubstitution : psis)	{

            Substitution oneGamma = lastExp.chainOne(aSubstitution, scope, flag);

            if (oneGamma != null)
                return oneGamma;

        }
        return null;
    }

    /**
     * Like in <code>Expression</code> class, there are four similar methods:
     * chain, chainOne, eval and evalOne. Each of them is related to resulution,
     * appropriate methods of <code>ExpressionList</code> act as group wrappers
     * arround correponding methods of <code>Expression</code> class.
     * <p>
     * For instance behaviour of this method could be described as follows:
     * <ol>
     *     <li> return resolution state if there no elements in list</li>
     *     <li> try to resolve first element of list
     *         <ol>
     *         <li>
     *             if resolution of first list element produces empty
     *             substitution list, return empty substitution list as result
     *             of expression list chaining
     *         </li>
     *         <li>
     *             continue, otherwise
     *         </li>
     *         </ol>
     *     </li>
     *     <li> so far it appear chaining the first element produced non empty
     *          list of substitutions. All that is left is to go through rest
     *          of elements in current expression list:
     *     <ol>
     *         <li> chain iterated element with each of the produced substitutions</li>
     *         <li> once non empty result is returned, store it to temporary list value</li>
     *         <li> pass temporary list value to next cycle of iteration</li>
     *     </ol></li>
     *     <li> return list with accumulated substitutions</li>
     * </ol>
     * </p>
     *
     * @param gamma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    public List<Substitution> chain( Substitution gamma,
                                     RuleScope    scope,
                                     TimerFlag    flag )
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;

        // Just return a list of given substitutions
        // if there are no expressions in the list.
        if ( isEmpty() ) {
            List<Substitution> psis = new ArrayList<Substitution>();
            psis.add(gamma);
            return psis;
        }

        return resolveThroughList( gamma, scope, flag );
    }

    /**
     * This private method acts as entry point for <code>chain</code> and
     * <code>chainOne</code> methods. It's purpose is entirely enchaned
     * readability.
     *
     * @param gamma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during
     *         resolution procedure.
     * @throws InterruptedException This exception is thrown once time expires
     *                              during procedure.
     */
    private List<Substitution> resolveThroughList( Substitution gamma,
                                                   RuleScope    scope,
                                                   TimerFlag    flag )
    throws InterruptedException {
        Iterator<Expression> iter  = iterator();
        Expression           first = iter.next();
        List<Substitution>   psis  = first.chain(gamma, scope, flag);

        // handle null substitution list
        if (psis == null) {
            logger.error( "returned empty subst-list, while resolving: "+first);
            return null;
        }

        // handle empty substitution list
        if ( psis.isEmpty() )
            return psis;

        /*
         * This loop iterates on rest of current list entries available
         * through iterator, sequentially applying substitutions on each of
         * them.
         */
        while ( iter.hasNext() ) {
            Expression         anExpression = iter.next();
            List<Substitution> deltas       = applySubstitutions( scope,
                                                                  flag,
                                                                  psis,
                                                                  anExpression);

            // if we find an empty delta substitution list we exit here.
            if ( deltas.isEmpty() )
                return deltas;

            /*
             * This part actually contains accumulation.
             * deltas is evolving from one expression to
             * another. And psis stores this evolution
             * to propagate it to next expression in the list.
             */
            psis = deltas;
        }

        // return accumulated subsitution list
        return psis;
    }

    /**
     * This private method acts as entry point to <code>iterateOnRest</code>
     * method. It iterates through given substitutions, applies them to given
     * expression and in case of non empty result, accumulates results. Later
     * on, accumulated results are returned to calling method.
     *
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @param psis List of substitutions produced during resolution of first list entry.
     * @param anExpression An entry of current expression list for which resolution
     *                     is to be performed.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires
     *                              during procedure.
     */
    private List<Substitution> applySubstitutions( RuleScope          scope,
                                                   TimerFlag          flag,
                                                   List<Substitution> psis,
                                                   Expression      anExpression)
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;

        List<Substitution> deltas = new ArrayList<Substitution>();
        for ( Substitution aSubstitution : psis ) {
            List<Substitution> gammas = anExpression.chain( aSubstitution,
                                                            scope,
                                                            flag );
            if ( !gammas.isEmpty() )
                deltas.addAll(gammas);
        }
        return deltas;
    }

    public FuzzyResolution fuzzyEvaluate( FuzzySubstitution sigma,
                                          GameStateScope    scope,
                                          List<Expression>  guard,
                                          TimerFlag         flag )
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;

        Iterator<Expression> iter       = iterator();
        Expression           first      = iter.next();
        FuzzyResolution      resolution = first.fuzzyEvaluate( sigma,
                                                               scope,
                                                               guard,
                                                               flag );
        boolean              onBottom   = resolution.isOnBottom();

        while ( iter.hasNext() ) {
            Expression e = iter.next();

            if (guard.contains(e.canonize()) && onBottom)
                resolution.tNorm(Expression.fuzzyZero);

            else {
                FuzzyResolution fuzzyResolution = handleSingleConjunct(
                                                                    e,
                                                                    resolution,
                                                                    scope,
                                                                    guard,
                                                                    flag );

                resolution.replaceWithNextResolutionStep(fuzzyResolution);
                onBottom = resolution.isOnBottom();
            }

        }

        return resolution;
    }

    private FuzzyResolution handleSingleConjunct( Expression       e,
                                                  FuzzyResolution  sigmas,
                                                  GameStateScope   scope,
                                                  List<Expression> guard,
                                                  TimerFlag        flag )
    throws InterruptedException {
        final FuzzyResolution       currentStage = new FuzzyResolution();
        Iterator<FuzzySubstitution> iter         = sigmas.iterator();

        if ( iter.hasNext() ) {
            FuzzySubstitution aSub = iter.next();
            final FuzzyResolution derived = e.fuzzyEvaluate( aSub, scope, guard, flag );
            currentStage.addAll( derived );
            currentStage.setFuzzyValue( derived.getFuzzyValue() );
        }

        while ( iter.hasNext() ){
            FuzzySubstitution aSub = iter.next();
            handleSingleSubstitution(e, aSub, currentStage, scope, guard, flag);
        }

        return currentStage;
    }

    private void handleSingleSubstitution( Expression            e,
                                           FuzzySubstitution     aSub,
                                           final FuzzyResolution currentStage,
                                           GameStateScope        scope,
                                           List<Expression>      guard,
                                           TimerFlag             flag )
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;

        final FuzzyResolution derived = e.fuzzyEvaluate( aSub, scope, guard, flag );
        currentStage.addAll( derived );
        currentStage.tConorm( derived.getFuzzyValue() );
    }

    /**
     * @return Returns string representation of all
     *         the expression in a list.
     */
    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();
        for (Expression exp : this)
            s.append(exp.toString()).append(' ');

        return s.toString();
    }

    public final void processVisitor(AbstractVisitor visitor) {
        visitor.visitExpressionList(this);
    }

    /**
     *  This method collect all the variables across all the
     *  expressions that are in a list.
     * @return Returns list of variables from all expressions
     *         in current list.
     */
    public List<Variable> getVariables() {
        if (cachedVariables == null) {

            List<Variable> gather = new ArrayList<Variable>();
            for (Expression anExpression : this) {
                if ( anExpression instanceof Atom )
                    continue;

                List<Variable> temp = anExpression.getVariables();
                if (temp != null)
                    gather.addAll(temp);
            }
            cachedVariables = gather;
        }
        return cachedVariables;
    }

    /**
     * This method sequentially applies given substitution to all the
     * elements of the list. Results are collected into yet another
     * <code>ExpressionList</code>.
     * @see de.tu_dresden.inf.ggp06_2.resolver.Expression#apply(Substitution) Expression.apply
     * @param sigma Given substitution
     * @return Returns new <code>ExpressionList</code>, that contains
     *         expressions generated by application of given substitution.
     */
    public ExpressionList apply(Substitution sigma) {

        ExpressionList dup = new ExpressionList();
        for ( Expression anExpression : this )
            dup.add( anExpression.apply(sigma) );
        return dup;
    }

    /**
     * This method check whether a variable is present or not within an
     * expression of this expression list.
     *
     * @param var The variable to check for occurrences
     * @return True if the variable appears in an expression; false otherwise.
     */
    public boolean isPresent(Variable var) {
        for ( Expression anExpression : this )
            if ( anExpression.isPresent(var) )
                return true;

        return false;
    }

    /**
     * This method produces MGU (most general unifier) - a
     * substitution that makes current list of expressions
     * equal to given one, w.r.t. current state of resolution,
     * which is passed as parameter in another substitution.
     *
     * @param target Given expression to compare to
     * @param sigma Current state of resolution
     * @return Returns substitution that makes current expression
     *         equal to a given one and preserves as much variables
     *         as possible.
     */
    public Substitution mgu(ExpressionList target, Substitution sigma) {

        if ( sigma == null || target.size() != size() )
            return null;

        for (int i = 0, iMax = size(); i < iMax; i++) {
            sigma = get(i).mgu(target.get(i), sigma);
            if ( sigma == null )
                return null;
        }
        return sigma;
    }

}
