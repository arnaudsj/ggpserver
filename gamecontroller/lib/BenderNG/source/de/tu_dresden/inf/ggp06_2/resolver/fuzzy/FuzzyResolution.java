package de.tu_dresden.inf.ggp06_2.resolver.fuzzy;

import java.util.HashSet;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;

public class FuzzyResolution extends HashSet<FuzzySubstitution> {
    private static final long serialVersionUID = 326534752238540666L;

    double fuzzyValue = Expression.fuzzyZero;
    boolean initialFuzzyValue = true;
    boolean onBottom = false;

    public FuzzyResolution(){
        this.initialFuzzyValue = true;
        this.fuzzyValue = Expression.fuzzyZero;
        this.onBottom = false;
    }

    public final double getFuzzyValue() {
        return fuzzyValue;
    }

    public void tConorm(double fuzzyValue){
        if (this.initialFuzzyValue){
            this.fuzzyValue = fuzzyValue;
            this.initialFuzzyValue = false;
        } else {
            this.fuzzyValue = Expression.tConorm(this.fuzzyValue, fuzzyValue);
        }

    }

    public final void setFuzzyValue(double value) {
        this.fuzzyValue = value;
    }

    public void tNorm(double fuzzyValue) {
        if (this.initialFuzzyValue){
            this.fuzzyValue = fuzzyValue ;
            this.initialFuzzyValue = false;
        } else {
            this.fuzzyValue = Expression.tNorm(this.fuzzyValue, fuzzyValue);
        }
    }

    public void replaceWithNextResolutionStep(FuzzyResolution fuzzyResolution) {
        this.clear();
        this.addAll(fuzzyResolution);
        this.tNorm(fuzzyResolution.getFuzzyValue());
        this.onBottom = fuzzyResolution.onBottom;
    }

    public void addAlternativeResolution(FuzzyResolution resolution) {
        double fuzzyValue = resolution.getFuzzyValue();
        this.addAll( resolution );
        if ((this.fuzzyValue < Expression.fuzzyOne &&
                fuzzyValue >= Expression.fuzzyOne) ||
            this.initialFuzzyValue){
            this.fuzzyValue = fuzzyValue;
            this.initialFuzzyValue = false;
        } else {
            this.fuzzyValue = Expression.tConorm( this.fuzzyValue, fuzzyValue );
        }
        this.onBottom = this.onBottom || resolution.onBottom;
    }

    @Override
    public boolean add(FuzzySubstitution fuzzySub) {
        this.tConorm( fuzzySub.getFuzzyValue() );
        this.onBottom = this.onBottom || fuzzySub.isBottom();
        return super.add(fuzzySub);
    }

    public final boolean isOnBottom() {
        return onBottom;
    }

}
