package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.ArrayList;
import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.scope.RuleScope;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

/**
 * 
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 *
 */
public abstract class Term extends Expression {

	/**
	 * @return Returning 'this' whatever it is: atom or variable.
	 */
	@Override
	public Term firstOperand() {
		return this;
	}	

	/**
	 * @return No second operand for terms. Returning null.
	 */
	@Override
	public Term secondOperand() {
		return null;
	}

    /**
     * <code>Term</code> class is actually second one that that meke use
     * of GDL rules scopes by getting similar expressions from them.
     * Trick about <code>Term</code> is that there might be implication 
     * consequences (take <i>&quot;terminal&quot;</i> for instance)
     * and single predicates (both static and dynamic) recieved 
     * after matching with existing data. So major trick is a kind of branching.
     * Another point is that <code>chain</code> method is looking for all 
     * possible resolutions. Here is a step by step behavior:
     * <ol>
     *     <li> ask current GDL scope for similar predicates.</li>
     *     <li> iterate through each of the predicates:
     *     <ol>
     *         <li> check type of expression being currently iterated
     *         <ol>
     *             <li> if an implication happens to be retrieved:
     *             <ol>
     *                 <li> ask it's consiquence for a MGU</li> 
     *                 <li> proceed to <code>chain</code> of the premises (those are 
     *                      stored in an <code>ExpressionList</code></li>  
     *                 <li> store the result</li>
     *             </ol></li>
     *             <li> otherwise just as for an MGU with iterated expression</li>       
     *         </ol></li>
     *         <li> store any non-empty results</li>
     *     </ol>
     *     </li>
     *     <li> if no results were accumulated, assume it as resolution failure
     *          and return empty list of substitutions</li>
     * </ol>
     * 
     * @see de.tu_dresden.inf.ggp06_2.resolver.Predicate#chainBody(Substitution, RuleScope, TimerFlag) Predicate.chainBody
     * @see de.tu_dresden.inf.ggp06_2.resolver.ExpressionList#chain(Substitution, RuleScope, TimerFlag) ExpressionList.chain
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
	@Override
	protected List<Substitution> chainBody( Substitution sigma, 
                                            RuleScope    scope, 
                                            TimerFlag    flag )
	throws InterruptedException {

        // First: Check for memoized values
		if ( scope.isDisproven(this) )
		    return new ArrayList<Substitution>();
		else if ( scope.isProven(this) )
            return scope.getProven(sigma, this);

        // Second: calculate substitutions
        List<Substitution> answers = chainSimilarExpressions( sigma, scope, flag );
		
		// Third: store result for future.
		if ( !answers.isEmpty() )
            scope.setProven( this, answers );	 					
		else
			scope.setDisproven(this);

        return answers;
	}

    /**
     * This private method acts only as an entry point for <code>chain</code>
     * method. It hides all the stuff specific to getting similar expressions
     * from the rules scope and iterating over them. Implemented only for the
     * sake of readability. 
     *
     * @see de.tu_dresden.inf.ggp06_2.resolver.Term#chainBody(Substitution, RuleScope, TimerFlag) chainBody
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns list of substitutions that were produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    private List<Substitution> chainSimilarExpressions( Substitution sigma, 
                                                        RuleScope    scope, 
                                                        TimerFlag    flag ) 
    throws InterruptedException {

        List<Substitution> answers    = new ArrayList<Substitution>();
		ExpressionList     candidates = scope.getSimilarExpressions(this);        

        for (Expression anExpression : candidates) {
			if (anExpression instanceof Implication) {
				chainImplication( 
                        sigma, scope, flag, 
                        answers, (Implication) anExpression );
				
			} else {
                Substitution psi = mgu(anExpression, sigma);
				if (null != psi) 
                    answers.add(psi);
			}
		}
        return answers;
    }

    /**
     * This private method acts only as an entry point for 
     * <code>chainSimilarExpressions</code> method
     * It takes care of handling any implications that appear during iteration.
     * Implemented for the sake of readability only.
     * 
     * @see de.tu_dresden.inf.ggp06_2.resolver.Term#chainSimilarExpressions(Substitution, RuleScope, TimerFlag) chainSimilarExpressions 
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @param accumulator List of substitutions that actually accumulates any 
     *                    successful results
     * @param imp Current implication to be resolved.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */    
    private void chainImplication(
            Substitution sigma, RuleScope scope, TimerFlag flag, 
            List<Substitution> accumulator, Implication imp) 
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;
        Substitution gamma = mgu(imp.getConsequence(), sigma);
        if (gamma != null) {
            List<Substitution> psis = imp.getPremises().chain(gamma, scope, flag);
        	if (psis.size() != 0)
        		accumulator.addAll(psis);
        }
    }

    /**
     * Trick about <code>Term</code> is that there might be implication consequences
     * and single predicates (both static and dynamic) recieved after matching
     * with existing data. So major trick is a kind of branching. Here is a step
     * by step behavior:
     * <ol>
     *     <li> ask current GDL scope for similar predicates.</li>
     *     <li> iterate through each of the predicates:
     *     <ol>
     *         <li>if an implication happens to be retrieved, ask it's 
     *             consiquence for a MGU, and proceed to <code>chainOne</code>
     *             of the premises (those are stored in an 
     *             <code>ExpressionList</code></li>
     *         <li>otherwise just ask for an MGU with iterated expression</li>
     *         <li>if any of iterations produces non-empty result - return it</li>
     *     </ol>
     *     </li>
     *     <li> if no results were returned, assume it as resolution failure
     *          and return null</li>
     * </ol>
     * 
     * @see de.tu_dresden.inf.ggp06_2.resolver.Term#mgu(Expression, Substitution) mgu
     * @see de.tu_dresden.inf.ggp06_2.resolver.ExpressionList#chainOne(Substitution, RuleScope, TimerFlag) ExpressionList.chainOne
     * @param sigma Current state of resolution
     * @param scope Scope of GDL rules used for resolution
     * @param flag Timer flag, which is checked before every step of resolution.
     * @return Returns one (first) substitution that was produced during resolution
     *         procedure.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */    
	@Override
	protected Substitution chainOneBody( Substitution sigma, 
                                         RuleScope    scope, 
                                         TimerFlag    flag ) 
    throws InterruptedException {
        
		// Check for memoized values
	    if ( scope.isProven(this) )
            return scope.getProven(sigma, this).get(0);

        else if ( scope.isDisproven(this) )
	        return null;

	    ExpressionList candidates = scope.getSimilarExpressions(this);
		Substitution   psi;
		for (Expression anExpression : candidates) {
			if (anExpression instanceof Implication) {
                psi = chainOneImplication( 
                        sigma, scope, 
                        flag, (Implication) anExpression );
			} else {
				psi = mgu(anExpression, sigma);
			}
			
			if (psi != null) {
				return psi;
			}
		}
        
		// Memoize failure
		scope.setDisproven(this);
		return null;
	}

    /**
     * This method acts only as an entry point for <code>chainOne</code> method.
     * It actually takes care of handling implication, if an implication
     * happens among expression returned by scope.
     * 
     * @see de.tu_dresden.inf.ggp06_2.resolver.Term#chainOne(Substitution, RuleScope, TimerFlag) chainOne
     * @param sigma State of resolution
     * @param scope GDL rules scope
     * @param flag Timer flag
     * @param imp Actual implication to resolve with
     * @return Returns substitution that resolves current predicate to given 
     *         implication, if there is any.
     * @throws InterruptedException This exception is thrown once time expires during
     *                              procedure.
     */
    private Substitution chainOneImplication( Substitution sigma, 
                                              RuleScope    scope, 
                                              TimerFlag    flag, 
                                              Implication  imp ) 
    throws InterruptedException {
        if ( flag.interrupted() ) throw Const.interrupt;
        
        Substitution gamma = mgu( imp.getConsequence(), sigma );
        if ( gamma != null )
        	return imp.getPremises().chainOne(gamma, scope, flag);
        
        return null;
    }

    @Override
    public int getOperandCount() {
        return 0;
    }
    
}
