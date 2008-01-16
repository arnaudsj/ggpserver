package de.tu_dresden.inf.ggp06_2.resolver.scope;

import java.util.ArrayList;
import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.TruePredicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.resolver.structures.functions.Function;

public class GameStateScope implements RuleScope {

    private final GameState gameState;
    private final Theory    theory;


    public GameStateScope(Theory theory, GameState gameState){
        this.theory    = theory;
        this.gameState = gameState;
    }

    public ExpressionList getSimilarExpressions(Expression expression) {
        ExpressionList retExp = new ExpressionList();

        // if we hit a fluent we add the stuff from the game state
        if ( Const.aTrue.equals( expression.firstOperand() ) ) {

            for (Expression exp : gameState.get(expression.secondOperand()) )
                retExp.add( exp.apply( exp.uniquifier() ) );

            return retExp;
        }

        // otherwise we use the theory objects
        Atom key = expression.getKeyAtom();
        for (Expression exp : theory.get(key) )
            retExp.add( exp.apply( exp.uniquifier() ) );

        return retExp;
    }

    public ExpressionList getSimilarFluents(Atom key){
        ExpressionList fluents  = new ExpressionList();
        ExpressionList similars = gameState.get( key );
        for (Expression exp : similars)
            fluents.add( exp.apply( exp.uniquifier() ) );

        return fluents;
    }

    public ExpressionList getLinearLegalMoves(){
        return gameState.getLinearLegalMoves();
    }

    public List<Substitution> getProven(Substitution sigma, Expression expression) {
        return gameState.getProven( sigma, expression );
    }

    public List<FuzzySubstitution> getProvenForFuzzy(Substitution sigma, Expression expression) {
        return gameState.getProvenForFuzzy( sigma, expression );
    }

    public boolean isDisproven(Expression expression) {
        return gameState.isDisproven( expression );
    }

    public boolean isProven(Expression expression) {
        return gameState.isProven( expression );
    }

    public void setDisproven(Expression expression) {
        gameState.setDisproven(expression);
    }

    public void setProven(Expression expression, List<Substitution> subs) {
        gameState.setProven( expression, subs );
    }

    public boolean isFuzzylyEvaluated(Expression expression) {
        return gameState.isFuzzylyEvaluated( expression );
    }

    public FuzzyResolution getFuzzyResolutionStage(FuzzySubstitution sigma, Expression expression) {
        return gameState.getFuzzyResolutionStage( sigma, expression );
    }

    public void setFuzzyEvaluationStage(Expression expression, FuzzyResolution sigmas) {
        gameState.setFuzzyEvaluationStage( expression, sigmas );
    }

    public ExpressionList getLegalMoves(Atom role) {
        return gameState.getLegalMoves( role );
    }

    public Function getStructure(Atom fSymbol) {
        Function potentialStructure = theory.getFSymbols().get(fSymbol);
        if (potentialStructure != null && potentialStructure.isStructure())
            return potentialStructure;
        return null;
    }

    public List<Substitution> chainFunction(Substitution sigma, TruePredicate pred) {

        // create new substitution list
        List<Substitution> answers = new ArrayList<Substitution>();
        Atom secondOperand = (Atom) pred.secondOperand();
        // There are basically two options:
        if ( gameState.containsKey(secondOperand) ){

            ExpressionList similarFluents = getSimilarFluents( secondOperand );

            for (Expression aFluent : similarFluents){
                Substitution psi = pred.mgu(aFluent, sigma);
                if (null != psi && !answers.contains( psi )){
                    answers.add( psi );
                }
            }
        }
        return answers;
    }

    public Substitution chainOneFunction(Substitution sigma, TruePredicate pred) {
        Atom secondOperand = (Atom) pred.secondOperand();

        if ( gameState.containsKey(secondOperand) ){

            ExpressionList similarFluents = getSimilarFluents( secondOperand );

            for (Expression aFluent : similarFluents){
                Substitution psi = pred.mgu(aFluent, sigma);
                if (null != psi){
                    return psi;
                }
            }
        }
        return null;

    }

}
