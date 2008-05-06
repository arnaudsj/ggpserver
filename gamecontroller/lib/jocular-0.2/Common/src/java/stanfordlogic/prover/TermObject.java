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

import java.util.Map;
import java.util.TreeMap;

import stanfordlogic.gdl.SymbolTable;


/**
 *
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class TermObject extends Term
{
    
    public int token_;
    
    private TermObject(int token)
    {
        token_ = token;
    }
    
    // THINK: use a WeakHashMap here?
    private static Map<Integer, TermObject> objMemoMap_ = new TreeMap<Integer, TermObject>();
    
    public static TermObject makeTermObject(int token)
    {
        TermObject obj = objMemoMap_.get(token);
        
        // Did we find it? Then just return it.
        if ( obj != null )
            return obj;
        
        // Else, we need to create it and stick it into the map.
        obj = new TermObject(token);
        objMemoMap_.put(token, obj);
        
        return obj;
    }
    
    @Override
    public Term clone()
    {
        return this; // nothing to do for term objects!
    }

    public int getToken()
    {
        return token_;
    }
    
    @Override
    public int getTotalColumns()
    {
        // Only need one column: object name
        return 1;
    }

    @Override
    protected int compareTo( TermFunction t )
    {
        // Obj < Func < Var
        return -1;
    }
    
    @Override
    protected int compareTo( TermVariable t )
    {
        // Obj < Func < Var
        return -1;
    }

    @Override
    protected int compareTo( TermObject t )
    {
        return Integer.signum( token_ - t.token_ );
    }


    @Override
    public String toString( SymbolTable symtab )
    {
        return symtab.get(token_);
    }

    @Override
    public Term applySubstitution( Substitution sigma )
    {
        // Nothing to do here -- no variables in object constants.
        return this;
    }



    @Override
    public boolean hasVariables()
    {
        // term-objects never have variables
        // (true by definition -- they are object constants)
        return false;
    }


    @Override
    public boolean hasTermFunction( int functionName )
    {
        // false by definition
        return false;
    }

    @Override
    public boolean hasVariable( int varName )
    {
        // false by definition
        return false;
    }

    @Override
    public boolean equals( Object obj )
    {
        // Two objects are equal if and only if they share the same physical memory.
        return this == obj;
    }


    @Override
    public Term uniquefy( Map<TermVariable, TermVariable> newVarMap )
    {
        // Nothing to do, by definition
        return this;
    }
    
    @Override
    public boolean canMapVariables( Term other, Map<TermVariable, TermVariable> varMap )
    {
        return this.equals(other);
    }



    @Override
    public boolean mgu( Term t, Substitution subsSoFar )
    {
        if ( t instanceof TermObject )
            return equals(t);
        else if ( t instanceof TermVariable )
        {
            // Reverse the problem; the TermVariable class handles this 
            return ((TermVariable) t).mgu(this, subsSoFar);
        }
        else if ( t instanceof TermFunction )
        {
            // Can't unify a function and a constant.
            return false;
        }
        else
        {
            throw new IllegalArgumentException(
                    "TermObject.mgu: Don't know how to handle term of type "
                            + t.getClass().getName() );
        }
    }

    
}
