/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.resolver;

import java.util.ArrayList;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AndOp extends Connective {
	protected static final Atom andOperator = new Atom("AND");
	protected static final int AND_OPERATOR_HASH_SEED = 2147002279;
	protected static final int AND_OPERANDS_HASH_SEED = 2147002519;
	protected static final int AND_OPERANDS_HASH_MUL = 2147002751;
	
	/**
	 * @param operator
	 * @param operands
	 */
	public AndOp(Expression e1, Expression e2) {
		super();
		addOperand(e1);
		addOperand(e2);
		setOperator(andOperator);
		buildVolatile(false);
	}
	
	public AndOp(Expression[] opArray) {
		this(new ExpList(opArray));
		buildVolatile(false);
	}

	public AndOp(ExpList operands) {
		super();
		setOperands(operands);
		setOperator(andOperator);
		buildVolatile(false);
	}
	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#apply(cs227b.teamIago.resolver.Substitution)
	 */
	public Expression apply(Substitution sigma) {
		return new AndOp(operands.apply(sigma));
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chain(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList chain(Substitution sigma, Theory t, boolean cond)  throws InterruptedException {
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chain(sigma,t,cond);

		t.enterChain(this);
		ArrayList answers = operands.chain(sigma,t, cond);
		if ((answers == null) || (answers.size()==0))
		{
			t.exitChain(this,false,null);
			return null;
		}
		else
		{
			t.exitChain(this,true,answers);
			return answers;
		}
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chainOne(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution chainOne(Substitution sigma, Theory t, boolean cond)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chainOne(sigma,t,cond);

		t.enterChain(this);
		Substitution psi = operands.chainOne(sigma,t, cond);
		if (psi == null)
		{
			t.exitChain(this,false,null);
			return null;
		}
		else
		{
			t.exitChain(this,true,psi);
			return psi;
		}
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#eval(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList eval(Substitution sigma, Theory t)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.eval(sigma,t);

		t.enterChain(this);
		ArrayList answers = operands.eval(sigma,t);
		if ((answers == null) || (answers.size()==0))
		{
			t.exitChain(this,false,null);
			return null;
		}
		else
		{
			t.exitChain(this,true,answers);
			return answers;
		}
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#evalOne(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution evalOne(Substitution sigma, Theory t)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.evalOne(sigma,t);

		t.enterChain(this);
		Substitution psi = operands.evalOne(sigma,t);
		if (psi == null)
		{
			t.exitChain(this,false,null);
			return null;
		}
		else
		{
			t.exitChain(this,true,psi);
			return psi;
		}
	}
	
	public boolean buildVolatile(boolean impliedVol) {
		amVolatile = amVolatile || impliedVol;
		// The operator here is AND,
		// which is not volatile.
		
		// However, the volatility of the AndOp
		// depends on the volatility of the operands.
		amVolatile = operands.buildVolatile(false) || amVolatile;
		return amVolatile;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (operator.hashCode()*AND_OPERATOR_HASH_SEED + 
			hashProd(operands,AND_OPERANDS_HASH_MUL,AND_OPERANDS_HASH_SEED))
			*AND_OPERATOR_HASH_SEED % HASH_QUAD;
	}
	
	public Substitution mapTo(Substitution sigma, Expression e) {
		Substitution psi = null;
		if (e instanceof AndOp) {
			AndOp other = (AndOp) e;
			psi = operator.mapTo(sigma,other.operator);
			if (psi != null) psi = operands.mapTo(psi,other.operands);
		}
		return psi;
	}
}
