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
public abstract class Connective extends Expression {
	protected Term operator;
	protected ExpList operands;
	protected boolean amVolatile = false;
	protected static final int CONN_HASH_SEED = 2147001559;
		
	/**
	 * @param operator
	 */
	public Connective() {
		super();
		operator = null;
		operands = new ExpList();
	}

	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#occurs(cs227b.teamIago.resolver.Variable, cs227b.teamIago.resolver.Substitution)
	 */
	public boolean occurs(Variable var) {
		if (operator.occurs(var)) return true;
		else if (operands.occurs(var)) return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Formula#mgu(cs227b.teamIago.resolver.Formula, cs227b.teamIago.resolver.Substitution)
	 */
	public Substitution mgu(Expression target, Substitution sigma, Theory t) {
		if (target instanceof Variable)
			return ((Variable) target).mgu(this,sigma,t);
		else if (target instanceof Predicate)
		{
			Connective c2 = (Connective) target;
			Substitution temp = operator.mgu(c2.operator,sigma,t);
			if (temp != null) return operands.mgu(c2.operands,temp,t);
		}
		return null;
	}
		
	public void setOperands(ExpList elist)
	{
		if (elist == null) operands = new ExpList();
		else operands = elist;
	}
	
	public void addOperand(Expression e)
	{
		operands.add(e);
	}
	
	/**
	 * @return Returns the operands.
	 */
	public ExpList getOperands() {
		return operands;
	}
	/**
	 * @return Returns the operator.
	 */
	public Term getOperator() {
		return operator;
	}
	
	/*
	 * @param operator
	 * 	Attempts to set the operator.
	 *  Returns a success value.
	 */
	public boolean setOperator(Term operator){
		if (this.operator == null) 
		{
			this.operator = operator;
			return true;
		}
		else return false;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#firstOp()
	 */
	public Term firstOp() {
		return operator;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Connective)
		{
			Connective c = (Connective) obj;
			if (!c.operator.equals(operator)) return false;
			return c.operands.equals(operands);
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#secondOp()
	 */
	public Term secondOp() {
		Term op2;
		if (this.operands == null) return null;
		for (int i = 0; i < operands.size(); i++)
		{
			op2 = operands.get(i).firstOp();
			if (op2 != null) return op2;
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#getMaxVarNum()
	 */
	public long getMaxVarNum() {
		long headNum = operator.getMaxVarNum();
		long listNum = operands.getMaxVarNum();
		if (listNum > headNum) return listNum;
		else return headNum;
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#getVars()
	 */
	public ExpList getVars() {
		ExpList head = operator.getVars();
		head.addAll(operands.getVars());
		return head;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + operator.toString() + " " + operands.toString() + ")";
	}
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#isVolatile()
	 */
	public boolean isVolatile() {
		return amVolatile;
	}
	
	protected int hashProd(ExpList operands, int mul, int seed) {
		int val = seed;
		for (int i =0; i < operands.size(); ++i) 
		{
			val = val*val*mul;
			val = val + operands.get(i).hashCode();
			val = val % HASH_QUAD;
		}
		val = val * val * mul;
		val = val % HASH_QUAD;		
		return val;
	}
}
