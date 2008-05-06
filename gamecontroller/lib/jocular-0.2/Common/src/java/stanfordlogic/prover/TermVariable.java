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

import stanfordlogic.gdl.SymbolTable;


/**
 *
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class TermVariable extends Term
{
    private int varName_;
    
    public TermVariable(int varName)
    {
        varName_ = varName;
    }
    
    private static int nextUnique_ = 290;
    
    public static void setUniqueStart(int start)
    {
        nextUnique_ = start;
    }

    /**
     * Make a new unique variable.
     * 
     * @return
     */
    public static TermVariable makeTermVariable()
    {
        TermVariable obj = new TermVariable(nextUnique_++);
        //THINK: make nextUnique 'rotate', no need to have it too big
        return obj;
    }

    @Override
    public Term applySubstitution( Substitution sigma )
    {
        // Does sigma apply to our variable?
        Term replacement = sigma.getMapping(this);
        
        if ( replacement == null )
            return this; // nothing to change!

        // Otherwise, return the variable's replacement
        return replacement;
    }
    
    @Override
    public int getTotalColumns()
    {
        // Only need one column: variable name
        return 1;
    }
    
    @Override
    public Term clone()
    {
        return new TermVariable(this.varName_);
    }

    @Override
    public String toString( SymbolTable symtab )
    {
        return "?var" + varName_;
    }

    @Override
    protected int compareTo( TermObject t )
    {
        // Obj < Func < Var
        return 1;
    }

    @Override
    protected int compareTo( TermFunction t )
    {
        // Obj < Func < Var
        return 1;
    }

    @Override
    protected int compareTo( TermVariable t )
    {
        return Integer.signum( this.varName_ - t.varName_ );
    }

    @Override
    public boolean hasVariables()
    {
        return true;
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
        return varName_ == varName;
    }

    public int getName()
    {
        return varName_;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof TermVariable == false )
            return false;
        
        // Two variables are equal if and only if they have the same (name in this context)
        return varName_ == ((TermVariable) obj).varName_;
    }

    @Override
    public int hashCode()
    {
        return varName_;
    }
    
    @Override
    public boolean canMapVariables( Term other, Map<TermVariable, TermVariable> varMap )
    {
        if ( other instanceof TermVariable == false )
            return false;
        
        // Both are variables, so either the first has mapped to no variable,
        // or first must map to second
        TermVariable mapped = varMap.get(this);
        
        if ( mapped == null )
        {
            varMap.put(this, (TermVariable) other);
            return true;
        }
        else
        {
            return mapped.equals(other);
        }
    }

    @Override
    public Term uniquefy( Map<TermVariable, TermVariable> newVarMap )
    {
        TermVariable newVar = newVarMap.get(this);
        if ( newVar == null )
        {
            newVar = makeTermVariable();
            newVarMap.put(this, newVar);
        }
        
        return newVar;
    }

    @Override
    public boolean mgu( Term t, Substitution subsSoFar )
    {
        if ( t instanceof TermObject )
        {
            Term replacement = subsSoFar.getMapping( this );
            
            if ( replacement != null )
            {
                // If there's already a replacement, it has to be equal to the term object
                if ( replacement.equals(t) == false )
                    return false;

                // The replacement equals this, so we're ok
                else
                    return true;
            }
            
            // There was no replacement:
            else
            {
                // Add a mapping for the variable to this term-object
                subsSoFar.addMapping( this, t);
                return true;
            }
        }
        else if ( t instanceof TermVariable )
        {
            TermVariable it = (TermVariable) t;
            
            Term myReplacement = subsSoFar.getMapping(this);
            Term itsReplacement = subsSoFar.getMapping(it);
            
            if ( itsReplacement == null )
            {
                // just map 'it' to me (or my replacement)
                
                if ( myReplacement == null )
                {
                    if(!equals(it))
                        subsSoFar.addMapping(it, this);
                }
                else
                {
                    if(!(myReplacement instanceof TermVariable) || !((TermVariable) myReplacement).equals(it))
                        subsSoFar.addMapping(it, myReplacement);
                }
                
                return true;
            }
            
            // At this point, 'it' has a replacement.
            
            if ( myReplacement == null )
            {
                // I don't have a replacement, so map me to it, or to its replacement
                
                if ( itsReplacement == null )
                {
                    subsSoFar.addMapping(this, it);
                }
                else
                {
                    if(!(itsReplacement instanceof TermVariable) || !((TermVariable) itsReplacement).equals(this))
                        subsSoFar.addMapping(this, itsReplacement);
                }
                
                return true;
            }
            
            // At this point, both term variables have replacements.
            // So make sure that they are the same!
            
            if ( myReplacement.equals(itsReplacement) )
                return true;
            else
                return false;   
        }
        else if ( t instanceof TermFunction )
        {
            Term myReplacement = subsSoFar.getMapping(this);

            // Case 1: I have a replacement
            if ( myReplacement != null )
            {
                // See if my replacement can be unified with the function
                return myReplacement.mgu(t, subsSoFar);
            }
            
            // Case 2: I have no replacement
            else
            {
                TermFunction itsReplacement = subsSoFar.getMapping((TermFunction) t);

                if(itsReplacement.hasVariable(this))
                {
                    return false;
                }
                
                // just set my replacement to the function
                subsSoFar.addMapping(this, itsReplacement);
                return true;
            }
        }
        else
        {
            throw new IllegalArgumentException(
                    "TermVariable.mgu: Don't know how to handle term of type "
                            + t.getClass().getName() );
        }
    }
    
}
