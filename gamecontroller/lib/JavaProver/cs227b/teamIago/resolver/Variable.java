/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.resolver;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Variable extends Term {
	// protected Atom name;
	protected long varNum;
	protected final int VAR_HASH_SEED = 2147001823;
	/**
	 * @param name
	 */
	public Variable(long varNum) {
		this.varNum = varNum;
	}
	
	public Variable(Atom name) {
		this.varNum = name.hashCode();
	}
	
	public Variable(String name){
		this(new Atom(name));
	}
	
	public Variable(Variable copy) {
		this.varNum = copy.varNum;
	}
	/**
	 * @return Returns the name.
	 */
	public long getNum() {
		return varNum;
	}
	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Formula#mgu(cs227b.teamIago.resolver.Formula, cs227b.teamIago.resolver.Substitution)
	 */
	public Substitution mgu(Expression target, Substitution sigma, Theory t) {
		Expression subThis = apply(sigma);
		Expression subThis2 = this;
		while (!subThis.equals(subThis2)) {
			subThis2 = subThis;
			subThis = subThis2.apply(sigma);
		}
		Expression subTarget = target.apply(sigma);
		Expression subTarget2 = target;
		while (!subTarget.equals(subTarget2)) {
			subTarget2 = subTarget;
			subTarget = subTarget2.apply(sigma);
		}
		if (!subThis.equals(this)) return subThis.mgu(subTarget,sigma,t);
		else if (subTarget.occurs(this)) return null;
		else if (subTarget instanceof Variable) {
			Variable newV = t.generateVar();
			Substitution psi = new Substitution(sigma);
			psi.addAssoc(this,newV);
			psi.addAssoc((Variable)subTarget,newV);
			return psi;
		}
		else
		{
			Substitution psi = new Substitution(sigma);
			psi.addAssoc(this,subTarget);
			return psi;
		}
	}
	
	
	public Substitution mapTo(Substitution sigma, Expression e) {
		Expression subThis = apply(sigma);
		Expression subThis2 = this;
		while (!subThis.equals(subThis2)) {
			subThis2 = subThis;
			subThis = subThis2.apply(sigma);
		}
		Expression subTarget = e.apply(sigma);
		Expression subTarget2 = e;
		while (!subTarget.equals(subTarget2)) {
			subTarget2 = subTarget;
			subTarget = subTarget2.apply(sigma);
		}
		if (!subThis.equals(this)) {
			return subThis.mapTo(sigma,subTarget);
		}
		else
		{
			Substitution psi = new Substitution(sigma);
			psi.addAssoc(this,subTarget);
			return psi;
		}
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#apply(cs227b.teamIago.resolver.Substitution)
	 */
	public Expression apply(Substitution sigma) {
		if (sigma == null) return this;
		Expression e = sigma.maps(this);
		if (e == null) return this;
		else return e;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#occurs(cs227b.teamIago.resolver.Variable, cs227b.teamIago.resolver.Substitution)
	 */
	public boolean occurs(Variable var) {
		if (equals(var)) return true;
		else return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Variable)) return false;
		return ((Variable) o).varNum == varNum;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#getMaxVarNum()
	 */
	public long getMaxVarNum() {
		// TODO Auto-generated method stub
		return varNum;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#getVars()
	 */
	public ExpList getVars() {
		ExpList ret = new ExpList();
		ret.add(this);
		return ret;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Var#(" + varNum + ")";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */

	public int hashCode() {
		return (int) varNum * VAR_HASH_SEED % HASH_QUAD;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#buildVolatile()
	 */
	public boolean buildVolatile(boolean impliedVol) {
		return true;  // variables are always volatile
	}
	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#isVolatile()
	 */
	public boolean isVolatile() {
		return true;  // variables are always volatile
	}
}
