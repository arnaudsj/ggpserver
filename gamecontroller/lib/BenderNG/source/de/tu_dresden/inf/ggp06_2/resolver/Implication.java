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
public class Implication extends Expression {

    protected static final int IMP_HASH_SEED = 2147006989;

    /* content of the implication */
    protected Expression     consequence;
    protected ExpressionList premises;

    /**
     * Constructs a implication with an expression as consequence and an
     * expression list as the premises.
     *
     * @param consequence Consequence of the implication
     * @param premises List of premises is for implication
     */
    public Implication(Expression consequence, ExpressionList premises) {
        this.consequence = consequence;
        this.premises    = premises;
    }

    /**
     * Some implications could have just one premise. In case of hand-coding
     * rules such form is much more compact.
     *
     * @param consequence Consequence of the implication
     * @param premise1 Single and only premise
     */
    public Implication(Expression consequence, Expression premise1) {
        this.consequence = consequence;
        this.premises    = new ExpressionList();
        this.premises.add( premise1 );
    }

    /**
     * Sometimes even two-premise implications happen, and such form also
     * comes handy in case of hand-coding.
     *
     * @param consequence Consequence of the implication
     * @param premise1 First premise
     * @param premise2 Second premise
     */
    public Implication(Expression consequence, Expression premise1, Expression premise2) {
        this.consequence = consequence;
        this.premises    = new ExpressionList();
        this.premises.add( premise1 );
        this.premises.add( premise2 );
    }

    /**
     * @return Returns whether the variable var occurs in this implication.
     */
    @Override
    public boolean isPresent(Variable var) {
        return consequence.isPresent(var) && premises.isPresent(var);
    }

    /**
     * Implication is equal to another one, only in case
     * <ol>
     *     <li>They have the same consequence</li>
     *     <li>They also have the same premises</li>
     * </ol>
     * Everything else is false
     *
     * @param Object to compare to
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof Implication ) {
            Implication other = (Implication) obj;
            if ( other.consequence.equals(consequence) )
                return ( other.premises.equals(premises) );
        }
        return false;
    }

    /**
     * Following form of string is produced:
     *  <i>&quot;( <= &quot; + consequence + &quot; &quot; + premises + &quot; )&quot;</i>
     */
    @Override
    public String toString() {
        return (toString == null) ?
                toString = "( <= " + consequence + " " + premises + " )" :
                toString;
    }

    /**
     * @return Returns the consequence of this implication.
     */
    public Expression getConsequence() {
        return this.consequence;
    }

    /**
     * @return Returns the premises list of this implication.
     */
    public ExpressionList getPremises() {
        return this.premises;
    }

    /**
     * @return Returns a new implication in which on both parts the substitution sigma
     * was applied.
    */
    @Override
    public Expression apply(Substitution sigma) {
        return new Implication( consequence.apply(sigma),
                                premises.apply(sigma) );
    }

    /**
     * Simply ask for chaining of premises, which are stored as <code>ExpressionList</code>
     *
     * @see de.tu_dresden.inf.ggp06_2.resolver.Expression#chainBody(Substitution, RuleScope, TimerFlag) Expression.chainBody
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
    protected List<Substitution> chainBody(
            Substitution sigma, RuleScope scope, TimerFlag flag)
    throws InterruptedException {

        return premises.chain(sigma, scope, flag);
        /*
        boolean proven = ( ( answers != null ) && ( ! answers.isEmpty() ) );

        if (proven)
            return answers;

        return null;
        */
    }

    /**
     * Simply ask for <code>chainOne</code> of premises, which are stored as <code>ExpressionList</code>
     *
     * @see de.tu_dresden.inf.ggp06_2.resolver.Expression#chainOneBody(Substitution, RuleScope, TimerFlag) Expression.chainOneBody
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
        return premises.chainOne( sigma, scope, flag );
    }

    @Override
    protected FuzzyResolution fuzzyEvaluateBody(
            FuzzySubstitution sigma, GameStateScope scope,
            List<Expression> guard, TimerFlag flag)
    throws InterruptedException {
        return premises.fuzzyEvaluate(sigma, scope, guard, flag);
    }

    /**
     * The first operand is everytime the implication atom.
     */
    @Override
    public Term firstOperand() {
        return Const.aImpOp;
    }

    /**
     * @return The second operand is the first operand of the consequence.
     */
    @Override
    public Term secondOperand() {
        return this.consequence.firstOperand();
    }

    /**
     * @return Returns all Variables occuring in this implication.
     */
    @Override
    public List<Variable> getVariables() {
        List<Variable> allVars = new ArrayList<Variable>();
        allVars.addAll( consequence.getVariables() );
        allVars.addAll( premises.getVariables()    );
        return allVars;
    }

    /**
     * @return Returns the most general unification of this implication under
     *         a given substitution.
     */
    @Override
    public Substitution mgu(Expression target, Substitution sigma) {
        return target.mgu( consequence.apply(sigma), sigma);
    }

    @Override
    public boolean isGround() {
        for (Expression exp : premises)
            if (!exp.isGround())
                return false;
        return true;
    }

    @Override
    public Atom getKeyAtom() {
        return consequence.getKeyAtom();
    }

    @Override
    public int getOperandCount() {
        return consequence.getOperandCount();
    }

    @Override
    public final void processVisitor(AbstractVisitor visitor) {
        visitor.visitImplication(this);
    }

}
