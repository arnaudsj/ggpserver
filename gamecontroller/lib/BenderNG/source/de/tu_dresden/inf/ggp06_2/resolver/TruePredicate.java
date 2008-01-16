package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.RuleScope;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;


public final class TruePredicate extends Predicate {

    private final Atom secondOperand;

    /**
     * True operator is unary so it can be initialized only with one argument.
     * @param expression Argument to negate.
     */
    public TruePredicate(Expression expression) {
        super(Const.aTrue, expression);
        secondOperand = (Atom) expression.firstOperand();
    }

    /**
     * Apply given substitution to argument of true operator, initialize new
     * true operator with result of application.
     *
     * @param sigma Substitution to apply.
     * @return New true operator.
     */
    @Override
    public Expression apply(Substitution sigma) {
        return new TruePredicate( operands.get(0).apply(sigma) );
    }

    @Override
    public String toString() {
        return (toString == null) ? "(TRUE " + operands.get(0) + ")" : toString;
    }

    @Override
    public Term secondOperand() {
        return secondOperand;
    }

    @Override
    protected List<Substitution> chainBody( Substitution sigma,
                                            RuleScope    scope,
                                            TimerFlag    flag )
    throws InterruptedException {
        return scope.chainFunction(sigma, this);
    }

    @Override
    protected Substitution chainOneBody( Substitution sigma,
                                         RuleScope    scope,
                                         TimerFlag    flag )
    throws InterruptedException {
        return scope.chainOneFunction(sigma, this);
    }

    @Override
    protected FuzzyResolution fuzzyEvaluateBody( FuzzySubstitution sigma,
                                                 GameStateScope    scope,
                                                 List<Expression>  guard,
                                                 TimerFlag         flag )
    throws InterruptedException {
        Atom            fluentName = secondOperand;
        FuzzyResolution sigmas     = new FuzzyResolution();

        ExpressionList candidates = scope.getSimilarFluents( fluentName );
        for ( Expression e : candidates) {
            Substitution psi = mgu(e, sigma);
            if (null != psi)
                evaluateToFuzzyTruth( sigma, sigmas, psi );
        }

        if ( sigmas.isEmpty() )
            evaluateToFuzzyFalse( sigma, sigmas );

        return sigmas;
    }

    private void evaluateToFuzzyTruth(FuzzySubstitution sigma, FuzzyResolution sigmas, Substitution psi) {
        FuzzySubstitution fuzzySub = new FuzzySubstitution(psi, sigma);
        fuzzySub.tNorm( Expression.fuzzyOne );
        sigmas.add( fuzzySub );
        sigmas.setFuzzyValue( Expression.fuzzyOne );
    }
}
