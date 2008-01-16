package de.tu_dresden.inf.ggp06_2.resolver;


public final class InitPredicate extends Predicate {

    /**
     * Init predicate is unary so it can be initialized only with one argument.
     * @param expression Argument to negate.
     */
    public InitPredicate(Expression expression) {
        super(Const.aInit, expression);
    }    
    
    /**
     * Apply given substitution to argument of init predicate, initialize new 
     * init predicate with result of application.
     * 
     * @param sigma Substitution to apply.
     * @return New init predicate.
     */
    @Override
    public Expression apply(Substitution sigma) {
        return new InitPredicate( operands.get(0).apply(sigma) );
    }

    @Override
    public String toString() {
        return (toString == null) ?
                    toString = "(INIT " + operands.get(0) + ")" :
                    toString;
    }
}
