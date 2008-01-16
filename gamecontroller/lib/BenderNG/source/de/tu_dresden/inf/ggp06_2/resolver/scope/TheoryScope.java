package de.tu_dresden.inf.ggp06_2.resolver.scope;

import java.util.List;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.TruePredicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.resolver.structures.functions.Function;

public class TheoryScope implements RuleScope {
    
    private static final Logger logger = Logger.getLogger( TheoryScope.class );
    private Theory theory;
    
    public TheoryScope(Theory theory) {
        this.theory = theory;
    }
    
    public Theory getTheory(){
        return theory;
    }
    
    public ExpressionList getSimilarExpressions(Expression expression) {
        logger.trace("entering getSimilarExpressions");

        ExpressionList rets = new ExpressionList();
        rets.addAll( theory.get( expression.getKeyAtom() ) );

        ExpressionList retExp = new ExpressionList();
        for (Expression exp : rets )
            retExp.add( exp.apply( exp.uniquifier() ) );

        return retExp;
    }

    public List<Substitution> getProven(Substitution sigma, Expression expression) {
        return null;
    }

    public boolean isDisproven(Expression expression) {
        return false;
    }

    public boolean isProven(Expression expression) {
        return false;
    }

    public void setDisproven(Expression expression) {
    }

    public void setProven(Expression expression, List<Substitution> subs) {
    }

    public Function getStructure(Atom fSymbol) {
        Function potentialStructure = theory.getFSymbols().get(fSymbol);
        if (potentialStructure != null && potentialStructure.isStructure())
            return potentialStructure;
        return null;
    }

    public List<Substitution> chainFunction(Substitution sigma, TruePredicate pred) {
        return null;
    }

    public Substitution chainOneFunction(Substitution sigma, TruePredicate pred) {
        return null;
    }
}
