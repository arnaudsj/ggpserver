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
 *
 */
public final class DistinctOperator extends Connective {
        
    /**
     * <code>DistinctOperator</code> is initiallized by two expressions. These
     * have to be Terms in fact for operator to produce some effect, otherwise
     * it will just return empty substitutions as results of chaining.
     * @param e1
     * @param e2
     */
	public DistinctOperator(Expression e1, Expression e2) {
	    operands.add( e1 );
        operands.add( e2 );
	}

    /**
     * Just a syntactic sugar over scalable conjunction operation 
     * All of the operands must successfully resolve in order 
     * for operator to be true.
     * @param opArray List of operands to resolve.
     */
    public DistinctOperator(ExpressionList operands) {
        this.operands.addAll( operands ); 
    }

    /**
     * Apply given substitutionn to operands and return new
     * <code>DistinctOperator</code> initialized with the results 
     * of that application.
     * 
     * @param sigma Substitution to apply to current operator.
     * @return Returns new <code>DistinctOperator</code> initialized
     *         operands to which substitution is already applied.
     */
	@Override
	public Expression apply(Substitution sigma) {
        return new DistinctOperator( operands.apply(sigma) );
	}

    /**
     * Simply ask for first available solution via <code>chainOne</code>.
     * If there is one, then return it as only element of the list.
     * @see de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator#chainOneBody(Substitution, RuleScope, TimerFlag) chainOneBody
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

        Substitution psi = chainOne(sigma, scope, flag);
		
        // We _never_ expect list to be null, it might be empty however.
		List<Substitution> answers = new ArrayList<Substitution>();		
		
		if ( psi != null )
			answers.add(psi);		

        return answers;
	}

    /**
     * Take first and second elements of the operands list. If both of them
     * are atoms and both of them are equal - return given substitution,
     * otherwise return null. The overall idea behind this is, that both
     * arguments of <code>DistinctOperator</code> should be substituted
     * by the time resolution gets here. Otherwise, these elements could 
     * not be rally distinct. Connectives and Implications are incomparable 
     * in this sence.
     * 
     * @see de.tu_dresden.inf.ggp06_2.resolver.Atom#equals(Object) Atom.equals
     *  
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns one (first) substitution that was produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */  
	@Override
	protected Substitution chainOneBody(
            Substitution sigma, RuleScope scope, TimerFlag flag) 
    throws InterruptedException {
		Expression e1 = operands.get(0);
		Expression e2 = operands.get(1);
		
		if ((e1 instanceof Atom) && 
				(e2 instanceof Atom)) {
			if (!e1.equals(e2)) {		
				return sigma;
			}
		}

		return null;
	}

    
    @Override
    protected FuzzyResolution fuzzyEvaluateBody(
            FuzzySubstitution sigma, GameStateScope scope, 
            List<Expression> guard, TimerFlag flag) 
    throws InterruptedException {
        Expression e1 = operands.get(0);
        Expression e2 = operands.get(1);
        FuzzyResolution resolution = new FuzzyResolution();
        
        if ((e1 instanceof Atom) && 
                (e2 instanceof Atom)) {
            if (!e1.equals(e2)) {
                sigma.tNorm( Expression.fuzzyOne );
                resolution.add( sigma );
                return resolution;
            }
        }
        sigma.setBottom( true );
        sigma.tNorm( Expression.fuzzyZero );
        resolution.add( sigma );
        return resolution;
    }    

    @Override
    public Atom firstOperand() {
        return Const.aDistinctOp;
    }

    @Override
    public Substitution mgu(Expression target, Substitution sigma) {
        if (target instanceof Variable)
            return ((Variable) target).mgu(this, sigma);

        else if (target instanceof DistinctOperator )
            return operands.mgu( ((DistinctOperator) target).operands, sigma);

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DistinctOperator && 
               operands.equals( ((DistinctOperator) obj).operands ); 
    }

    @Override
    public String toString() {
        return (toString == null) ?
                    toString = "(DISTINCT " + operands + ")" :
                    toString;
    }

    @Override
    public boolean isGround() {
        return operands.get(0).isGround() && operands.get(1).isGround();
    }

    @Override
    public Atom getKeyAtom() {
        return Const.aDistinctOp;
    }

    @Override
    public final void processVisitor(AbstractVisitor visitor) {
        visitor.visitDistinct(this);
    }
}
