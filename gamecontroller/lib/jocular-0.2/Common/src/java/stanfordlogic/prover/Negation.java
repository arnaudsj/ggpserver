///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

/**
 * 
 */
package stanfordlogic.prover;

import java.io.PrintStream;
import java.util.Map;

import stanfordlogic.gdl.SymbolTable;


/**
 * A negation of a sentence. The negation is provable when the negated sentence
 * is <i>not</i> provable.
 */
public class Negation extends Expression
{
    private Expression negated_;
    
    public Negation(Expression negated)
    {
        negated_ = negated;
        
    }
    
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        
        if ( obj instanceof Negation == false )
            return false;
        
        return negated_ == ((Negation) obj).negated_;
    }
    
    @Override
    public Expression applySubstitution( Substitution sigma )
    {
        Expression newNegated = (Expression) negated_.applySubstitution(sigma);
        
        return new Negation(newNegated);
    }
    
    public Expression getNegated()
    {
        return negated_;
    }
    

    @Override
    public boolean hasTermFunction( int functionName )
    {
        return negated_.hasTermFunction(functionName);
    }
    
    @Override
    public boolean hasTermVariable( int varName )
    {
        return negated_.hasTermVariable(varName);
    }

    @Override
    public boolean canMapVariables( Expression other )
    {
        if ( other instanceof Negation == false )
            return false;
        
        return negated_.canMapVariables( ((Negation) other).getNegated() );
    }

    @Override
    public void printToStream( PrintStream target, SymbolTable symtab )
    {
        target.print("(not ");
        negated_.printToStream(target, symtab);
        target.print(")");
    }
    
    @Override
    public Expression uniquefy( Map<TermVariable, TermVariable> varMap )
    {
        return new Negation( (Expression) negated_.uniquefy(varMap) );
    }
        
}
