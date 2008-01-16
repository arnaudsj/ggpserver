package de.tu_dresden.inf.ggp06_2.resolver.scope;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.TruePredicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.Term;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.ResolutionMemorizer;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;


public class MovesScope implements RuleScope {

    private static final Logger logger = Logger.getLogger(MovesScope.class);
    private final ExpressionList      moves;
    private final GameState           gameState;
    private final Theory              theory;
    private final ResolutionMemorizer memorizer;

    public MovesScope(Theory theory, GameState gameState, ExpressionList moves){
        this.theory    = theory;
        this.gameState = gameState;
        this.moves     = moves;
        this.memorizer = new ResolutionMemorizer();
    }

    /**
     * This method is actual facade implementation. That takes care of 
     * handling different expression storages like (theory, game state and
     * moves list).
     *  
     * @param expression - any expression, that we what to match against 
     * data given by Theory, GameState and MoveList
     */
    public ExpressionList getSimilarExpressions(Expression expression) {
        ExpressionList rets   = new ExpressionList();
        Atom           first  = (Atom) expression.firstOperand();
        boolean        isMove = Const.aDoes.equals( first );

        if (!Const.aTrue.equals(first) && !isMove) {
            rets.addAll( theory.get(expression.getKeyAtom()) );

        } else if (isMove) {
            rets.addAll(moves);

        } else {
            Atom key = (Atom) expression.secondOperand();
            if ( gameState.containsKey(key) )
                rets.addAll( gameState.get(key) );
        }
        
        ExpressionList retExp = new ExpressionList();
        for (Expression exp : rets )
            retExp.add( exp.apply( exp.uniquifier() ) );

        return retExp;

    }

    public List<Substitution> getProven(Substitution sigma, Expression expression) {
        if ( isLocallyMemorizable( expression ) ){
            if (logger.isTraceEnabled()){
                logger.trace(" recalling: "+expression+" from moves scope." );
            }
            return memorizer.getProven( sigma, expression );
        } else {
            if (logger.isTraceEnabled()){
                logger.trace(" recalling: "+expression+" from game state." );
            }
            return gameState.getProven( sigma, expression );
        }
    }

    public boolean isDisproven(Expression expression) {
        if ( isLocallyMemorizable( expression ) ){
            return this.memorizer.isDisproven( expression );
        } else {
            return this.gameState.isDisproven( expression );
        }
    }

    public boolean isProven(Expression expression) {
        if ( isLocallyMemorizable( expression ) ){
            return this.memorizer.isProven( expression );
        } else {
            return this.gameState.isProven( expression );
        }
    }

    public void setDisproven(Expression expression) {
        if ( isLocallyMemorizable( expression ) ){
            this.memorizer.setDisproven(expression);
        } else {
            this.gameState.setDisproven(expression);
        }
    }

    public void setProven(Expression expression, List<Substitution> subs) {
        if ( isLocallyMemorizable( expression ) ){
            if (logger.isTraceEnabled()){
                logger.trace(" memorizing: "+expression+" to moves scope." );
            }
            this.memorizer.setProven( expression, subs );
        } else {
            if (logger.isTraceEnabled()){
                logger.trace(" memorizing: "+expression+" to game state." );
            }
            this.gameState.setProven( expression, subs );
        }
    }

    private boolean isLocallyMemorizable(Expression expression) {
        Term first = expression.firstOperand();
        return Const.aNext.equals( first ) ||
               Const.aDoes.equals( first );
    }

    public List<Substitution> chainFunction(Substitution sigma, TruePredicate pred) {

        // create new substitution list
        List<Substitution> answers = new ArrayList<Substitution>();

        // get structure
        //Function structur = theory.getFSymbols().get( (Atom) pred.secondOperand() );
/*        
        // check if we hit a structure
        if (structur != null) {
                    
            // check for beeing counter
            if ( structur instanceof Counter ) {
                return answers;
            }
                
        }
*/        
        // if we reached this point we do normal chaining
        Atom key = (Atom) pred.secondOperand();
        ExpressionList rets = null;
        if ( gameState.containsKey(key) ){
            rets  = gameState.get(key);
            ExpressionList similarFluents = new ExpressionList();
            for (Expression exp : rets )
                similarFluents.add( exp.apply( exp.uniquifier() ) );
            
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
        // get structure
        //Function structur   = theory.getFSymbols().get( (Atom) pred.secondOperand() );
        Substitution answer = null;
/*        
        // check if we hit a structure
        if (structur != null) {
                    
            // check for beeing counter
            if ( structur instanceof Counter ) {
                //return null;
            }
                
        }
*/        
        // if we reached this point we do normal chaining
        Atom key = (Atom) pred.secondOperand();
        ExpressionList rets = null;
        if ( gameState.containsKey(key) ){
            rets  = gameState.get(key);
            ExpressionList similarFluents = new ExpressionList();
            for (Expression exp : rets )
                similarFluents.add( exp.apply( exp.uniquifier() ) );
            
            for (Expression aFluent : similarFluents){
                Substitution psi = pred.mgu(aFluent, sigma);
                if (null != psi){
                    answer = psi;
                    break;
                }
            }
        }
        return answer;
    }

}