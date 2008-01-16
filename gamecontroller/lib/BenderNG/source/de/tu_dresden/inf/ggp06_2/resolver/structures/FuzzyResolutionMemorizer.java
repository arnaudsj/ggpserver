package de.tu_dresden.inf.ggp06_2.resolver.structures;

import java.util.HashMap;
import java.util.Map;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;

public class FuzzyResolutionMemorizer {

    Map<Expression, FuzzyResolutionMemo> evaluated;

    static class FuzzyResolutionMemo{
        double fuzzyValue;
        FuzzyResolution resolution;
    }

    /**
     *
     * TODO: write JavaDoc
     */
    public FuzzyResolutionMemorizer(){
        this.evaluated = new HashMap<Expression, FuzzyResolutionMemo>();
    }

    /**
     * TODO: write JavaDoc
     * @param evaluatedStatements
     */
    public FuzzyResolutionMemorizer(
            Map<Expression, FuzzyResolutionMemo> evaluatedStatements) {
        this.evaluated    = evaluatedStatements;
    }

    /**
     * TODO: write JavaDoc
     *
     * @param sigma
     * @param e
     * @return
     */
    public FuzzyResolution getFuzzyResolutionStage( FuzzySubstitution sigma, Expression e ) {
        Substitution canon = e.deriveCanon();
        Expression canonical = e.apply( canon );

        FuzzyResolutionMemo memo = evaluated.get(canonical);
        FuzzyResolution resolution = memo.resolution;
        FuzzyResolution returned = new FuzzyResolution();
        returned.setFuzzyValue( memo.fuzzyValue );

        if (null != resolution){
            for (FuzzySubstitution aProof : resolution ) {
                Substitution psi = canon.apply( aProof );
                psi = psi.restrict( e );
                psi = sigma.apply( psi );
                returned.add(new FuzzySubstitution(psi, aProof));
            }
        }
        return returned;
    }

    /**
     * TODO: write JavaDoc
     *
     * @param e
     * @return
     */
    public boolean isFuzzylyEvaluated(Expression e){
        return evaluated.containsKey( e.canonize() );
    }

    /**
     * TODO: write JavaDoc
     *
     * @param e
     * @param fuzzyValue
     * @param subs
     */
    public void setFuzzyEvaluationStage(Expression e, FuzzyResolution subs){
        Substitution canon = e.deriveCanon();
        Expression canonical = e.apply(canon);
        FuzzyResolution expressionRestictedSubs = null;
        for ( FuzzySubstitution psi : subs ) {
            Substitution sigma = psi.restrict(e);
            Substitution sigma1 = sigma.canonize(canon);

            if (!sigma1.isEmpty() &&
                    ( null == expressionRestictedSubs ||
                            !expressionRestictedSubs.contains(sigma1)) ){
                if (null == expressionRestictedSubs){
                    expressionRestictedSubs = new FuzzyResolution();
                }
                expressionRestictedSubs.add(new FuzzySubstitution(sigma1, psi));
            }
        }
        FuzzyResolutionMemo memo = new FuzzyResolutionMemo();
        memo.fuzzyValue = subs.getFuzzyValue();
        if (null != expressionRestictedSubs){
            expressionRestictedSubs.setFuzzyValue( memo.fuzzyValue );
            memo.resolution = expressionRestictedSubs;
        }
        evaluated.put( canonical,  memo);
    }

    public final void clear(){
        this.evaluated.clear();
    }

    /**
     * TODO: write JavaDoc
     * @return
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("proven:\n");
        for(Expression key : this.evaluated.keySet()){
            FuzzyResolution fuzzyStage = this.evaluated.get( key ).resolution;
            sb.append( key ).append( " : " ).append( fuzzyStage.getFuzzyValue() ).append( " : " );
            for(FuzzySubstitution psi : fuzzyStage) {
                sb.append( psi ).append( " ;" );
            }
            sb.append( "\n" );
        }
        return sb.toString();
    }
}
