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
public class OrOp extends Connective {
	protected static final Atom orOperator = new Atom("OR");
	protected static final int OR_OPERATOR_HASH_SEED = 2147004323;
	protected static final int OR_OPERANDS_HASH_SEED = 2147004521;
	protected static final int OR_OPERANDS_HASH_MUL = 2147004779;
	
	/**
	 * @param e1
	 * @param e2
	 */
	public OrOp(Expression e1, Expression e2) {
		super();
		addOperand(e1);
		addOperand(e2);
		setOperator(orOperator);
		buildVolatile(false);
	}
	
	public OrOp(Expression[] opArray) {
		this(new ExpList(opArray));
	}

	public OrOp(ExpList operands) {
		super();
		setOperands(operands);
		setOperator(orOperator);
		buildVolatile(false);
	}
	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#apply(cs227b.teamIago.resolver.Substitution)
	 */
	public Expression apply(Substitution sigma) {
		return new OrOp(operands.apply(sigma));
	}
	
	
	protected void addAllNew(ArrayList src, ArrayList dest) {
		if (src == null) return;
		if (dest == null) {
			dest = (ArrayList) src.clone();
			return;
		}
		for (int i = 0; i < src.size(); ++i) {
			Object o = src.get(i);
			if (!dest.contains(o)) dest.add(o);
		}
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chain(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList chain(Substitution sigma, Theory t, boolean cond)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chain(sigma,t, cond);

		t.enterChain(this);
		ArrayList answers = new ArrayList();
		for (int i = 0; i < operands.size(); i++)
		{
			Expression e = operands.get(i);
			if (t.interrupted()) throw new InterruptedException();
			ArrayList psis = e.chain(sigma,t, cond);
			//  TODO: Check if this speeds up proving 
			// if (psis != null) answers.addAll(psis);
			addAllNew(psis,answers);
		}
	
		boolean proven = (answers.size() != 0);
		t.exitChain(this,proven,answers);
		if (proven) return answers;
		else return null;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chainOne(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution chainOne(Substitution sigma, Theory t, boolean cond)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chainOne(sigma,t, cond);

		t.enterChain(this);
		for (int i = 0; i < operands.size(); i++)
		{
			Expression e = operands.get(i);
			if (t.interrupted()) throw new InterruptedException();
			Substitution psi = e.chainOne(sigma,t, cond);
			if (psi != null) {
				t.exitChain(this,true,psi);
				return psi;
			}
		}
		t.exitChain(this,false,null);
		return null;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#eval(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList eval(Substitution sigma, Theory t) throws InterruptedException {
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.eval(sigma,t);
		
		t.enterChain(this);
		ArrayList answers = new ArrayList();
		for (int i = 0; i < operands.size(); i++)
		{
			Expression e = operands.get(i);
			if (t.interrupted()) throw new InterruptedException();
			ArrayList psis = e.eval(sigma,t);
			//  TODO: Check if this speeds up proving 
			//if (psis != null) answers.addAll(psis);
			addAllNew(psis,answers);
		}
	
		boolean proven = (answers.size() != 0);
		t.exitChain(this,proven,answers);
		if (proven) return answers;
		else return null;

	}
	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#evalOne(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution evalOne(Substitution sigma, Theory t)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.evalOne(sigma,t);		
		
		t.enterChain(this);
		for (int i = 0; i < operands.size(); i++)
		{
			Expression e = operands.get(i);
			if (t.interrupted()) throw new InterruptedException();
			Substitution psi = e.evalOne(sigma,t);
			if (psi != null) {
				t.exitChain(this,true,psi);
				return psi;
			}
		}
		t.exitChain(this,false,null);
		return null;
	}
	
	public boolean buildVolatile(boolean impliedVol) {
		amVolatile = amVolatile || impliedVol;
		// The operator here is OR,
		// which is not volatile.
		
		// However, the volatility of the OrOp
		// depends on the volatility of the operands.
		amVolatile = operands.buildVolatile(false) || amVolatile;
		return amVolatile;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (operator.hashCode()*OR_OPERATOR_HASH_SEED + 
				hashProd(operands,OR_OPERANDS_HASH_MUL,OR_OPERANDS_HASH_SEED))
				*OR_OPERATOR_HASH_SEED % HASH_QUAD;

	}
	
	public Substitution mapTo(Substitution sigma, Expression e) {
		Substitution psi = null;
		if (e instanceof OrOp) {
			OrOp other = (OrOp) e;
			psi = operator.mapTo(sigma,other.operator);
			if (psi != null) psi = operands.mapTo(psi,other.operands);
		}				
		return psi;
	}

}
