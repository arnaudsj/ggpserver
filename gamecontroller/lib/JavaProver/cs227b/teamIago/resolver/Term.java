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
public abstract class Term extends Expression {

	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Expression#firstOp()
	 */
	public Term firstOp() {
		return this;
	}
	
	public Term secondOp() {
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

		// Check for memoized values
		// We're only storing chained,
		// so disproven is still fine, but
		// don't use proven

		//TODO Don't enable these until we fix
		//the substitution-matching
 		if (t.checkDisproven(this)) {
			t.exitChain(this,false,null);
			return null;
		}

 		ArrayList answers = new ArrayList();
		ExpList candidates = t.getCandidates(this);
		Substitution psi;
		for (int i = 0; i < candidates.size(); i++)
		{
			psi = mgu(candidates.get(i), sigma,t);
			if (psi != null) answers.add(psi);
		}
		boolean proven = (answers.size() != 0);
		t.exitChain(this,proven,answers);
		if (proven) return answers;
		else return null;
	}
	
	/* (non-Javadoc)
	 * @see cs227b.teamIago.resolver.Evaluable#evalOne(cs227b.teamIago.resolver.Expression, cs227b.teamIago.resolver.Substitution, cs227b.teamIago.resolver.Theory)
	 */
	public Substitution evalOne(Substitution sigma, Theory t)  throws InterruptedException {
		Expression s = ground(t,sigma);
		if (t.interrupted()) throw new InterruptedException();
		if (!s.equals(this)) return s.evalOne(sigma,t);

		t.enterChain(this);
		
		// Check for memoized values
		// We're only storing chained,
		// so disproven is still fine, but
		// don't use proven

		//TODO Don't enable these until we fix
		//the substitution-matching
 		if (t.checkDisproven(this)) {
			t.exitChain(this,false,null);
			return null;
		}

		ExpList candidates = t.getCandidates(this);
		Substitution psi;
		for (int i = 0; i < candidates.size(); i++)
		{
			psi = mgu(candidates.get(i), sigma,t);
			if (psi != null) {
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
		t.enterChain(this);

		ArrayList answers;

		// Check for memoized values

 		if (!cond) {
 			//TODO Don't enable these until we fix
 			//the substitution-matching
 			
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
			if (candidates.get(i) instanceof Implication)
			{
				Substitution gamma;
				ArrayList psis;
				Implication imp = (Implication) candidates.get(i);
				gamma = mgu(imp.getConsequence(),sigma,t);
				if (gamma != null) 
				{
					if (t.interrupted()) throw new InterruptedException();
					psis = imp.getPremises().chain(gamma,t, cond);
					if ((psis != null) && (psis.size() != 0))
						answers.addAll(psis);
				}
			}
			else {
				psi = mgu(candidates.get(i), sigma,t);
				if (psi != null) answers.add(psi);
			}
		}
		boolean proven = answers.size() != 0;
		t.exitChain(this,proven,answers);
		if (proven) {
			// Memoize the values returned

			
			// Must decide if this is a rational
			// set of answers for this predicate.
			// Given that these integrate the incoming
			// substitution, this may not be the case!
			
			// Fixed this by storing the "restriction"
			// of a substitution
			// (I think)

	 		if (!cond) t.setProven(this,answers);
			return answers;
		}
		else {
			// Memoize failure
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
 			//TODO Don't enable these until we fix
 			//the substitution-matching
 			
 			ArrayList allSubs = t.checkProven(sigma,this);
 			if ((allSubs != null) && (allSubs.size() > 0))
 			{
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
				if (t.interrupted()) throw new InterruptedException();
				if (gamma != null) psi = imp.getPremises().chainOne(gamma,t, cond);
			}
			else psi = mgu(candidates.get(i), sigma,t);
			if (psi != null) 
			{
				t.exitChain(this,true,psi);
				return psi;
			}
		}
		t.exitChain(this,false,null);
		// Memoize failure
		if (!cond) t.setDisproven(this);
		return null;
	}
}
