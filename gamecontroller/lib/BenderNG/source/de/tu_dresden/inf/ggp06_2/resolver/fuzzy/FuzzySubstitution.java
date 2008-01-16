package de.tu_dresden.inf.ggp06_2.resolver.fuzzy;

import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
/**
 * The overall idea is to drive more information into
 * particular substitution. Does this substitution lead to
 * a successful resolution, i.e. fuzzy one value? Most probably
 * not, since that is what usual substitutions are made for.
 * Therefore, this one holds values of various resolution elements
 * combined by T-Norm (fuzzy 'and' operator).
 *
 * This idea appear after it became clear that during
 * fuzzy evaluation of ExpressionList, one needs to know whether
 * current substutution is a successful one at all. If it is not
 * recurrsive calls should not be resolved at all, but simply
 * assessed with fuzzy zero instead.
 *
 *
 * @author Arsen Kostenko
 *
 */
public class FuzzySubstitution extends Substitution {

    /**
     *
     */
    private static final long serialVersionUID = 81135707190552102L;

    private double fuzzyValue;
    private boolean bottom;
    private boolean initialFuzzyValue = true;

    public FuzzySubstitution() {
        this.fuzzyValue = Expression.fuzzyZero;
        this.bottom = false;
        this.initialFuzzyValue = true;
    }

    public FuzzySubstitution(Substitution psi){
        this.putAll( psi );
        this.fuzzyValue = Expression.fuzzyOne;
        this.bottom = false;
        this.initialFuzzyValue = false;
    }

    public FuzzySubstitution(Substitution psi, FuzzySubstitution sigma) {
        this.putAll( psi );
        //logger.info( "fuzzyValue: "+fuzzyValue );
        this.fuzzyValue = sigma.fuzzyValue;
        this.bottom = sigma.bottom;
        this.initialFuzzyValue = sigma.initialFuzzyValue;
    }

    public double getFuzzyValue() {
        return fuzzyValue;
    }

    public void setFuzzyValue(double fuzzyValue) {
        //logger.info( "fuzzyValue: "+fuzzyValue );
        this.fuzzyValue = fuzzyValue;
        this.initialFuzzyValue = false;
    }

    public boolean isBottom() {
        return bottom;
    }

    public void setBottom(boolean bottom) {
        this.bottom = this.bottom || bottom;
    }

    public void tNorm(double fuzzyValue) {
        if (this.initialFuzzyValue) {
            this.fuzzyValue = fuzzyValue;
            this.initialFuzzyValue = false;
        } else {
            this.fuzzyValue = Expression.tNorm( this.fuzzyValue, fuzzyValue );
        }
    }
}
