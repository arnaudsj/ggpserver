package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.ArrayList;
import java.util.List;
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
 * @author Novak Novakovic - General Game Playing course student at TUD
 *
 */
public final class NotOperator extends Expression {
    
    private final Expression operand;
    
    /**
     * Negation operator is unary so it can be initialized only with one argument.
     * @param expression Argument to negate.
     */
    public NotOperator(Expression expression) {
        operand = expression;
    }

    /**
     * Apply given substitution to argument of negation operator,
     * initialize new negation operator with result of application.
     * 
     * @param sigma Substitution to apply.
     * @return New negation operator.
     */
	@Override
	public Expression apply(Substitution sigma) {
        return new NotOperator( operand.apply(sigma) );
	}
    
    public Expression getOperand(){
        return this.operand;
    }

    /**
     * Simply ask for chaining of argument for any resolution result via <code>chainOne</code>
     * method. No resulting substitution is found - return given one, otherwise
     * return empty list of substitutions.
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
		
        if ( flag.interrupted() ) throw Const.interrupt;
		
		List<Substitution> result = new ArrayList<Substitution>();
        if ( operand.chainOne(sigma, scope, flag) == null )
			result.add(sigma);			
		
		return result;
	}

    /**
     * Simply ask for <code>chainOne</code> of argument and inverse the result. 
     * 
     * @see de.tu_dresden.inf.ggp06_2.resolver.NotOperator#chainBody(Substitution, RuleScope, TimerFlag) chainBody
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
		
        if ( flag.interrupted() ) throw Const.interrupt;

        return ( operand.chainOne(sigma, scope, flag) == null ) ? sigma : null;
	}

    @Override
    protected FuzzyResolution fuzzyEvaluateBody(
                                        FuzzySubstitution       sigma,
                                        GameStateScope     scope,
                                        List<Expression>   guard,
                                        TimerFlag          flag ) 
    throws InterruptedException {
        
        //Classical fuzzy negation.
        FuzzyResolution resolution = operand.fuzzyEvaluate( 
                sigma, scope, guard, flag );
        
        resolution.setFuzzyValue(Expression.fuzzyOne - resolution.getFuzzyValue() );
        /* but we NEVER want a zero as an output, so
         * we have to substitute it with (1.0 / Double.MAX_VALUE)
         */ 
        if ( 0.0 >= resolution.getFuzzyValue() ) 
                resolution.setFuzzyValue(Expression.fuzzyZero);
        return resolution;
        
    }

    @Override
    public Atom firstOperand() {
        return Const.aNotOp;
    }

    @Override
    public Substitution mgu(Expression target, Substitution sigma) {
        if (target instanceof Variable)
            return ((Variable) target).mgu(this, sigma);

        return (target instanceof NotOperator ) ?
                        operand.mgu( ((NotOperator) target).operand, sigma) :
                        null;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof NotOperator && 
               operand.equals( ((NotOperator) obj).operand ); 
    }

    @Override
    public String toString() {
        return (toString == null) ?
                    toString = "(NOT " + operand + ")" :
                    toString;
    }
    
    /**
     * There might be two places for variables in a
     * <code>Connective</code>: operator name,
     * and all the operands.
     */
    @Override
    public List<Variable> getVariables() {
        return operand.getVariables();
    }
    
    /**
     * Check whether given variable occurs in current 
     * connective.
     * 
     * @param var Given variable
     * @return True or false ;)
     */
    @Override
    public boolean isPresent(Variable var) {
        return operand.isPresent(var);
    }

    /**
     * @return Returns name of the first operand available
     *         or null if none of them available.
     */
    @Override
    public Term secondOperand() {
        return operand.firstOperand();
    }

    @Override
    public boolean isGround() {        
        return operand.isGround();
    }

    @Override
    public Atom getKeyAtom() {
        return Const.aNotOp;
    }

    @Override
    public int getOperandCount() {
        return 1;
    }

    @Override
    public final void processVisitor(AbstractVisitor visitor) {
        visitor.visitNot(this);
    }
}