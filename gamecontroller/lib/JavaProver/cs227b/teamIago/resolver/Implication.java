/*
 * Created on Apr 19, 2005
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.resolver;

import java.util.ArrayList;

/**
 * @author Nick
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Implication extends Expression {

	protected static final Atom impOp = new Atom("<=");
	protected Expression consequence;
	protected ExpList premises;
	protected boolean amVolatile = false;
	protected static final int IMP_HASH_SEED = 2147006989;
	/**
	 * @param consequence
	 * @param premises
	 */
	public Implication(Expression consequence, ExpList premises) {
		this.consequence = consequence;
		this.premises = premises;
		buildVolatile(false);
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Formula#mgu(cs227b.teamIago.resolver.Formula, cs227b.teamIago.resolver.Substitution)
	 */
	public Substitution mgu(Expression target, Substitution sigma, Theory t) {
		return target.mgu(consequence.apply(sigma),sigma,t);
		//return null;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#apply(cs227b.teamIago.resolver.Substitution)
	 */
	public Expression apply(Substitution sigma) {
		return new Implication(
				consequence.apply(sigma),
				premises.apply(sigma));
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#occurs(cs227b.teamIago.resolver.Variable, cs227b.teamIago.resolver.Substitution)
	 */
	public boolean occurs(Variable var) {
		if (consequence.occurs(var)) return true;
		else return (premises.occurs(var));
	}
	/**
	 * @return Returns the consequence.
	 */
	public Expression getConsequence() {
		return consequence;
	}
	/**
	 * @return Returns the premises.
	 */
	public ExpList getPremises() {
		return premises;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#firstOp()
	 */
	public Term firstOp() {
		return impOp;
	}

	public Term secondOp()
	{
		return consequence.firstOp();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Implication) {
			Implication other = (Implication) obj;
			if (!other.consequence.equals(consequence)) return false;
			return (other.premises.equals(premises));
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chain(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList chain(Substitution sigma, Theory t, boolean cond)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chain(sigma,t, cond);

		t.enterChain(this);
		ArrayList answers = premises.chain(sigma,t, cond);
		boolean proven = ((answers != null) && (answers.size() > 0));
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
		Substitution psi = premises.chainOne(sigma,t, cond);
		boolean proven = (psi != null);
		t.exitChain(this,proven,psi);
		if (proven) return psi;
		else return null;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#eval(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList eval(Substitution sigma, Theory t) {
		// We don't deal with implication
		// for eval.  If we get here, we should just
		// back out.
		t.enterChain(this);
		t.exitChain(this,false,null);
		return null;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#evalOne(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution evalOne(Substitution sigma, Theory t) {
		// We don't deal with implication
		// for eval.  If we get here, we should just
		// back out.
		t.enterChain(this);
		t.exitChain(this,false,null);
		return null;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#getMaxVarNum()
	 */
	public long getMaxVarNum() {
		long conseq,prem;
		conseq = consequence.getMaxVarNum();
		prem = premises.getMaxVarNum();
		if (conseq > prem) return conseq;
		else return prem;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#getVars()
	 */
	public ExpList getVars() {
		ArrayList allVars = new ArrayList();
		allVars.addAll(consequence.getVars().toArrayList());
		allVars.addAll(premises.getVars().toArrayList());
		return new ExpList(allVars);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(<= " + consequence.toString() + " " + premises.toString() + ")";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	
	public int hashCode() {
		return (consequence.hashCode()*IMP_HASH_SEED + impOp.hashCode() + 
				premises.hashCode())* IMP_HASH_SEED  % HASH_QUAD;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#buildVolatile()
	 */
	public boolean buildVolatile(boolean impliedVol) {
		// For Implications, impliedVol will always be false
		// because we can't nest implications
		if (impliedVol) {
			System.err.println("Logic error: nested implication not allowed");
		}
		amVolatile = premises.buildVolatile(false) ||  amVolatile;
		consequence.buildVolatile(amVolatile);
		return amVolatile;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#isVolatile()
	 */
	public boolean isVolatile() {
		return amVolatile;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#mapTo(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Expression)
	 */
	public Substitution mapTo(Substitution sigma, Expression e) {
		Substitution psi = null;
		if (e instanceof Implication) {
			Implication other = (Implication) e;
			psi = consequence.mapTo(sigma,other.consequence);
			if (psi != null) psi = premises.mapTo(psi,other.premises);
		}
		return psi;
	}
}
