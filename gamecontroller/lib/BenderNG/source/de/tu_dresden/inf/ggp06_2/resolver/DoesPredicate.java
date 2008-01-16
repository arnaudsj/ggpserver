package de.tu_dresden.inf.ggp06_2.resolver;


public final class DoesPredicate extends Predicate {

    /**
     * Does predicate is binary so it can be initialized only with two argument.
     * @param expression Argument to negate.
     */
    public DoesPredicate(Expression role, Expression move) {
        super(Const.aDoes, role, move);
    }    
    
    /**
     * Does predicate is binary so it can be initialized only with two argument.
     * @param expression Argument to negate.
     */
    public DoesPredicate(ExpressionList expressionList) {
        super(Const.aDoes, expressionList);
    }    

    /**
     * Apply given substitution to argument of does predicate, initialize new 
     * does predicate with result of application.
     * 
     * @param sigma Substitution to apply.
     * @return New does predicate.
     */
    @Override
    public Expression apply(Substitution sigma) {
        return new DoesPredicate( operands.apply(sigma) );
    }

    @Override
    public String toString() {
        return (toString == null) ?
                    toString = "(DOES " + operands + ")" : 
                    toString;
    }
}
