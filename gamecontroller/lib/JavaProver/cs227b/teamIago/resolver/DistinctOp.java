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
public class DistinctOp extends Connective {
	protected static final Atom distinctOperator = new Atom("DISTINCT");
	protected static final int DISTINCT_OPERATOR_HASH_SEED = 2147002933;
	protected static final int DISTINCT_OPERANDS_HASH_SEED = 2147003003;
	protected static final int DISTINCT_OPERANDS_HASH_MUL = 2147003143;

	/**
	 * @param e1
	 * @param e2
	 */
	public DistinctOp(Expression e1, Expression e2) {
		super();
		addOperand(e1);
		addOperand(e2);
		setOperator(distinctOperator);
		buildVolatile(false);
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#apply(cs227b.teamIago.resolver.Substitution)
	 */
	public Expression apply(Substitution sigma) {
		if (operands.size() < 2) 
		{
			System.err.println("Error: 'Apply' called on 'Distinct' with undefined operands.");
			return null;
		}
		Expression e1 = operands.get(0);
		Expression e2 = operands.get(1);
		return new DistinctOp(e1.apply(sigma),e2.apply(sigma));
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chain(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList chain(Substitution sigma, Theory t, boolean cond)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chain(sigma,t, cond);
		t.enterChain(this);

		Substitution psi = chainOne(sigma, t, cond);
		if (psi == null) {
			t.exitChain(this,false,null);
			return null;
		}
		ArrayList answers = new ArrayList();
		answers.add(psi);
		t.exitChain(this,true,answers);
		return answers;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chainOne(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution chainOne(Substitution sigma, Theory t, boolean cond)  throws InterruptedException {
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chainOne(sigma,t, cond);
		t.enterChain(this);

		Expression e1 = operands.get(0);
		Expression e2 = operands.get(1);
		
		if ((e1 instanceof Atom) && (e2 instanceof Atom))
			if (!e1.equals(e2)) {
				t.exitChain(this,true,sigma);
				return sigma;
			}
		t.exitChain(this,false,null);
		return null;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#eval(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList eval(Substitution sigma, Theory t)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.eval(sigma,t);
		t.enterChain(this);

		Substitution psi = evalOne(sigma, t);
		if (psi == null) {
			t.exitChain(this,false,null);
			return null;
		}
		ArrayList answers = new ArrayList();
		answers.add(psi);
		t.exitChain(this,true,answers);
		return answers;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#evalOne(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution evalOne(Substitution sigma, Theory t)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();	
		if (!s.equals(this)) return s.evalOne(sigma,t);
		t.enterChain(this);

		Expression e1 = operands.get(0);
		Expression e2 = operands.get(1);
		
		if ((e1 instanceof Atom) && (e2 instanceof Atom))
			if (!e1.equals(e2)) {
				t.exitChain(this,true,sigma);
				return sigma;
			}
		t.exitChain(this,false,null);
		return null;
	}
	
	public boolean buildVolatile(boolean impliedVol) {
		// Since distinct only works on atoms,
		// it should never be volatile.
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (operator.hashCode()*DISTINCT_OPERATOR_HASH_SEED + 
				hashProd(operands,DISTINCT_OPERANDS_HASH_MUL,DISTINCT_OPERANDS_HASH_SEED))
				*DISTINCT_OPERATOR_HASH_SEED % HASH_QUAD;
	}
	
	public Substitution mapTo(Substitution sigma, Expression e) {
		Substitution psi = null;
		if (e instanceof DistinctOp) {
			DistinctOp other = (DistinctOp) e;
			psi = operator.mapTo(sigma,other.operator);
			if (psi != null) psi = operands.mapTo(psi,other.operands);
		}
		return psi;
	}

}
