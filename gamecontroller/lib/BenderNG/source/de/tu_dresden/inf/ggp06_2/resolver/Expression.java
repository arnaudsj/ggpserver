package de.tu_dresden.inf.ggp06_2.resolver;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

import java.util.ArrayList;
import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.astvisitors.AbstractVisitor;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.RuleScope;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

/**
 * This is one of the very basic classes for theory construction. Three direct
 * children are: Connective, Implication and Term. Use Eclipse Hierarchy window
 * to see the complete picture. :)
 *
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 * @author Novak Novakovic - General Game Playing course student at TUD
 */
public abstract class Expression {

    protected String  toString = null;

    /**
     * This method produces MGU (most general unifier) - a substitution that
     * makes current expression equal to given one, w.r.t. current state of
     * resolution, which is passed as parameter in another substitution.
     *
     * @param target Given expression to compare to
     * @param in Current state of resolution
     * @return Returns substitution that makes current expression
     *         equal to a given one and preserves as much variables
     *         as possible. In the case of a fail it returns null.
     */
    public abstract Substitution mgu(Expression target, Substitution in);

    /**
     * This method applies given substitution to current expression.
     *
     * @param sigma Given substitution.
     * @return Returns new expression produced via application
     *         of given substitution to current expression.
     */
	public abstract Expression apply(Substitution sigma);

    /**
     * This method checks wether an expression is ground or not.
     * @return Returns true if expression is ground; otherwise false.
     */
	public abstract boolean isGround();


    /**
     * This method returns the key atom of an expression. This means that this
     * atom is used for the mapping within theory class.
     * @return Atom which is the key for this expression.
     */
    public abstract Atom getKeyAtom();

    /**
     * This method returns the number of operands of a concrete Expression.
     *
     * Here are the expected outcomes:
     * <ul>
     *   <li>0 - Term derived classes</li>
     *   <li>1 - NotOperand</li>
     *   <li>2 - DistinctOperand</li>
     *   <li>n - Connective derived classes; essentially operands.size()</li>
     *   <li>n - Implication returns consequence.getOperandCount()</li>
     * </ul>
     * @return
     */
    public abstract int getOperandCount();

    /**
     * This method check wether a variable is present or not within the current
     * expression.
     *
	 * @param var The variable to check for occurrences
	 * @return True if the variable appears in the current expression; false
     *         otherwise.
	 */
	public abstract boolean isPresent(Variable var);

    /**
     * Get first operand of current expression, i.e. word
     * &quot;and&quot; for conjunction operator.
     * @return Returns term that definies current expression.
     */
	public abstract Term firstOperand();

    /**
     * Get second operand of current expression, i.e.
     * word &quot;cell&quot; for expression like
     * <i>&quot;( true (cell 1) )&quot;</i>
     * @return Return term that defines first operand among
     *         premises of current expression or <b>null</b>
     *         if none.
     */
	public abstract Term secondOperand();


    /**
     * This method has to be implemented by the final subclasses.
     * @param visitor
     */
    public abstract void processVisitor(AbstractVisitor visitor);

    /**
	 * There are two similar methods: chain and chainOne. Each of them is using
     * the same technique for progression over the tree. This technique may be
     * expressed as follows:
	 * <ol>
     *   <li>
     *     Check for interrupted flag within Theory class.
     *   </li>
	 *   <li>
     *      Apply the sigma to the current expression to get a ground version.
     *   </li>
	 *   <li>
     *     Check whether ground expression is equal to currently
     *     chained/evaluated expression and
	 *     <ol>
	 *       <li>
     *         Delegate chaining/evaluation to ground expression, if the are
     *         NOT equal.
     *       </li>
	 *       <li>Continue otherwice</li>
	 *     </ol>
	 *   </li>
	 *   <li>
     *     Continue with corresponding "body" method (whereas "body" methods
     *     differ from instance to instance)
     *   </li>
	 * </ol>
     *
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
	 */
	public List<Substitution> chain( Substitution sigma,
                                     RuleScope    scope,
                                     TimerFlag    flag )
    throws InterruptedException{
        if ( flag.interrupted() ) throw Const.interrupt;

	    Expression s = this.apply(sigma);

        // if that is different to current expression
        // chaining is passed to a ground version
        return !s.equals(this) ? s.chain  (sigma, scope, flag):
                                 chainBody(sigma, scope, flag);
	}

    public static final double fuzzyZero = 0.123456789;
    public static final double fuzzyOne  = (1 - fuzzyZero);
    public static final double threshold = 0.60;

    public final static double tConorm(double a, double b){
        return 1.0 - tNorm(1.0-a, 1.0-b);
    }

    public final static double tNorm(double a, double b) {
        return (min( a, b ) > 0.5 ) ? max(t(a,b), threshold) : t( a, b );
    }

    public static double t(double a, double b) {
        return 1.0 - s( 1-a, 1-b );
    }

    public static double s(double a, double b) {
        double q   = 7;
        double a_q = pow( a, q );
        double b_q = pow( b, q);
        double s   = pow(a_q+b_q, 1/q);
        double div = Math.pow(2, 1/q);
        return min(s, fuzzyOne + (s/div)*(1.0-fuzzyOne));
    }

    /**
     * This method actually performes all the chaining machinery specific to each
     * type of expressions. See each concrete implementation in particular.
     * @see de.tu_dresden.inf.ggp06_2.resolver.AndOperator#chainBody(Substitution, RuleScope, TimerFlag) AndOperator.chainBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.OrOperator#chainBody(Substitution, RuleScope, TimerFlag) OrOperator.chainBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.NotOperator#chainBody(Substitution, RuleScope, TimerFlag) NotOperator.chainBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator#chainBody(Substitution, RuleScope, TimerFlag) DistinctOperator.chainBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Predicate#chainBody(Substitution, RuleScope, TimerFlag) Predicate.chainBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Implication#chainBody(Substitution, RuleScope, TimerFlag) Implication.chainBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Atom#chainBody(Substitution, RuleScope, TimerFlag) Atom.chainBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Term#chainBody(Substitution, RuleScope, TimerFlag) Term.chainBody
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
	protected abstract List<Substitution> chainBody( Substitution sigma,
                                                     RuleScope    scope,
                                                     TimerFlag    flag )
    throws InterruptedException;

    /**
     * This method is another entering point for a chain resolution, but this time only
     * one (first) solution is reported.
     * @see de.tu_dresden.inf.ggp06_2.resolver.Expression#chain(Substitution, RuleScope, TimerFlag) chain
     * @param sigma Current state of resulution
     * @param scope Scope of GDL rules used for resulution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns a substitution that was produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
	public Substitution chainOne(Substitution sigma, RuleScope scope, TimerFlag flag) throws InterruptedException{
        if ( flag.interrupted() ) throw Const.interrupt;

        Expression s = this.apply( sigma );
        if ( !s.equals(this) )
            return s.chainOne(sigma, scope, flag);

		return chainOneBody(sigma, scope, flag);
	}

    /**
     * This method hides all the machinery of <code>chainOne</code> method specific
     * to concrete implementations of Expressions.
     * @see de.tu_dresden.inf.ggp06_2.resolver.AndOperator#chainOneBody(Substitution, RuleScope, TimerFlag) AndOperator.chainOneBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.OrOperator#chainOneBody(Substitution, RuleScope, TimerFlag) OrOperator.chainOneBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.NotOperator#chainOneBody(Substitution, RuleScope, TimerFlag) NotOperator.chainOneBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator#chainOneBody(Substitution, RuleScope, TimerFlag) DistinctOperator.chainOneBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Predicate#chainOneBody(Substitution, RuleScope, TimerFlag) Predicate.chainOneBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Implication#chainOneBody(Substitution, RuleScope, TimerFlag) Implication.chainOneBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Atom#chainOneBody(Substitution, RuleScope, TimerFlag) Atom.chainOneBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Term#chainOneBody(Substitution, RuleScope, TimerFlag) Term.chainOneBody
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns one (first) substitution that was produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
	protected abstract Substitution chainOneBody(
            Substitution sigma, RuleScope scope, TimerFlag flag) throws InterruptedException;

    /**
     * This method performs fuzzy evaluation of an expression against
     * some GDL rule-scope. In fact, the initial idea was to evaluate
     * terminal and goal statements, so the scope is ment to be an
     * instance of <code>GameStateScope</code>
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    public FuzzyResolution fuzzyEvaluate(
            FuzzySubstitution sigma, GameStateScope scope,
            List<Expression> guard, TimerFlag flag)
    throws InterruptedException{
        return this.apply( sigma ).fuzzyEvaluateBody(sigma, scope, guard, flag);
    }

    /**
     * This method actually performes all the chaining machinery of <code>fuzzyEvaluate</code>
     * specific to each type of expressions. See each concrete implementation in particular.
     * @see de.tu_dresden.inf.ggp06_2.resolver.AndOperator#fuzzyEvaluateBody(Substitution, RuleScope, TimerFlag) AndOperator.evalBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.OrOperator#fuzzyEvaluateBody(Substitution, RuleScope, TimerFlag) OrOperator.evalBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.NotOperator#fuzzyEvaluateBody(Substitution, RuleScope, TimerFlag) NotOperator.evalBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator#fuzzyEvaluateBody(Substitution, RuleScope, TimerFlag) DistinctOperator.evalBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Predicate#fuzzyEvaluateBody(Substitution, RuleScope, TimerFlag) Predicate.evalBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Implication#fuzzyEvaluateBody(Substitution, RuleScope, TimerFlag) Implication.evalBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Atom#fuzzyEvaluateBody(Substitution, RuleScope, TimerFlag) Atom.evalBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.Term#fuzzyEvaluateBody(Substitution, RuleScope, TimerFlag) Term.evalBody
     * @param sigma Current state of resolution
     * @param sigmas Total list of possible resolutions at this stage.
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    protected abstract FuzzyResolution fuzzyEvaluateBody(
            FuzzySubstitution sigma, GameStateScope scope,
            List<Expression> guard, TimerFlag flag)
    throws InterruptedException;

    /**
     * This method returns all variables used within this expression. It is used
     * for example in the substitution class for restricting a substitution to a
     * domain.
     *
     * @see Substitution#restrict(Expression)
	 */
	public abstract List<Variable> getVariables();

	/**
     * @return Returns a hash code of current expression.
     *         hash code is obtained via turning expression into
     *         string and then taking hash code of that string.
	 */
	@Override
    public int hashCode() {
		return toString().hashCode();
	}


    /**
     * This method returns a substitution that is either empty if this expression
     * does not have a variable or the substitution contains an association
     * between a variable from the expression to a new variable for each
     * variable within this expression.
     * @return
     */
    public Substitution uniquifier() {

        if ( getVariables() == null )
            return new Substitution();

        Substitution   uni    = new Substitution();
        List<Variable> culled = new ArrayList<Variable>();
        List<Variable> vars   = getVariables();

        // iterate over list entries and add only once
        for ( Variable aVar : vars )
            if ( !culled.contains(aVar) ) {
                culled.add( aVar );
                uni.addAssociation( aVar, new Variable() );
            }

        return uni;
    }

   /* No need for this class
    * public Expression toDNF(){
        if (this instanceof Implication){
            return new Implication(((Implication)this).consequence,
                    ((Implication)this).getPremises().flattenExpresionList());
        }
        Expression nnf = this.moveNegationToAtoms();
        nnf.reorderConnectives();
        return nnf;
    }*/
    /* No need for this class
    public Expression reorderConnectives(){
        if (this instanceof OrOperator){
            return new OrOperator(((Connective)this).getOperands().get( 0 ).reorderConnectives(),
                        ((Connective)this).getOperands().get( 1 ).reorderConnectives());
        }
        if (this instanceof AndOperator){
            Expression firstOperand = ((Connective)this).getOperands().get( 0 ).reorderConnectives();
            Expression secondOperand = ((Connective)this).getOperands().get( 0 ).reorderConnectives();
            if (firstOperand instanceof OrOperator){
                Expression aux1 = new AndOperator(((Connective)firstOperand).getOperands().get( 0 ),
                        secondOperand);
                Expression aux2 = new AndOperator(((Connective)firstOperand).getOperands().get( 1 ),
                        secondOperand);
                return new OrOperator(aux1.reorderConnectives(), aux2.reorderConnectives());
            }
            if (secondOperand instanceof OrOperator){
                Expression aux1 = new AndOperator(((Connective)secondOperand).getOperands().get( 0 ),
                        firstOperand);
                Expression aux2 = new AndOperator(((Connective)firstOperand).getOperands().get( 1 ),
                        firstOperand);
                return new OrOperator(aux1.reorderConnectives(), aux2.reorderConnectives());
            }
            return new AndOperator(firstOperand, secondOperand);
        }
        return new Predicate((Atom)(this.firstOperand()),
                ((Predicate)this).getOperands());
    } */

    public Expression canonize() {
        return apply( deriveCanon() );
    }

    public Substitution deriveCanon() {
        List<Variable> vars  = this.getVariables();
        Substitution   canon = new Substitution();
        int i = 0;
        for (Variable var : vars)
            canon.addAssociation( var, new Variable("?var" + i++) );

        return canon;
    }
}