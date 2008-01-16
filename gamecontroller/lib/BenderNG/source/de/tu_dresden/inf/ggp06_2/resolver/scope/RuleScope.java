package de.tu_dresden.inf.ggp06_2.resolver.scope;

import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.TruePredicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;

/**
 * The RuleScope interface follows the "facade" pattern.
 * 
 * Since game state, moves and rules are all now stored in different classes, 
 * there are separate classes that search for matches within appropriate
 * containers.
 * 
 * @author ingo
 *
 */
public interface RuleScope {
    
    public ExpressionList getSimilarExpressions(Expression expression);
    
    public List<Substitution> getProven( Substitution sigma, Expression expression );
    
    public void setProven(Expression expression, List<Substitution> subs);
    
    public boolean isProven(Expression expression);
    
    public void setDisproven(Expression expression);
    
    public boolean isDisproven(Expression expression);
    
    public List<Substitution> chainFunction(Substitution sigma, TruePredicate pred);
    public Substitution       chainOneFunction(Substitution sigma, TruePredicate pred);

}
