package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.ArrayList;
import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.astvisitors.AbstractVisitor;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

/**
 *
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 *
 */
public final class Variable extends Term {

    /**
     *  variable counter
     */
    static int variableCounter = Integer.MIN_VALUE + 1;

    /**
     * As string for now, but should be a number for faster matching
    * the constructor could use a static map to make the conversation from
    * string to internal number
     */
    final String variable;

    /**
     *  internal numerical variable interpretation of the variable
     */
    final int number;

    /**
     *  Variable list
     */
    final ArrayList<Variable> varList;

    /**
     * Simple constructor for a random new variable.
     *
     */
    public Variable() {
        variable = "?" + variableCounter++;
        number   = variable.hashCode();
        varList  = new ArrayList<Variable>();
        varList.add(this);
    }

    /**
     * Create a new variable with given string as its content.
     * @param string Basic string for assigning properties of
     *               a variable.
     */
    public Variable(String string) {
        variable = string.toUpperCase();
        number   = variable.hashCode();
        varList  = new ArrayList<Variable>();
        varList.add(this);
    }

    /**
     * Simply defaults to false. No other variables
     * can occur inside a variable.
     *
     * @param var Variable to check
     * @return Should return true if given variable
     *         occurs inside an expression. In this
     *         case defaults to false, however.
     */
    @Override
    public boolean isPresent(Variable var) {
        return equals(var);
    }

    /**
     * This method tries to map the variable to an expression using the given
     * substitution.
     *
     * @param  sigma Substitution
     * @return If the variable is mapable it returns a expression; the variable
     *         otherwise.
     */
    @Override
    public Expression apply(Substitution sigma) {
        return sigma.containsKey(this) ? sigma.get(this) : this;
    }

    /**
     * @param obj Object to compare to.
     * @return Returns true if parameter is a variable with
     *         the same internal number.
     */
    @Override
    public final boolean equals( Object obj ) {
        return (obj instanceof Variable) && number == obj.hashCode();
    }

    /**
     * @param obj Object to compare to.
     * @return Returns true if parameter is a variable with
     *         the same internal number.
     */
    public final boolean equals( Variable obj ) {
        return number == obj.number;
    }

    /**
     * @return Returns the hashCode of the object.
     */
    @Override
    public final int hashCode() {
        return number;
    }

    /**
     * @return Returns the string representation of variable. If it is a anonym
     * variable it will return the variable number.
     */
    @Override
    public final String toString() {
        return variable;
    }

    /**
     * This method returns a new list of variables.
     *
     * @return Returns itself wrapped by a dummy list.
     */
    @Override
    public final List<Variable> getVariables() {
        return varList;
    }


    /**
     * Calculates the most generale unifier.
     *
     * @param target Expression to unify with.
     * @param sigma Substitution to consider while
     *              performing unification
     */
    @Override
    public Substitution mgu(Expression target, Substitution sigma) {
        Expression subThis   = this.apply(sigma);
        Expression subTarget = target.apply(sigma);

        if ( !subThis.equals( this ) )
            return subThis.mgu(subTarget, sigma);

        else if ( subTarget.isPresent( this ) )
            return null;

        else if ( subTarget instanceof Variable ) {
            Variable     newV = new Variable();
            Substitution psi  = new Substitution(sigma);
            psi.addAssociation( this,                 newV );
            psi.addAssociation( (Variable) subTarget, newV );
            return psi;

        } else {
            Substitution psi = new Substitution(sigma);
            psi.addAssociation( this, subTarget );
            return psi;
        }
    }


    /**
     * Variables are expected to unify with everything :) so
     * the reply for fuzzy evaluation is trivially <code>Expression.fuzzyOne</code>
     *
     * @param sigma State of resolution
     * @param scope GDL rules scope
     * @param flag Timer flag
     */
    @Override
    protected FuzzyResolution fuzzyEvaluateBody(
            FuzzySubstitution sigma, GameStateScope scope,
            List<Expression> guard, TimerFlag flag)
    throws InterruptedException {
        FuzzyResolution resolution = new FuzzyResolution();
        resolution.add( sigma );
        resolution.setFuzzyValue( Expression.fuzzyOne );
        return resolution;
    }

    @Override
    public final boolean isGround() {
        return false;
    }

    /**
     * This method provokes a class cast exception since if we would like to
     * throw an own exception here, we would have to put everything into
     * try/catch blocks.
     */
    @Override
    public final Atom getKeyAtom() {
        Object dummy = this;
        return (Atom) dummy;
    }

    @Override
    public final void processVisitor(AbstractVisitor visitor) {
        visitor.visitVariable(this);
    }

}
