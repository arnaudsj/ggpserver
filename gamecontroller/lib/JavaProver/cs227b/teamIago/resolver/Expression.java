/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.resolver;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Expression implements Serializable {
	public abstract Substitution mgu(Expression target, Substitution in, Theory t);
	public abstract Expression apply(Substitution sigma);
	protected final int HASH_RES_1 = 46147;
	protected final int HASH_RES_2 = 46511; // product gives hash_quad
	public static final int HASH_QUAD = 2146343117; //MAX_VALUE = 2147483647
	/* occurs
	 * @param var 
	 * 		The variable to check for occurrences
	 * @param sigma
	 * 		The current substitution
	 * 
	 * returns true if the variable appears in the
	 * current expression; false otherwise.
	 */
	public abstract boolean occurs(Variable var);
	public abstract Term firstOp();
	public abstract Term secondOp();
	public abstract ArrayList chain(Substitution sigma, Theory t, boolean cond) throws InterruptedException;
	public abstract Substitution chainOne(Substitution sigma, Theory t, boolean cond) throws InterruptedException;
	public abstract ArrayList eval(Substitution sigma, Theory t) throws InterruptedException;
	public abstract Substitution evalOne(Substitution sigma, Theory t) throws InterruptedException;
	public abstract long getMaxVarNum();
	public abstract ExpList getVars();
	public abstract boolean  buildVolatile(boolean impliedVol);
	public abstract boolean isVolatile();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public abstract boolean equals(Object obj);
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	//public abstract int hashCode();
	public int hashCode() {
		return toString().hashCode();
	}
	
	public abstract Substitution mapTo(Substitution sigma, Expression e);
	
	protected Expression ground(Theory t, Substitution sigma) {
		Expression s = apply(sigma);
		Expression s2 = this;
		while (!s.equals(s2)) {
			if (t.interrupted()) return null;
			s2 = s;
			s = s2.apply(sigma);
		}
		if (t.interrupted()) return null;
		else return s;
	}
	
}
