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
import java.util.Arrays;
import java.util.Map;

import stanfordlogic.gdl.SymbolTable;


/**
 * A conjunction of sentences. To be provable, all conjuncts must be provable.
 */
public class Conjunction extends Expression
{
    final private Expression [] sentences_;
    
    public Conjunction()
    {
        this(false, (Expression[]) null);
    }
    
    public Conjunction(Expression [] sentences)
    {
        this(true, sentences);
    }
    
    public Conjunction(boolean clone, Expression ... sentences)
    {
        if ( sentences == null )
            sentences_ = EMPTY_SENTENCES;
        else
        {
            if ( clone )
                sentences_ = sentences.clone();
            else
                sentences_ = sentences;
        }
    }
    
    public int numConjuncts()
    {
        return sentences_.length;
    }
    
    public Expression getConjunct(int whichOne)
    {
        return sentences_[whichOne];
    }
    
    public Expression [] getConjuncts()
    {
        return sentences_;
    }
    
    
    @Override
    public boolean hasTermFunction( int functionName )
    {
        for ( Expression s : sentences_ )
        {
            if ( s.hasTermFunction(functionName) )
                return true;
        }
        
        return false;
    }

    @Override
    public boolean hasTermVariable( int varName )
    {
        for ( Expression s: sentences_ )
            if ( s.hasTermVariable(varName) )
                return true;
        
        return false;
    }
    
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        
        if ( obj instanceof Conjunction == false )
            return false;
        
        return Arrays.equals( sentences_, ((Conjunction) obj).sentences_ );
    }

    @Override
    public boolean canMapVariables( Expression other )
    {
        if ( other instanceof Conjunction == false )
            return false;
        
        Conjunction sl = (Conjunction) other;
        
        if ( numConjuncts() != sl.numConjuncts() )
            return false;
        
        for ( int i = 0; i < numConjuncts(); i++ )
        {
            if ( getConjunct(i).canMapVariables(sl.getConjunct(i)) == false )
                return false;
        }
        
        return true;
    }

    @Override
    public Conjunction applySubstitution( Substitution sigma )
    {
        Expression [] newSentences = new Expression [sentences_.length];
        
        for ( int i = 0; i < sentences_.length; i++ )
            newSentences[i] = (Expression) sentences_[i].applySubstitution(sigma);
        
        // TODO: remove duplicates if any were created during application of substitution
        
        return new Conjunction(false, newSentences);
    }

    @Override
    public void printToStream( PrintStream target, SymbolTable symtab )
    {
        if ( sentences_.length == 0 )
            return;
        
        target.print("Rule body: ");
        int i;
        for ( i = 0; i < sentences_.length - 1; i++ )
        {
            sentences_[i].printToStream(target, symtab);
            target.print(" & ");
        }
        sentences_[i].printToStream(target, symtab);
    }
    
    @Override
    public Conjunction uniquefy( Map<TermVariable, TermVariable> varMap )
    {
        Expression [] newSentences = new Expression [sentences_.length];

        for ( int i = 0; i < sentences_.length; i++ )
            newSentences[i] = (Expression) sentences_[i].uniquefy(varMap);

        return new Conjunction(false, newSentences);
    }

}
