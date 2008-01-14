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
public class Association {
	Variable var;
	Expression sub;
	
	/**
	 * @param v
	 * @param sub
	 */
	public Association(Variable v, Expression sub) {
		this.var = v;
		this.sub = sub;
	}
	
	
	/**
	 * @return Returns the sub.
	 */
	public Expression getSub() {
		return sub;
	}
	/**
	 * @param sub The sub to set.
	 */
	public void setSub(Expression sub) {
		this.sub = sub;
	}
	/**
	 * @return Returns the v.
	 */
	public Variable getVar() {
		return var;
	}
	/**
	 * @param v The v to set.
	 */
	public void setVar(Variable v) {
		this.var = v;
	}
	
	public boolean assigns(Variable v) {
		return this.var.equals(v);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "{" + var.toString() + " -> " + sub.toString() + "}";
	}
}
