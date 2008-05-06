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
 * A disjunction of sentences. To be true, at least one disjunct must be true.
 */
public class Disjunction extends Expression
{
    final private Expression [] sentences_;
    
    public Disjunction(Expression [] sentences)
    {
        this(true, sentences);
    }
    
    public Disjunction(boolean clone, Expression [] sentences)
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
    
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        
        if ( obj instanceof Disjunction == false )
            return false;
        
        return Arrays.equals( sentences_, ((Disjunction) obj).sentences_ );
    }
    
    @Override
    public Expression applySubstitution( Substitution sigma )
    {
        Expression [] newSentences = new Expression [sentences_.length];
        
        for ( int i = 0; i < sentences_.length; i++ )
            newSentences[i] = sentences_[i].applySubstitution(sigma);
        
        return new Disjunction(newSentences);
    }

    public Expression getDisjunct(int whichOne)
    {
        return sentences_[whichOne];
    }
    
    public int numDisjuncts()
    {
        return sentences_.length;
    }
    
    public Expression [] getDisjuncts()
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
        for ( Expression s : sentences_ )
            if (s.hasTermVariable(varName))
                return true;
        
        return false;
    }

    @Override
    public boolean canMapVariables( Expression other )
    {
        if ( other instanceof Disjunction == false )
            return false;
        
        Disjunction os = (Disjunction) other;
        
        if ( numDisjuncts() != os.numDisjuncts() )
            return false;
        
        for ( int i = 0; i < numDisjuncts(); i++ )
        {
            if ( getDisjunct(i).canMapVariables(os.getDisjunct(i)) == false )
                return false;
        }
        
        return true;
    }

    @Override
    public void printToStream( PrintStream target, SymbolTable symtab )
    {
        target.print("(or ");
        
        int i;
        for ( i = 0; i < sentences_.length - 1; i++ )
        {
            sentences_[i].printToStream(target, symtab);
            target.print(' ');
        }
        sentences_[i].printToStream(target, symtab);
        
        target.print(')');
    }
    
    @Override
    public Expression uniquefy( Map<TermVariable, TermVariable> varMap )
    {
        Expression [] newSentences = new Expression [sentences_.length];

        for ( int i = 0; i < newSentences.length; i++ )
            newSentences[i] = sentences_[i].uniquefy(varMap);

        return new Disjunction(false, newSentences);

    }
}
