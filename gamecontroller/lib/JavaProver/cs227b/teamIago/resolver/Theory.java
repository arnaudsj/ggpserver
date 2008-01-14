/*
 * Created on Apr 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.resolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cs227b.teamIago.util.GameState;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Theory {
	
	protected HashMap univ;
	protected HashMap trans;
	protected HashMap rules;
	protected ExpList moves;
	protected ExpList premises;
	protected long topVar;
	protected boolean debug;
	protected boolean useOpt;
	protected int proofLevel;
	protected boolean abort = false;
	
	/* Memoization tools (dynamic programming optimization) */
	protected HashMap provenStat;
	protected HashSet disprovenStat;
	
	protected static final boolean memoTrans = true;
	protected static final boolean storeTrans = false;
	protected HashMap provenTrans;
	protected HashSet disprovenTrans;
	
	static final Atom aTrue = new Atom("TRUE");
	static final Atom aImp  = new Atom("<=");
	static final Atom aVar  = new Atom("*");
	static final Atom aDoes = new Atom("DOES");
	static final Atom aDummy = new Atom("found");
	
	
	public Theory(boolean debug, boolean useOpt)
	{
		univ = new HashMap();
		rules = new HashMap();
		provenStat = new HashMap();
		disprovenStat = new HashSet();
		clearState();
		topVar = Long.MIN_VALUE;
		this.debug = debug; 
		this.useOpt = useOpt;
		proofLevel = 0;
		premises = null;
	}
	
	public void buildVolatile() {
		// Work out which predicates are static
		// and which depend on transients
		// Recursive, but doesn't require that much
		// maintenance--basically a startup cost
		ArrayList ruleLists = new ArrayList(rules.values());
		ExpList ruleList = new ExpList();
		for (int i = 0; i < ruleLists.size(); ++i) {
			ruleList.addAll((ExpList)ruleLists.get(i));
		}
		
		boolean stillBuilding = true;
		while (stillBuilding) {
			ruleList.resetVolatile();
			stillBuilding = ruleList.buildVolatile(false);
			if (!stillBuilding) break;
			for (int i = 0; i < ruleList.size();) {
				if (ruleList.get(i).isVolatile()) {
					ruleList.remove(i);
				} else ++i;
			}
		}

		ArrayList statLists = new ArrayList(univ.values());
		ExpList statList = new ExpList();
		for (int i = 0; i < statLists.size(); ++i) {
			statList.addAll((ExpList)statLists.get(i));
		}
		
		statList.buildVolatile(false);
	}
	
	public void clearState() {
		if (trans == null || trans.size() != 0) 
				trans = new HashMap();
		clearMoves();
		//if (useOpt && memoTrans) clearTransProofs();
	}

	protected void clearProofs() {
		if (!useOpt) return;
		if (provenStat == null || provenStat.size() != 0)
			provenStat = new HashMap();
		if (disprovenStat == null || disprovenStat.size() != 0)
			disprovenStat = new HashSet();
		if (memoTrans) clearTransProofs();
	}

	protected void clearTransProofs() {
		if (!useOpt || !memoTrans) return;
		if (provenTrans == null || provenTrans.size() != 0)
			provenTrans = new HashMap();
		if (disprovenTrans == null || disprovenTrans.size() != 0)
			disprovenTrans = new HashSet();
		
	}
		
	public void clearMoves() {
		if (memoTrans) clearTransProofs();
		moves = new ExpList();
	}
	
	public GameState getState() {
		if (memoTrans && storeTrans) 
			return new GameState(trans,provenTrans,disprovenTrans);
		else return new GameState(trans);
	}
	
	public void setState(GameState state) {
		if (state == null) clearState();
		else { 
			clearMoves();
			trans = state.getMap();
			
			if (!useOpt || !memoTrans || !storeTrans) return;

			HashMap sp = state.getProven();
			HashSet sdp = state.getDisproven();
			if (sp != null) provenTrans = sp;
			else provenTrans = new HashMap();
			if (sdp != null) disprovenTrans = sdp;
			else disprovenTrans = new HashSet();
		}
	}
	
	public void setState(ExpList state) {
		clearState();
		for (int i = 0; i < state.size(); i++) {
			addToTrans(state.get(i));
		}
	}
	
	public Variable generateVar()
	{
		return new Variable(++topVar);
	}
	
	public Substitution uniquifier(Expression e)
	{
		long minVar = e.getMaxVarNum();
		if (topVar < minVar) topVar = minVar;
		ExpList vars = e.getVars();
		vars = vars.removeDuplicates();
		Substitution uni = new Substitution();
		if (vars == null) return uni;
		for (int i = 0; i < vars.size(); i++)
			uni.addAssoc((Variable)vars.get(i),generateVar());
		return uni;
	}

	public boolean truep(Expression toMatch) throws InterruptedException
	{
		/*
		Substitution sub = uniquifier(toMatch);
		Expression uniqueMatch = toMatch.apply(sub);
		if (truex(aDummy, uniqueMatch) == null) return false;
		*/
		if (truex(aDummy, toMatch) == null) return false;
		return true;	
	}
	
	public Expression truex(Expression fill, Expression toMatch) throws InterruptedException
	{
		/*
		Substitution sub = uniquifier(toMatch);
		Expression uniqueMatch = toMatch.apply(sub);
		Substitution sigma = uniqueMatch.evalOne(new Substitution(), this);
		*/
		Substitution xi;
		if (useOpt) xi = uniquifier(toMatch);
		else xi = new Substitution();
		if (useOpt) toMatch.buildVolatile(false);
		Substitution sigma = toMatch.evalOne(xi, this);
		return retExp(fill,sigma);
	}
	
	public ExpList trues(Expression fill, Expression toMatch) throws InterruptedException
	{
		/*
		Substitution sub = uniquifier(toMatch);
		Expression uniqueMatch = toMatch.apply(sub);
		ArrayList sigmas = uniqueMatch.eval(new Substitution(), this);
		*/
		Substitution xi;
		if (useOpt) xi = uniquifier(toMatch);
		else xi = new Substitution();
		if (useOpt) toMatch.buildVolatile(false);
		ArrayList sigmas = toMatch.eval(xi, this);
		
		return retExpList(fill,sigmas);
	}
	
	public boolean findp(Expression toMatch) throws InterruptedException
	{
		/*
		Substitution sub = uniquifier(toMatch);
		Expression uniqueMatch = toMatch.apply(sub);
		if (findx(aDummy, uniqueMatch) == null) return false;
		*/
		if (findx(aDummy, toMatch) == null) return false;

		return true;	
	}
	
	public Expression findx(Expression fill, Expression toMatch) throws InterruptedException
	{
		/*
		Substitution sub = uniquifier(toMatch);
		Expression uniqueMatch = toMatch.apply(sub);
		Substitution sigma = uniqueMatch.chainOne(new Substitution(), this);
		*/
		Substitution xi;
		if (useOpt) xi = uniquifier(toMatch);
		else xi = new Substitution();
		if (useOpt) toMatch.buildVolatile(false);
		Substitution sigma = toMatch.chainOne(xi, this, false);
		return retExp(fill,sigma);
	}
	
	public ExpList findx(ExpList fill, Expression toMatch) throws InterruptedException
	{
		/*
		Substitution sub = uniquifier(toMatch);
		Expression uniqueMatch = toMatch.apply(sub);
		Substitution sigma = uniqueMatch.chainOne(new Substitution(), this);
		*/
		Substitution xi;
		if (useOpt) xi = uniquifier(toMatch);
		else xi = new Substitution();
		if (useOpt) toMatch.buildVolatile(false);
		Substitution sigma = toMatch.chainOne(xi, this, false);
		return retExpList(fill,sigma);
	}

	public ExpList finds(Expression fill, Expression toMatch)  throws InterruptedException
	{
		/*
		Substitution sub = uniquifier(toMatch);
		Expression uniqueMatch = toMatch.apply(sub);
		ArrayList sigmas = uniqueMatch.chain(new Substitution(), this);
		*/
		Substitution xi;
		if (useOpt) xi = uniquifier(toMatch);
		else xi = new Substitution();
		if (useOpt) toMatch.buildVolatile(false);
		ArrayList sigmas = toMatch.chain(xi, this, false);
		return retExpList(fill,sigmas);
	}

	public ArrayList finds(ExpList fill, Expression toMatch)  throws InterruptedException
	{
		/*
		Substitution sub = uniquifier(toMatch);
		Expression uniqueMatch = toMatch.apply(sub);
		ArrayList sigmas = uniqueMatch.chain(new Substitution(), this);
		*/
		Substitution xi;
		if (useOpt) xi = uniquifier(toMatch);
		else xi = new Substitution();
		if (useOpt) toMatch.buildVolatile(false);
		ArrayList sigmas = toMatch.chain(xi, this, false);
		return retExpLists(fill,sigmas);
	}

	public boolean findpConditional(Expression toProve,ExpList given)  throws InterruptedException
	{
		boolean value;
		premises = given;
		Substitution sigma = toProve.chainOne(new Substitution(), this, true);
		value = (sigma != null);
		premises = null;
		return value;
	}
	
	public ExpList findxConditional(Expression toProve,ExpList given) throws InterruptedException
	{
		ExpList value;
		premises = given;
		Substitution sigma = toProve.chainOne(new Substitution(), this, true);
		value = retExpList(given,sigma);
		premises = null;
		return value;
	}

	public ArrayList findsConditional(Expression toProve, ExpList given)  throws InterruptedException
	{
		ArrayList value;
		premises = given;
		ArrayList sigmas = toProve.chain(new Substitution(),this, true);
		value = retExpLists(given,sigmas);
		premises = null;
		return value;
	}

	
	public void add(ExpList exps) {
		for (int i = 0; i < exps.size(); i++) {
			add(exps.get(i));
		}
	}
	
	public boolean add(Expression exp)
	{
		if (useOpt) exp.buildVolatile(false);
		long maxVar = exp.getMaxVarNum();
		if (maxVar > topVar) topVar = maxVar + 1;
		Term first = exp.firstOp();
		boolean toTrue = first.equals(aTrue);
		boolean toImp = first.equals(aImp);
		boolean toDoes = first.equals(aDoes);
		if (toTrue) return addToTrans(exp);
		else if (toImp) return addToRules(exp);
		else if (toDoes) return addToMoves(exp);
		else return addToUniv(exp);
	}

	public ExpList getCandidates(Expression exp)
	{
		ArrayList rets = new ArrayList();
		Term temp;
		Atom first, second;
		temp = exp.firstOp();
		if (!(temp instanceof Atom))
		{
			// If the operator is not an atom, but a variable,
			// we can match anything in either
			// the unqualified universals or the rules.
			// (and the moves)
			
			first = aVar;
			temp = exp.secondOp();
			if (!(temp instanceof Atom))
				second = aVar;
			else second = (Atom)temp;
			
			Collection c = univ.values();
			for (Iterator i = c.iterator(); i.hasNext();)
			{
				Object obj = i.next();
				ExpList tempList = (ExpList) obj;
				rets.addAll(tempList.toArrayList());
			}

			c = rules.values();
			for (Iterator i = c.iterator(); i.hasNext();)
			{
				Object obj = i.next();
				ExpList tempList = (ExpList) obj;
				rets.addAll(tempList.toArrayList());
			}
			
			// moves are just a list
			rets.addAll(moves.toArrayList());
			
			// Since the first op is a wildcard, it will also match "true."
			// This means we also have to check for things in the
			// transient state whose second op matches.
			
			// If the second op is a star, we are in big trouble.
			// Then it's just every danged thing in the theory.
			if (second.equals(aVar))
			{
				
				c = trans.values();
				for (Iterator i = c.iterator(); i.hasNext();)
				{
					Object obj = i.next();
					ExpList tempList = (ExpList) obj;
					rets.addAll(tempList.toArrayList());
				}
			}
			else
			{
				// The second op was a literal, so we only get the
				// "true"s that actually match, or that have a
				// variable op in the axioms
				ExpList matchTrans = (ExpList) trans.get(second);
				ExpList starTrans = (ExpList) trans.get(aVar);
				
				if (matchTrans != null) rets.addAll(matchTrans.toArrayList());
				if (starTrans != null) rets.addAll(starTrans.toArrayList());
			}
		}
		else 
		{
			// First operator was not a variable,
			// but a literal  (whew!)
			// so we can match it
			
			first = (Atom)temp;
			boolean isTrans = first.equals(aTrue);
			boolean isMove =  first.equals(aDoes);
			temp = exp.secondOp();
			if (temp instanceof Atom) second = (Atom)temp;
			else second = aVar;
			
			
			// Everything matches the (first op of the) "*" list
			ExpList starUniv = (ExpList)univ.get(aVar);
			if (starUniv != null) rets.addAll(starUniv.toArrayList());
			ExpList starRules = (ExpList) rules.get(aVar);
			if (starRules != null) rets.addAll(starRules.toArrayList());
			
			if (!isTrans && !isMove)
			{
				ExpList matchUniv = (ExpList)univ.get(first);
				if (matchUniv != null) rets.addAll(matchUniv.toArrayList());
				ExpList matchRules = (ExpList) rules.get(first);
				if (matchRules != null) rets.addAll(matchRules.toArrayList());
			}
			else if (isMove)
			{
				rets.addAll(moves.toArrayList());
			} 
			else
			{
				// either true(expression) or true(*) 
				if (!second.equals(aVar)) 
				{	
					ExpList matchTrans = (ExpList) trans.get(second);
					ExpList starTrans = (ExpList) trans.get(aVar);
					if (matchTrans != null) rets.addAll(matchTrans.toArrayList());
					if (starTrans != null) rets.addAll(starTrans.toArrayList());
				}
				else 
				{
					//	Second operator is a variable--everything's a candidate
					//	(true *), so add the whole transient set
					Collection c = trans.values();
					for (Iterator i = c.iterator(); i.hasNext();)
					{
						Object obj = i.next();
						ExpList tempList = (ExpList) obj;
						rets.addAll(tempList.toArrayList());
					}
				}
			}	
		}

		ExpList preRetExp = new ExpList(rets);
		ExpList retExp = new ExpList();
		for (int j = 0; j < preRetExp.size(); j++)
		{
			Expression e = preRetExp.get(j);
			Substitution psi = uniquifier(e);
			Expression f = e.apply(psi);
			retExp.add(f);
		}
		if (premises != null){
			// Add premises after uniquification, so that variables in them
			// don't get relabelled and are thus only bound once
			// Also, add them at the end, so that we'll bind with other
			// clauses if it's provable in another way
			for (int j = 0; j < premises.size(); j++)
			{
				retExp.add((Expression) premises.get(j));
			}
		}
		return retExp;
		
		// replacement code --had to undo this, it didn't work right
		// return new ExpList(rets);
	}
	
	protected boolean addToMap(HashMap map, Atom key, Expression exp)
	{
		ExpList eL = (ExpList) map.get(key);
		if (eL == null) 
			{
				eL = new ExpList();
				eL.add(exp);
				map.put(key,eL);
				return true;
			}
		else if (!eL.contains(exp)) 
			{
				eL.add(exp);
				return true;
			}
		else return false;
		
	}

	protected boolean addToUniv(Expression exp)
	{
		if (useOpt) clearProofs();
		Term first = exp.firstOp();
		if (!(first instanceof Atom))
			return addToMap(univ,aVar,exp);
		else
			return addToMap(univ,(Atom) first, exp);
	}
	
	protected boolean addToTrans(Expression exp)
	{
		if (useOpt && memoTrans) clearTransProofs();
		Term secondOp = exp.secondOp();
		if (!(secondOp instanceof Atom))
			return addToMap(trans,aVar,exp);
		else return addToMap(trans, (Atom) secondOp, exp);
	}
	protected boolean addToRules(Expression exp)
	{
		if (useOpt) clearProofs();
		Implication imp = (Implication)exp;
		Term second = imp.secondOp();
		if (!( second instanceof Atom)) 
			return addToMap(rules,aVar,exp);
		else
			return addToMap(rules,(Atom)second,exp);
	}
	
	protected boolean addToMoves(Expression exp)
	{
		if (useOpt && memoTrans) clearTransProofs();
		return moves.add(exp);
	}
		
	/**
	 * @return Returns the debug.
	 */
	public boolean isDebug() {
		return debug;
	}
	/**
	 * @param debug The debug to set.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	/**
	 * @return Returns the topVar.
	 */
	public long getTopVar() {
		return topVar;
	}
	/**
	 * @param topVar The topVar to set.
	 */
	public void setTopVar(long topVar) {
		this.topVar = topVar;
	}
	/**
	 * @return Returns the proofLevel.
	 */
	public int getProofLevel() {
		return proofLevel;
	}
	
	public void incProofLevel() {
		++proofLevel;
	}
	public void decProofLevel() {
		--proofLevel;
	}
	
	protected void indent(int amount) {
		for (int i=0;i<amount;i++) System.err.print(" ");
	}
	
	public void enterChain(Object e) {
		incProofLevel();
		if (debug) {
			indent(proofLevel);
			System.err.println("Prove: " + e);
		}
	}
	
	public void exitChain(Object e, boolean result, Object res) {
		if (debug) {
			indent(proofLevel);
			System.err.print("Exit: ");
			if (result) System.err.println("true: " + res);
			else System.err.println("false");
		}
		decProofLevel();
	}
	
	public ArrayList checkProven(Substitution sigma, Expression e) {
		if (!useOpt) return null;
		ResolveMemoEntry mem = new ResolveMemoEntry(e);
		ResolveMemoEntry rMem = (ResolveMemoEntry) provenStat.get(mem);
		if (memoTrans && rMem == null) rMem = (ResolveMemoEntry) provenTrans.get(mem);
		if (rMem == null) return null;
		ArrayList rSubs = rMem.getSubs(e);
		Substitution sigmaC = sigma.factor();
		ArrayList retSubs = new ArrayList();
		for (int i = 0; i < rSubs.size(); ++i) {
			Substitution oneSub = (Substitution) rSubs.get(i);
			Substitution psi = sigmaC.apply(oneSub);
			retSubs.add(psi);
		}
//		if (rSubs != null) System.out.println("Using pre-proven result.");
		return retSubs;
	}
	
	public boolean checkDisproven(Expression e) {
		if (!useOpt) return false;
		// TODO change this to work not just if e 
		// in the list, but if e *unifies* with something
		// in the list
		
		// if it makes it in to disprovenStat, it's *always* stat
		// so it's disproven forever (no trans can make it true)
		ResolveMemoEntry mem = new ResolveMemoEntry(e);		
		if (disprovenStat.contains(mem)) return true;
		if (memoTrans && disprovenTrans.contains(mem)) return true;
		return false;
	}
		
	public void setProven(Expression e, ArrayList subs) {
		if (!useOpt) return;
		// Cut this to only the portion
		// necessary to ground e and match it with its
		// proof
		ArrayList rSubs = new ArrayList();
		for (int i = 0; i < subs.size(); ++i) {
			Substitution psi = (Substitution) subs.get(i);
			Substitution sigma = psi.restrict(e);
			if (!rSubs.contains(sigma)) rSubs.add(sigma);
		}
		ResolveMemoEntry mem = new ResolveMemoEntry(e);
		mem.subs = rSubs;
		// Then store it into the system
		if (e.isVolatile()) {
			if (memoTrans) provenTrans.put(mem,mem);
		}
		else provenStat.put(mem,mem);
	}

	public void setDisproven(Expression e) {
		if (!useOpt) return;
		ResolveMemoEntry mem = new ResolveMemoEntry(e);
		if (e.isVolatile()) {
			if (memoTrans) disprovenTrans.add(mem);
		}
		else disprovenStat.add(mem);
	}
	
	protected ExpList retExpList(Expression toFill, ArrayList sigmas) {
		if (sigmas == null) return null;
		ExpList results = new ExpList();
		for (int i = 0; i < sigmas.size(); i++)
		{
			Substitution psi = (Substitution)sigmas.get(i);
			if ((psi == null) || psi.empty()) continue; 
			Expression e = toFill;
			Expression retExp = e.apply(psi);
			while (!retExp.equals(e)) {
				e = retExp;
				retExp = e.apply(psi);
			}
			if (!results.contains(retExp)) results.add(retExp);			
		}
		if (results.size() == 0) return null;
		else return results;
	}
	
	protected ArrayList retExpLists(ExpList toFill, ArrayList sigmas){
		if (sigmas == null) return null;
		ArrayList retList = new ArrayList();
		for (int i = 0; i < sigmas.size(); i++)
		{
			Substitution psi = (Substitution)sigmas.get(i);
			if ((psi == null) || psi.empty()) continue; 
			ExpList e = toFill;
			ExpList retExp = e.apply(psi);
			while (!retExp.equals(e)) {
				e = retExp;
				retExp = e.apply(psi);
			}
			if (!retList.contains(retExp)) retList.add(retExp);			
		}
		if (retList.size() == 0) return null;
		else return retList;
	}
	
	protected Expression retExp(Expression toFill, Substitution sigma) {
		if (sigma == null) return null;
		Expression retExp = toFill.apply(sigma);
		while (!retExp.equals(toFill)) {
			toFill = retExp;
			retExp = toFill.apply(sigma);
		}
		return retExp;
	}	

	protected ExpList retExpList(ExpList toFill, Substitution sigma) {
		if (sigma == null) return null;
		ExpList retExp = toFill.apply(sigma);
		while (!retExp.equals(toFill)) {
			toFill = retExp;
			retExp = toFill.apply(sigma);
		}
		return retExp;
	}	
	
	public void interrupt() {
		abort = true;
	}

	public boolean interrupted() {
		return abort;
	}
}
