package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;


public final class LegalPredicate extends Predicate {

    /**
     * @param expression Argument to negate.
     */
    public LegalPredicate(Expression expression) {
        super(Const.aLegal, expression);
    }    
    
    /**
     * @param expression Argument to negate.
     */
    public LegalPredicate(ExpressionList expressionList) {
        super(Const.aLegal, expressionList);
    }    
    
    /**
     * 
     * 
     * @param sigma
     * @param scope
     * @param flag
     * @return
     * @throws InterruptedException
     */
    @Override
    protected FuzzyResolution fuzzyEvaluateBody(
            FuzzySubstitution sigma, GameStateScope scope, 
            List<Expression> guard, TimerFlag flag) 
    throws InterruptedException {
        
        ExpressionList candidates = getAppropriateMoves( scope );
        
        if (null == candidates || candidates.isEmpty())
            throw new InterruptedException(" seem to evaluate completely terminal state ");
        
        FuzzyResolution sigmas = new FuzzyResolution();
        for (Expression e : candidates ){
            Substitution psi = this.mgu( e, sigma );
            if (null != psi){            
                evaluatePositively( sigma, sigmas, psi );                
            }
        }
        if (sigmas.isEmpty()){
            evaluateToFuzzyFalse( sigma, sigmas );
        }        
        return sigmas;
    }

    /**
     * 
     * @param scope
     * @return
     */
    private ExpressionList getAppropriateMoves(GameStateScope scope) {
        ExpressionList candidates = null;
        Term role = this.secondOperand();
        if (role instanceof Atom){
            candidates = scope.getLegalMoves((Atom) role);
        } else {
            candidates = scope.getLinearLegalMoves();
        }
        return candidates;
    }

    /**
     * Apply given substitution to argument of legal predicate, initialize new 
     * legal predicate with result of application.
     * 
     * @param sigma Substitution to apply.
     * @return New legal predicate.
     */
    @Override
    public Expression apply(Substitution sigma) {
        return new LegalPredicate( operands.apply(sigma) );
    }

    @Override
    public String toString() {
        return (toString == null) ?
                    toString = "(LEGAL " + operands + ")" :
                    toString;
    }
}
