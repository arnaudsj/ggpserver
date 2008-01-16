package de.tu_dresden.inf.ggp06_2.resolver.helper;

import java.util.ArrayList;
import de.tu_dresden.inf.ggp06_2.resolver.AndOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Connective;
import de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Implication;
import de.tu_dresden.inf.ggp06_2.resolver.NotOperator;
import de.tu_dresden.inf.ggp06_2.resolver.OrOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;

public final class ResolverUtils {
    
    /**
     * This Method returns a ExpressionList with flattened implications based on
     * the given expList.
     * 
     * @param expList
     * @return
     */
    public static ExpressionList flattenExpressionList(ExpressionList expList) {
        ExpressionList         flattened = new ExpressionList();
        ArrayList<Implication> ruleList  = new ArrayList<Implication>();

        // get rules separated
        for (Expression exp : expList)
            if (exp instanceof Implication)
                ruleList.add( (Implication) exp );
            else
                flattened.add( exp );

        // if we do not have rules to be flattened
        if ( ruleList.isEmpty() )
            return flattened;
        
        // converting an expression to a disjunction normal form
        int ruleIdx = 0;
        for (Implication rule : ruleList)
            ruleList.set( ruleIdx++, moveNegationToAtoms(rule) );

        /*
         * factorization of rules
         */ 
        ArrayList <ExpressionList> newRulesPremises;
        ExpressionList             newPremises;
        Connective                 premise;
        int                        opCount;

        // iterate over rules
        for (int i = 0; i < ruleList.size(); i++) {
        
            ExpressionList premises = ruleList.get(i).getPremises();

            // iterate over premises
            for (int j = 0, premisesSize = premises.size(); j < premisesSize;) {

                premises = ruleList.get(i).getPremises();
                    
                // process OR
                if ( premises.get(j) instanceof OrOperator ) {
                    
                    newRulesPremises = new ArrayList<ExpressionList>();
                    premise          = (Connective) premises.get(j);
                    opCount          = premise.getOperandCount();
                    
                    // create new premises
                    for (int r = 0; r < opCount; r++) {
                        newPremises = new ExpressionList();

                        for (int k = 0; k < j; k++)
                            newPremises.add( premises.get(k) );                                
                        
                        newPremises.add( premise.getOperands().get(r) );
                        
                        for (int k = j+1; k < premisesSize; k++)
                            newPremises.add( premises.get(k) );        
                        
                        newRulesPremises.add( newPremises );
                    }
                        
                    // create new consequence
                    Expression consequence = ruleList.get(i).getConsequence();
                    ruleList.set( i, new Implication( consequence, 
                                                      newRulesPremises.get(0) ) );

                    for (int r = 1; r < opCount; r++)
                        ruleList.add( new Implication( consequence, 
                                                       newRulesPremises.get(r) ) );                            
                    
                } else 
                    
                    // process AND
                    if ( premises.get(j) instanceof AndOperator ) {
                        
                        premise     = (Connective) premises.get(j);
                        newPremises = new ExpressionList();

                        for ( int k = 0; k < j; k++ )
                            newPremises.add( premises.get(k) );

                        for (int r = 0, rMax = premise.getOperands().size(); r < rMax; r++)
                            newPremises.add( premise.getOperands().get(r) );           
                        
                        for ( int k = j+1; k < premisesSize; k++)
                            newPremises.add( premises.get(k) );

                        ruleList.set( i, new Implication( ruleList.get(i).getConsequence(), 
                                                          newPremises) );

                    } else
                        j++;
            }
        }        

        // add all factorized rules
        flattened.addAll(ruleList);
        return flattened;
    }

    public final static Implication moveNegationToAtoms(Implication rule) {
        ExpressionList rulePremises = rule.getPremises();
        ExpressionList premises     = new ExpressionList();
        for (Expression premise : rulePremises) 
            premises.add( moveNegationToAtoms(premise) );
        return new Implication(rule.getConsequence(), premises);
    }

    public final static Expression moveNegationToAtoms(Expression exp) {

        // investigate NOT
        if (exp instanceof NotOperator) {
            Expression notOperand = ((NotOperator)exp).getOperand();
            
            // NOT contains OR
            if (notOperand instanceof OrOperator){
                ExpressionList operands    = ((OrOperator)notOperand).getOperands();
                ExpressionList andOperands = new ExpressionList();
                for (Expression operand : operands)
                    andOperands.add( moveNegationToAtoms(new NotOperator(operand)) );           
                return new AndOperator(andOperands);                  
            }
        
            // NOT contains AND
            if (notOperand instanceof AndOperator) {
                ExpressionList operands   = ((AndOperator)notOperand).getOperands();
                ExpressionList orOperands = new ExpressionList();
                for (Expression operand : operands)
                    orOperands.add( moveNegationToAtoms(new NotOperator(operand)) );
                return new OrOperator(orOperands);                  
            }
                
            // NOT contains NOT
            if (notOperand instanceof NotOperator)
                return moveNegationToAtoms(((NotOperator)notOperand).getOperand());
            return new NotOperator( ((NotOperator)exp).getOperand() );           
        }
        
        // investigate AND
        if (exp instanceof AndOperator) {
            ExpressionList operands    =  ( (Connective) exp).getOperands();
            ExpressionList andOperands = new ExpressionList();
            for (Expression operand : operands)
                andOperands.add( moveNegationToAtoms(operand) );
            return new AndOperator(andOperands);            
        }

        // investigate OR
        if (exp instanceof OrOperator) {
            ExpressionList operands   = ( (Connective) exp).getOperands();
            ExpressionList orOperands = new ExpressionList();
            for (Expression operand : operands)
                orOperands.add( moveNegationToAtoms(operand) );
            return new OrOperator(orOperands);
        }
                
        // investigate DISTINCT
        if (exp instanceof DistinctOperator) {
            ExpressionList operands = ((Connective) exp).getOperands();
            return new DistinctOperator( operands.get(0), operands.get(1) );
        }
        
        // only ATOM or PREDICATE is left
        return (exp instanceof Atom) ?
                new Atom( exp.toString() ) : 
                new Predicate(  (Atom)      exp.firstOperand(), 
                              ( (Predicate) exp).getOperands() );
    }
    
    
    public Substitution mgu(Expression t1, Expression t2) {
        
        if ( t1.equals(t2) )
            return new Substitution();
        
        // occure check
        if ( t1 instanceof Variable ) {
            
            if ( t2.getVariables().contains( t1 ) ) {
                return null;
            } else {
                Substitution subst = new Substitution();
                subst.addAssociation( (Variable) t1 , t2 );
                return subst; 
            }
        }
        
        // symmetrie
        if ( t2 instanceof Variable )
            return ( mgu( t2, t1 ) );
        
        // und so weiter
        return null;
    }
    
}
