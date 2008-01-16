package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.ArrayList;
import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.astvisitors.AbstractVisitor;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.RuleScope;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

/**
 *
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 *
 */
public final class OrOperator extends Connective {

    /**
     * Alternative way of using OrOperator is with a
     * list of expression, where relations between them
     * are treeted as disjunction.
     *
     * @param operands List of expressions to be treated
     *                 as operands for disjunction.
     */
    public OrOperator(ExpressionList operands){
        this.operands.addAll( operands );
    }

    /**
     * Apply substitution to premises of current operator,
     * which produces list of new premises. These new premises
     * are used to create new &quot;or&quot; operator, which
     * is returnted as result.
     *
     * @param sigma Substitution to apply
     * @return Returns a new operator, obtained
     *         from a current one, by application
     *         of substitution.
     */
    @Override
    public Expression apply(Substitution sigma) {
        return new OrOperator(operands.apply(sigma));
    }


    /**
     * Iterate through premises asking each of them to perform resolution
     * on given substitution and rules scope. If some results are produced,
     * accumulate them and return accumulated results.
     *
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
        if ( flag.interrupted() ) throw Const.interrupt;
        List<Substitution> answers = new ArrayList<Substitution>();
        for (Expression anExpression : operands) {
            List<Substitution> psis = anExpression.chain(sigma, scope, flag);
            //  TODO: Check if this speeds up proving
            // if (psis != null) answers.addAll(psis);
            addAllNew(psis, answers);
        }
        return answers;
    }

    /**
     *
     * @param src
     * @param dest
     */
    private void addAllNew(List<Substitution> src, List<Substitution> dest) {
        if (src == null || src.isEmpty())
            return;

        for (Substitution aSubstitution : src)
            if (!dest.contains(aSubstitution))
                dest.add(aSubstitution);
    }

    /**
     * Iterate through premises and ask each of them for <code>chainOne</code>,
     * once any result is produced - return it. If no result were produced return null.
     *
     * @see de.tu_dresden.inf.ggp06_2.resolver.Expression#chainOneBody(Substitution, RuleScope, TimerFlag) Expression.chainOneBody
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
        for (Expression e : operands) {
            if ( flag.interrupted() ) throw Const.interrupt;

            Substitution psi = e.chainOne(sigma, scope, flag);
            if ( psi != null )
                return psi;
        }
        return null;
    }


    @Override
    protected FuzzyResolution fuzzyEvaluateBody(
            FuzzySubstitution sigma, GameStateScope scope,
            List<Expression> guard, TimerFlag flag)
    throws InterruptedException {
        FuzzyResolution orResolution = new FuzzyResolution();
        for (Expression e: operands){
            FuzzyResolution expressionFuzzyResolution = e.fuzzyEvaluate(
                    sigma, scope, guard, flag );
            //value = Expression.tConorm( value, expressionfuzzyValue );
            for (FuzzySubstitution fuzzySub : expressionFuzzyResolution){
                if ( ! orResolution.contains( fuzzySub )){
                    orResolution.add( fuzzySub );
                }
            }
        }
        return orResolution;
    }

    @Override
    public Atom firstOperand() {
        return Const.aOrOp;
    }

    @Override
    public Substitution mgu(Expression target, Substitution sigma) {
        if (target instanceof Variable)
            return ((Variable) target).mgu(this, sigma);

        else if (target instanceof OrOperator )
            return operands.mgu( ((OrOperator) target).operands, sigma);

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof OrOperator &&
               operands.equals( ((OrOperator) obj).operands );
    }

    @Override
    public String toString() {
        return (toString == null) ?
                    toString = "(OR " + operands + ")" :
                    toString;
    }

    @Override
    public boolean isGround() {
        for (Expression exp: operands)
            if ( !exp.isGround() )
                return false;
        return true;
    }

    @Override
    public Atom getKeyAtom() {
        return Const.aOrOp;
    }

    @Override
    public final void processVisitor(AbstractVisitor visitor) {
        visitor.visitOr(this);
    }

}
