/*
 * Created on Apr 19, 2005
 *
 *	Something like
 *  ArrayList<Expression> for Java < 1.5.0
 * 
 *  Implements some of the logic functions
 */
package cs227b.teamIago.resolver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExpList implements Serializable {
	protected ArrayList exps;
	protected boolean amVolatile;
	protected final int EXPLIST_HASH_SEED = 2147073697;
	
	public ExpList() {
		exps = new ArrayList();
		amVolatile = false;
	}
	
	public ExpList(ArrayList al)
	{
		this();
		if (al == null) return;
		for (int i = 0; i < al.size(); i++)
		{
			Object obj = al.get(i);
			if (!(obj instanceof Expression))
			{
				System.err.println("Error: cannot construct ExpList from ArrayList which contains classes not derived from Expression");
				System.exit(-1);
			}
			add((Expression)obj);
		}
	}
		
	public ExpList(Expression[] expArray){
		this();
		addAll(expArray);
	}
		
	public ExpList(Collection c) {
		this();
		addAll(c);
	}

	public ExpList(ExpList l){
		this();
		for (int i = 0; i < l.size(); i++) {
			exps.add(l.get(i));
		}
		amVolatile = l.amVolatile;
	}
	
	// For purposes of chaining, etc,
	// an expression list is treated as an extended
	// "and," as with its use for premises
	// in the "Implication" class.
	public ArrayList chain(Substitution sigma, Theory t, boolean cond)  throws InterruptedException
	{
		t.enterChain(this);
		ArrayList psis, deltas, answers;
		if (exps.size() == 0)
		{
			psis = new ArrayList();
			psis.add(sigma);
			t.exitChain(this,true,psis);
			return psis;
		}
		
		if (t.interrupted()) throw new InterruptedException();
		psis = get(0).chain(sigma,t, cond);
		if ((psis == null) || (psis.size() == 0)) 
		{
			t.exitChain(this,false,null);
			return null;
		}

		for (int i = 1; i < size(); i++)
		{
			deltas = new ArrayList();
			for (int j = 0; j < psis.size(); j++)
			{
				ArrayList gammas;
				Substitution onePsi;
				onePsi = (Substitution) psis.get(j);
				if (t.interrupted()) throw new InterruptedException();
				gammas = get(i).chain(onePsi,t, cond);
				if (gammas != null) deltas.addAll(gammas);				
			}
			if (deltas.size() == 0) {
				t.exitChain(this,false,null);
				return null;
			}
			psis = deltas;
		}
		if (psis.size() > 0) {
			t.exitChain(this,true,psis);
			return psis;
		}
		else {
			t.exitChain(this,false,null);
			return null;
		}
	}

	public Substitution chainOne(Substitution sigma, Theory t, boolean cond)  throws InterruptedException
	{
		t.enterChain(this);
		ArrayList psis, deltas, answers;
		if (exps.size() == 0)
		{
			t.exitChain(this,true,sigma);
			return sigma;
		}
		
		if (t.interrupted()) throw new InterruptedException();
		psis = get(0).chain(sigma,t, cond);
		if ((psis == null) || (psis.size() == 0)) 
		{
			t.exitChain(this,false,null);
			return null;
		}
		
		int last = exps.size() - 1;
		if (last == 0) {
			Substitution aleph = (Substitution) psis.get(0);
			if (t.interrupted()) throw new InterruptedException();
			t.exitChain(this,true,aleph);
			return aleph;
		}

		for (int i = 1; i < last; i++)
		{
			deltas = new ArrayList();
			for (int j = 0; j < psis.size(); j++)
			{
				ArrayList gammas;
				Substitution onePsi;
				onePsi = (Substitution) psis.get(j);
				if (t.interrupted()) throw new InterruptedException();
				gammas = get(i).chain(onePsi,t, cond);
				if (gammas != null) deltas.addAll(gammas);				
			}
			
			if (deltas.size() == 0) {
				t.exitChain(this,false,null);
				return null;
			}
			psis = deltas;
		}
		
		// It's not that much of a savings, but
		// for the last expression,
		// we can stop as soon as we find a single
		// working unifier if we're only looking for
		// one answer.
		
		Expression lastExp = get(last);
		for (int k = 0; k < psis.size(); k++)
		{
			Substitution onePsi, oneGamma;
			onePsi = (Substitution) psis.get(k);
			if (t.interrupted()) throw new InterruptedException();
			oneGamma = lastExp.chainOne(onePsi,t, cond);
			if (oneGamma != null) {
				t.exitChain(this,true,oneGamma);
				return oneGamma;			
			}
		}
		t.exitChain(this,false,null);
		return null;
	}
	public ArrayList eval(Substitution sigma, Theory t)  throws InterruptedException
	{
		t.enterChain(this);
		ArrayList psis, deltas, answers;
		if (exps.size() == 0)
		{
			psis = new ArrayList();
			psis.add(sigma);
			t.exitChain(this,true,psis);
			return psis;
		}
		
		if (t.interrupted()) throw new InterruptedException();
		psis = get(0).eval(sigma,t);

		for (int i = 0; i < exps.size(); i++)
		{
			deltas = new ArrayList();
			for (int j = 0; j < psis.size(); j++)
			{
				ArrayList gammas;
				Substitution onePsi;
				onePsi = (Substitution) psis.get(i);
				if (t.interrupted()) throw new InterruptedException();
				gammas = get(i).eval(onePsi,t);
				if (gammas != null) deltas.addAll(gammas);				
			}
			if (deltas.size() == 0){
				t.exitChain(this,false,null);
				return null;
			}
			psis = deltas;
		}
		if (psis.size() > 0)
		{
			t.exitChain(this,true,psis);
			return psis;
		}
		else {
			t.exitChain(this,false,null);
			return null;
		}
	}
	public Substitution evalOne(Substitution sigma, Theory t)  throws InterruptedException
	{
		t.enterChain(this);
		ArrayList psis, deltas, answers;
		if (exps.size() == 0)
		{
			t.exitChain(this,true,sigma);
			return sigma;
		}
		
		psis = get(0).eval(sigma,t);
		if ((psis == null) || (psis.size() == 0))
		{
			t.exitChain(this,false,null);
			return null;
		}
		
		int last = exps.size() - 1;
		if (last == 0) {
			Substitution aleph = (Substitution) psis.get(0);
			t.exitChain(this,true,aleph);
			return aleph;
		}

		for (int i = 0; i < last; i++)
		{
			deltas = new ArrayList();
			for (int j = 0; j < psis.size(); j++)
			{
				ArrayList gammas;
				Substitution onePsi;
				onePsi = (Substitution) psis.get(j);
				if (t.interrupted()) throw new InterruptedException();
				gammas = get(i).eval(onePsi,t);
				if (gammas != null) deltas.addAll(gammas);				
			}
			if (deltas.size() == 0) {
				t.exitChain(this,false,null);
				return null;
			}
			psis = deltas;
		}
		
		// It's not that much of a savings, but
		// for the last expression,
		// we can stop as soon as we find a single
		// working unifier if we're only looking for
		// one answer.
		
		Expression lastExp = get(last);
		for (int k = 0; k < psis.size(); k++)
		{
			Substitution onePsi, oneGamma;
			onePsi = (Substitution) psis.get(k);
			if (t.interrupted()) throw new InterruptedException();
			oneGamma = lastExp.evalOne(onePsi,t);
			if (oneGamma != null) {
				t.exitChain(this,true,oneGamma);
				return oneGamma;			
			}
		}
		t.exitChain(this,false,null);
		return null;
	}

	public ExpList removeDuplicates()
	{
		ExpList culled = new ExpList();
		for (int i = 0; i < size(); i++)
		{
			Expression e = get(i);
			if (!culled.contains(e)) culled.add(e);
		}
		return culled;
	}
	
	public boolean occurs(Variable var) {
		for (int i = 0; i < exps.size(); i++)
		{
			if (get(i).occurs(var)) return true;
		}
		return false;
	}
	
	public ExpList apply(Substitution sigma)
	{
		ExpList dup = new ExpList();
		for (int i = 0; i < exps.size(); i++)
			dup.exps.add(get(i).apply(sigma));
		return dup;
	}
	
	public Substitution mgu(ExpList target, Substitution sigma, Theory t)
	{
		Substitution temp = sigma;
		if (target.size() != size()) return null;
		for (int i = 0; (temp != null) && (i < size()); i++)
			temp = get(i).mgu(target.get(i),temp,t);
		return temp;
	}
	
	public long getMaxVarNum()
	{
		long curMax = Long.MIN_VALUE;
		for (int i = 0; i < size(); i++)
		{
			long temp = get(i).getMaxVarNum();
			if (temp > curMax) curMax = temp;
		}
		return curMax;
	}
	
	public ExpList getVars()
	{
		ExpList gather = new ExpList();
		for (int i = 0; i < size(); i++)
		{
			ExpList temp = get(i).getVars();
			if (temp != null) gather.addAll(temp);			
		}
		return gather;
	}
	
	public ArrayList toArrayList()
	{
		return exps;
	}
	
	/* 
	 */
	public void add(int i, Expression e) {
		amVolatile = amVolatile || e.buildVolatile(false);
		exps.add(i,e);
	}
	/* 
	 */
	public boolean add(Expression e) {
		amVolatile = amVolatile || e.buildVolatile(false);		
		return exps.add(e);
	}
	/* 
	 */
	
	public boolean add (ExpList l){
		boolean flag = true;
		for (int i = 0; i < l.size(); i++) {
			Expression e = l.get(i);
			amVolatile = amVolatile || e.buildVolatile(false);
			if (!exps.add(e))
				flag = false;
		}
		return flag;
	}
	
	public void addAll(Expression[] eArr) {
		if (eArr == null) return;
		for (int i = 0; i < eArr.length; ++i) {
			amVolatile = amVolatile || eArr[i].buildVolatile(false);
			add(eArr[i]);
		}
	}
	
	public void addAll(Collection c) {
		if (c == null) return;
		for (Iterator i = c.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof Expression) {
				Expression e = (Expression) o;
				amVolatile = amVolatile || e.buildVolatile(false);
				add(e);
			}
		}
	}

	public void addAll(ExpList eList) {
		if (eList == null) return;
		for (int i = 0; i < eList.size(); ++i) {
			amVolatile = amVolatile || eList.get(i).buildVolatile(false);
			add(eList.get(i));
		}
	}
	
	/* 
	 */
	public void clear() {
		exps.clear();
	}
	/* 
	 */
	public boolean contains(Expression e) {
		return exps.contains(e);
	}
	
	public boolean containsAll(ExpList eList) {
		if (eList == null) return true;
		for (int i = 0; i < eList.size(); ++i) {
			if (!contains(eList.get(i))) return false;
		}
		return true;
	}
	/* 
	 */
	public Expression get(int i) {
		return (Expression) exps.get(i);
	}
	/* 
	 */
	public int indexOf(Expression e) {
		return exps.indexOf(e);
	}
	/* 
	 */
	public boolean isEmpty() {
		return exps.isEmpty();
	}
	/* 
	 */
	public int lastIndexOf(Expression e) {
		return exps.lastIndexOf(e);
	}
	/* 
	 */
	public Expression remove(int i) {
		return (Expression) exps.remove(i);
	}
	/* 
	 */
	public boolean remove(Expression e) {
		return exps.remove(e);
	}
	/* 
	 */
	public Expression set(int i, Expression e) {
		return (Expression) exps.set(i,e);
	}
	/* 
	 */
	public int size() {
		return exps.size();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ExpList)
		{
			ExpList other = (ExpList) obj;
			if (other.size() != size()) return false;
			for (int i = 0; i < size(); i++)
				if (!(get(i).equals(other.get(i)))) return false;
			return true;
		}
		else if (obj instanceof Collection) {
			Collection c = (Collection) obj;
			if (c.size() != size()) return false;
			Iterator it = c.iterator();
			for (int i = 0; i < size(); i++)
			{
				if (!(get(i).equals(it.next()))) return false;
			}
			return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < size(); i++)
		{
			s.append(get(i).toString()).append(' ');
		}
		return s.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		long tempCode = 0;
		for (int i = 0; i < size(); i++)
			tempCode += get(i).hashCode();
		return (int)tempCode * EXPLIST_HASH_SEED % Expression.HASH_QUAD;
	}
	
	public boolean buildVolatile(boolean impliedVol) {
		amVolatile = amVolatile || impliedVol;
		for (int i = 0; i < exps.size(); ++i) {
		 amVolatile = 
		   ((Expression) exps.get(i)).buildVolatile(false) ||
		   amVolatile;
		}		
		return amVolatile;
	}
	
	public boolean isVolatile() {
		return amVolatile;
	}
	
	public void resetVolatile() {
		amVolatile = false;
	}
	
	public Substitution mapTo(Substitution sigma, ExpList eL) {
		Substitution psi = sigma;
		if (eL.size() != size()) return null;
		for (int i = 0; (psi != null) && (i < size()); i++)
			psi = get(i).mapTo(psi,eL.get(i));
		return psi;
	}
}
