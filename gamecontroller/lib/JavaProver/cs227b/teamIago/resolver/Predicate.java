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
public class Predicate extends Connective {
	protected static final Atom aTrue = new Atom("true");
	protected static final Atom aDoes = new Atom("does");
	protected static final int PRED_OPERATOR_HASH_SEED = 2147005213;
	protected static final int PRED_OPERANDS_HASH_SEED = 2147005547;
	protected static final int PRED_OPERANDS_HASH_MUL = 2147005921;
	
	/**
	 * @param operator
	 * @param operands
	 */
	public Predicate(String literal, ExpList operands) {
		super();
		this.operator = new Atom(literal);
		this.operands = operands;
		buildVolatile(false);
	}
	
	public Predicate(String literal, Expression[] opArray) {
		this(literal, new ExpList(opArray));
	}
	
	/**
	 * @param operator
	 * @param operands
	 */
	public Predicate(Term operator, ExpList operands) {
		super();
		this.operator = operator;
		this.operands = operands;
		buildVolatile(false);
	}
	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#apply(cs227b.teamIago.resolver.Substitution)
	 */
	public Expression apply(Substitution sigma) {
		return new Predicate(operator,operands.apply(sigma));
	}

	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#eval(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList eval(Substitution sigma, Theory t) throws InterruptedException {
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.eval(sigma,t);

		t.enterChain(this);
		// Check for memoized values
		// We're only storing chained,
		// so disproven is still fine, but
		// don't use proven

		if (t.checkDisproven(this)) {
			t.exitChain(this,false,null);
			return null;
		}
		ArrayList answers = new ArrayList();
		ExpList candidates = t.getCandidates(this);
		Substitution psi;
		for (int i = 0; i < candidates.size(); i++)
		{
			if (t.interrupted()) throw new InterruptedException();
			psi = mgu(candidates.get(i), sigma,t);
			if (psi != null) answers.add(psi);
		}
		
		boolean proven = (answers.size() > 0);
		t.exitChain(this,proven,answers);
		if (proven) return answers;
		else return null;
	}
	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Evaluable#evalOne(cs227b.teamIago.resolver.Expression, cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution evalOne(Substitution sigma, Theory t) throws InterruptedException {
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.evalOne(sigma,t);

		t.enterChain(this);
		// Check for memoized values
		// We're only storing chained,
		// so disproven is still fine, but
		// don't use proven

		if (t.checkDisproven(this)) {
			t.exitChain(this,false,null);
			return null;
		}
		
		ExpList candidates = t.getCandidates(this);
		Substitution psi;
		for (int i = 0; i < candidates.size(); i++)
		{
			if (t.interrupted()) throw new InterruptedException();
			psi = mgu(candidates.get(i), sigma,t);
			if (psi != null) 
			{
				t.exitChain(this,true,psi);
				return psi;
			}
		}
		t.exitChain(this,false,null);
		return null;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chain(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public ArrayList chain(Substitution sigma, Theory t, boolean cond)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chain(sigma,t, cond);

		ArrayList answers;
		t.enterChain(this);
		
		// Check for memoized values

 		if (!cond) {
 			if (t.checkDisproven(this)) {
 				t.exitChain(this,false,null);
 				return null;
 			}		
 			answers = t.checkProven(sigma,this);
 			if (answers != null) {
 				t.exitChain(this,true,answers);
 				return answers;
 			}
 		}

 		answers = new ArrayList();
		ExpList candidates = t.getCandidates(this);
		Substitution psi;
		for (int i = 0; i < candidates.size(); i++)
		{
			Expression e = candidates.get(i);
			if (e instanceof Implication)
			{
				Implication imp = (Implication) e;
				psi = mgu(imp.getConsequence(),sigma,t);
				if (psi != null)
				{
					ExpList prem = imp.getPremises();
					// prem = prem.apply(sigma);
					if (t.interrupted()) throw new InterruptedException();
					ArrayList premList = prem.chain(psi,t, cond);
					if (premList != null)
						answers.addAll(premList);
				}
			}			
			else
			{
				psi = mgu(candidates.get(i), sigma,t);
				if (psi != null) answers.add(psi);
			}
		}
		boolean proven = (answers.size() > 0);
		t.exitChain(this,proven,answers);
		if (proven) {
			// Memoize the values returned

			
			// Must decide if this is a rational
			// set of answers for this predicate.
			// Given that these integrate the incoming
			// substitution, this may not be the case!
			
			// Fixed this by storing only the "restriction"
			// of the solution set
			// (I think)
			
			if (!cond) t.setProven(this,answers);
			return answers;
		}
		else {
			// memoize the failure
			if (!cond) t.setDisproven(this);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#chainOne(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution chainOne(Substitution sigma, Theory t, boolean cond)  throws InterruptedException{
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.chainOne(sigma,t, cond);
		

		t.enterChain(this);
		// Check for memoized values

 		if (!cond) {
 			ArrayList allSubs = t.checkProven(sigma,this);
 			if ((allSubs != null) && (allSubs.size() > 0)) {
				Substitution psi = (Substitution) allSubs.get(0);
	 			t.exitChain(this,true,psi);
 				return psi;
 			}
 			if (t.checkDisproven(this)) {
 				t.exitChain(this,false,null);
 				return null;
 			}
 		}

 		ExpList candidates = t.getCandidates(this);
		Substitution psi;
		for (int i = 0; i < candidates.size(); i++)
		{
			if (candidates.get(i) instanceof Implication)
			{
				Substitution gamma;
				psi = null;
				Implication imp = (Implication) candidates.get(i);
				gamma = mgu(imp.getConsequence(),sigma,t);
				if (gamma != null)
				{
					ExpList prem = imp.getPremises();
					// prem = prem.apply(sigma);
					if (t.interrupted()) throw new InterruptedException();
					psi = prem.chainOne(gamma,t, cond);
				}
			}
			else psi = mgu(candidates.get(i), sigma,t);
			if (psi != null) 
			{
				t.exitChain(this,true,psi);
				// Don't set proven for a single unifier--
				// we need a full solution-set
				return psi;
			}
		}
		t.exitChain(this,false,null);
		// Memoize failure
		if (!cond) t.setDisproven(this);
		return null;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#buildVolatile()
	 */
	public boolean buildVolatile(boolean impliedVol) {
		amVolatile = amVolatile || impliedVol;
		boolean connectVol = amVolatile;
		
		if (operator.equals(aTrue)) connectVol = true;
		if (operator.equals(aDoes)) connectVol = true;
		if (operator instanceof Variable) connectVol = true;
		// Note that the words "true" and "does" aren't
		// *inherently* volatile--only as the operators in
		// predicates.
		connectVol = operator.buildVolatile(connectVol) || connectVol;
		
		amVolatile = amVolatile || connectVol;
		
		operands.buildVolatile(false);
		return amVolatile;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return ((operator.hashCode()*PRED_OPERATOR_HASH_SEED + 
				hashProd(operands,PRED_OPERANDS_HASH_MUL,PRED_OPERANDS_HASH_SEED)))
				*PRED_OPERATOR_HASH_SEED % HASH_QUAD;
	}

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#mapTo(cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Expression)
	 */
	public Substitution mapTo(Substitution sigma, Expression e) {
		Substitution psi = null;
		if (e instanceof Predicate) {
			Predicate other = (Predicate) e;
			psi = operator.mapTo(sigma,other.operator);
			if (psi != null) psi = operands.mapTo(psi,other.operands);
		}
		return psi;
	}
}
