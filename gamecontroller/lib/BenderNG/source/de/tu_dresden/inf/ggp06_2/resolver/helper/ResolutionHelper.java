package de.tu_dresden.inf.ggp06_2.resolver.helper;

import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.RuleScope;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class ResolutionHelper {
    
    /**
     * This method returns wether the given atom can be found or not.
     * @param toResolve
     * @return
     * @throws InterruptedException
     */
    public static boolean isResolvable( Expression toResolve, 
                                        RuleScope  scope, 
                                        TimerFlag  flag ) 
    throws InterruptedException {
        return toResolve.chainOne( new Substitution(), scope, flag ) != null;
    }
    
    /**
     * Just a sequencial application of given substitution sigma to
     * given expression toFill. Once no more changes happed,
     * newly created expression is returned.
     * @param toFill
     * @param sigma
     * @return
     */    
    public static Expression produceDerivativeFromOneSubstitution( 
            Expression   toFill, Substitution sigma ) {
        if (null == sigma) 
            return null;

        Expression retExp = toFill.apply(sigma);

        while ( !retExp.equals(toFill) ) {
            toFill = retExp;
            retExp = toFill.apply(sigma);
        }
        
        return retExp;
    }
    
    public static ExpressionList produceDerivativesFromSubstitutions(
            Expression toFill, List<Substitution> sigmas) {
        
        ExpressionList results = new ExpressionList();
        Expression     e;
        
        for ( Substitution aSubstitution: sigmas ) {        

            if (aSubstitution.isEmpty())
                continue;
            
            e = toFill;
            
            // Apply current substitution to the expression supplied.
            Expression retExp = e.apply(aSubstitution);
            
            /* 
             * While the result obtained from substitution is not equal to the 
             * one obtained on the previous stage (in other words: while there 
             * are any chages) we continously applying the substitution to 
             * itself.
             */
            while ( !retExp.equals(e) ) {
                e = retExp;
                retExp = e.apply(aSubstitution);
            }
            
            // Finally, if the result of substitution application is NOT in 
            // current results - add it. :)
            if ( !results.contains(retExp) )
                results.add(retExp);            
        }
        
        return results;
    }    
    
    /**
     * Same sequencial application of substitution. This time
     * to a list of expressions.
     * @param toFill
     * @param sigma
     * @return
     */
    public static ExpressionList produceMultipleDerivativeFromOneSubstitution(
            ExpressionList toFill, Substitution   sigma) {
        if (null == sigma) 
            return null;
        
        ExpressionList retExp = toFill.apply(sigma);
        while ( !retExp.equals(toFill) ) {
            toFill = retExp;
            retExp = toFill.apply(sigma);
        }
        
        return retExp;
    }    
    
    /**
     * This method is looking for all possible resolutions of a given pattern 
     * Any substitutions produced in a result of such resolutions are applied
     * to a given template. List of expressions created via such application
     * is returned as a result.  
     * @param template Holds a resulting expression template.
     * @param toResolve Holds a pattern to resolve in a given scope
     * @param scope Holds all relevant expressions.
     * @return list of newly created expressions that fit to a given template and produced substitution.
     * @throws InterruptedException This exception is thrown in case of time limit.
     */
    public static ExpressionList resolveAndApply( Expression template, 
                                                  Expression toResolve, 
                                                  RuleScope  scope, 
                                                  TimerFlag  flag ) 
    throws InterruptedException {
        List<Substitution> sigmas = toResolve.chain( 
                new Substitution(), scope, flag );
        return produceDerivativesFromSubstitutions( template, sigmas );
    }

}