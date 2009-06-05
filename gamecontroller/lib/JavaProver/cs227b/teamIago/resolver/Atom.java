/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.resolver;

import java.util.HashMap;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Atom extends Term {
	protected class VolObj {
		protected boolean trans;
		
		/**
		 * @param trans
		 */
		public VolObj(boolean trans) {
			super();
			this.trans = trans;
		}
		/**
		 * @return Returns the trans.
		 */
		public boolean isVolatile() {
			return trans;
		}
		/**
		 * @param trans The trans to set.
		 */
		public void setVolatile(boolean trans) {
			this.trans = trans;
		}
	}
	protected static HashMap volatileTable = new HashMap();
	// FIXME: This table has two disadvantages:
	// 1. It is only written to, never emptied -- memory hog
	// 2. Since it is static, it causes information to be shared among all instances
	//    of the JavaProver, including those instances of other games that are started
	//    later.
	//              --- Martin Guenther
	
	protected VolObj volObj;

	protected String literal;
	protected final int ATOM_HASH_SEED = 2147002129;
	/**
	 * @param literal
	 */
	public Atom(String literal) {
		this.literal = literal.toUpperCase();
		volObj = (VolObj) volatileTable.get(this.literal);
		if (volObj == null) {
			volObj = new VolObj(false);
			volatileTable.put(this.literal,volObj);
		}		
	}
	/**
	 * @return Returns the literal.
	 */
	public String getLiteral() {
		return literal;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Formula#mgu(cs227b.teamIago.resolver.Formula, cs227b.teamIago.resolver.Substitution)
	 */
	public Substitution mgu(Expression target, Substitution sigma, Theory t) {
		if (target instanceof Atom) {
			if (target.equals(this)) return sigma;
			else return null;
		}
		else if (target instanceof Variable) {
			Variable v = (Variable) target;
			return v.mgu(this,sigma,t);
		}
/*	TODO: find a different way to do this.
		else if (target instanceof Implication) {
			Implication imp = (Implication) target;
			return mgu(imp.getConsequence(),sigma);
		}
*/
		else return null;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#apply(cs227b.teamIago.resolver.Substitution)
	 */
	public Expression apply(Substitution sigma) {
		return this;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#occurs(cs227b.teamIago.resolver.Variable, cs227b.teamIago.resolver.Substitution)
	 */
	public boolean occurs(Variable var) {
		// no variables in a literal.
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Atom)) return false;
		return ((Atom)o).literal.equals(literal);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */

	public int hashCode() {
		return literal.hashCode() * ATOM_HASH_SEED % HASH_QUAD;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#getMaxVarNum()
	 */
	public long getMaxVarNum() {
		return Long.MIN_VALUE;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#getVars()
	 */
	public ExpList getVars() {
		return new ExpList();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return literal;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#buildVolatile(boolean)
	 */
	public boolean buildVolatile(boolean impliedVol) {
		// If it's implied volatile, it is.
		// otherwise, no.
		// Note that the words "true" and "does" aren't
		// *inherently* volatile--only as the operators in
		// predicates.  In that case, our
		// owner will tell us.
		boolean vol = volObj.isVolatile();
		vol = vol || impliedVol;
		volObj.setVolatile(vol);
		return vol;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#isVolatile()
	 */
	public boolean isVolatile() {
		// TODO Auto-generated method stub
		return volObj.isVolatile();
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#mapTo(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Expression)
	 */
	public Substitution mapTo(Substitution sigma, Expression e) {
		if (e == null) return null;
		if (!(e instanceof Atom)) return null;
		Atom other = (Atom) e;
		if (this.literal.equals(other.literal)) return sigma;
		return null;
	}
}
