package de.tu_dresden.inf.ggp06_2.resolver;

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
 *
 */
public final class AndOperator extends Connective {

    /**
     * Usual constructor for binary and (conjunction opperation).
     * both arguments are assumed to be expressions that
     * successfully resolve in order for the operator to 
     * be true.
     * @param operand1 First operand to resolve.
     * @param operand2 Second operand to resolve.
     */
    public AndOperator(Expression operand1, Expression operand2) {
        operands.add( operand1 );
        operands.add( operand2 );
    }
    
    /**
     * Just a syntactic sugar over scalable conjunction operation 
     * All of the operands must successfully resolve in order 
     * for operator to be true.
     * @param opArray List of operands to resolve.
     */
    public AndOperator(ExpressionList operands) {
        this.operands.addAll( operands ); 
    }

    /**
     * Apply substitution to premises of current operator, 
     * which produces list of new premises. These new premises
     * are used to create new &quot;and&quot; operator, which
     * is returnted as result.
     * @return Returns a new operator, obtained 
     *         from a current one, by application
     *         of substitution.
     */
	@Override
	public Expression apply(Substitution sigma) {
        return new AndOperator(operands.apply(sigma));
	}

    /**
     * Simply ask for chaining of premises, which are stored as <code>ExpressionList</code>
     * @see de.tu_dresden.inf.ggp06_2.resolver.Expression#chainBody(Substitution, RuleScope, TimerFlag) Expression.chainBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.ExpressionList#chain(Substitution, RuleScope, TimerFlag) ExpressionList.chain
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
		return operands.chain(sigma, scope, flag);
	}

    /**
     * Simply ask for <code>chainOne</code> of premises, which are stored as <code>ExpressionList</code>
     * @see de.tu_dresden.inf.ggp06_2.resolver.Expression#chainOneBody(Substitution, RuleScope, TimerFlag) Expression.chainOneBody
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
		return operands.chainOne(sigma, scope, flag);
	}

    @Override
    protected FuzzyResolution fuzzyEvaluateBody( 
                                        FuzzySubstitution       sigma,                                         
                                        GameStateScope     scope, 
                                        List<Expression>   guard,
                                        TimerFlag          flag ) 
    throws InterruptedException {        
        return operands.fuzzyEvaluate( sigma, scope, guard, flag );
    }    

    @Override
    public Atom firstOperand() {
        return Const.aAndOp;
    }
    
    @Override
    public Substitution mgu(Expression target, Substitution sigma) {
        if (target instanceof Variable)
            return ((Variable) target).mgu(this, sigma);

        else if (target instanceof AndOperator )
            return operands.mgu( ((AndOperator) target).operands, sigma);

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AndOperator && 
               operands.equals( ((AndOperator) obj).operands ); 
    }

    @Override
    public String toString() {
        return (toString == null) ?
                    toString = "(AND " + operands + ")" :
                    toString;
    }

    @Override
    public boolean isGround() {
        for (Expression exp: operands)
            if ( !exp.isGround() )
            return false;
        return true;
    }

    @Override
    public Atom getKeyAtom() {
        return Const.aAndOp;
    }

    @Override
    public final void processVisitor(AbstractVisitor visitor) {
        visitor.visitAnd(this);
    }

    
}