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

import java.util.Arrays;
import java.util.Map;

import stanfordlogic.gdl.SymbolTable;


/**
 *
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class TermFunction extends Term
{
    final public int functionName_;
    protected Term [] arguments_; // these arguments are either variables or objects
    
    final private int totalColumns_;
    
    final private static Term [] EMPTY_ARGS = new Term[0];
    
    private boolean hasVariables_;
    
    public TermFunction(int funcName, Term [] args)
    {
        this(true, funcName, args);
    }
    
    public TermFunction(boolean cloneCols, int funcName, Term [] args)
    {
        functionName_ = funcName;
        
        if ( args == null )
        {
            arguments_ = EMPTY_ARGS;
            hasVariables_ = false;
        }
        else
        {
            // Check if any of the term arguments are variables
            boolean vars = false;
            
            for ( Term t : args )
            {
                if ( t.hasVariables() )
                    vars = true;
            }
            
            hasVariables_ = vars;
            
            if ( cloneCols )
                arguments_ = args.clone();
            else
                arguments_ = args;
        }
        
        // Compute the total columns needed
        int total = 1; // 1 for the name
        for ( Term t: arguments_ )
            total += t.getTotalColumns();
        totalColumns_ = total;
    }
    
    public int getArity()
    {
        return arguments_.length;
    }
    
    public int getName()
    {
        return functionName_;
    }
    
    public Term getTerm(int whichOne)
    {
        return arguments_[whichOne];
    }
    
    @Override
    public int getTotalColumns()
    {
        // Only need one column: object name
        return totalColumns_;
    }
    
    protected void updateHasVariables()
    {
        boolean vars = false;
        
        for ( Term t : arguments_ )
        {
            if ( t.hasVariables() )
                vars = true;
        }
        
        hasVariables_ = vars;
    }
    
    @Override
    public Term clone()
    {
        Term [] args = new Term[arguments_.length];
        
        for ( int i = 0; i < arguments_.length; i++ )
            args[i] = arguments_[i].clone();
        
        return new TermFunction(false, functionName_, args);
    }

    @Override
    public Term applySubstitution( Substitution sigma )
    {
        Term [] args = new Term [arguments_.length];
        
        for ( int i = 0; i < arguments_.length; i++ )
            args[i] = arguments_[i].applySubstitution(sigma);
        
        return new TermFunction(false, functionName_, args);
    }

    @Override
    protected int compareTo( TermFunction t )
    {
        int comp = functionName_ - t.functionName_;
        
        if ( comp != 0 )
            return Integer.signum( comp );
        
        // At this point, function names are equal
        
        comp = arguments_.length - t.arguments_.length;
        
        if ( comp != 0 )
            return Integer.signum( comp );
        
        // At this point, both term-functions have same number of arguments
        
        for ( int i = 0; i < arguments_.length; i++ )
        {
            comp = arguments_[i].compareTo( t.arguments_[i] );
            
            if ( comp != 0 )
                return comp;
        }
        
        // At this point, the term-functions are equal.
        
        return 0;
    }
    
    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof TermFunction == false )
            return false;
        
        TermFunction func = (TermFunction) obj;
        
        return functionName_ == func.functionName_
                && Arrays.equals( arguments_, func.arguments_ );
    }

    @Override
    protected int compareTo( TermObject t )
    {
        // Obj < Func < Var
        return 1;
    }
    
    @Override
    protected int compareTo( TermVariable t )
    {
        // Obj < Func < Var
        return -1;
    }
    
    @Override
    public boolean canMapVariables( Term other, Map<TermVariable, TermVariable> varMap )
    {
        if ( other instanceof TermFunction == false )
            return false;
        
        TermFunction tf = (TermFunction) other;
        
        if ( getName() != tf.getName() || getArity() != tf.getArity() )
            return false;
        
        for ( int i = 0; i < getArity(); i++ )
        {
            if ( getTerm(i).canMapVariables( tf.getTerm(i), varMap ) == false )
                return false;
        }
        
        return true;
    }

    @Override
    public String toString( SymbolTable symtab )
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append('(');
        sb.append(symtab.get(functionName_));

        if ( arguments_.length > 0 )
            sb.append( ' ' );
        
        // Print all but the last argument
        int i;
        for ( i = 0; i < arguments_.length-1; i++ )
        {
            sb.append( arguments_[i].toString(symtab) );
            sb.append( ' ' );
        }
        
        // Print the last argument
        sb.append( arguments_[i].toString(symtab) );
        
        sb.append(')');
        
        return sb.toString();
    }

    @Override
    public boolean hasVariables()
    {
        return hasVariables_;
    }

    @Override
    public boolean hasTermFunction( int functionName )
    {
        if ( functionName_ == functionName )
            return true;
        
        for ( Term t : arguments_ )
            if ( t.hasTermFunction(functionName) )
                return true;
        
        return false;
    }

    @Override
    public boolean hasVariable( int varName )
    {
        for (Term t: arguments_) {
            if (t.hasVariable(varName)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public Term uniquefy( Map<TermVariable, TermVariable> newVarMap )
    {
        Term [] newArgs = new Term [arguments_.length];

        for ( int i = 0; i < arguments_.length; i++ )
            newArgs[i] = arguments_[i].uniquefy(newVarMap);

        return new TermFunction(false, functionName_, newArgs);
    }

    @Override
    public boolean mgu( Term t, Substitution subsSoFar )
    {
        if ( t instanceof TermObject )
        {
            // Cannot map functions to constants.
            return false;
        }
        else if ( t instanceof TermVariable )
        {
            // Reverse the problem; the TermVariable class handles this 
            return ((TermVariable) t).mgu(this, subsSoFar);
        }
        else if ( t instanceof TermFunction )
        {
            TermFunction f = (TermFunction) t;
            
            // Make sure that our function names are equal
            if ( functionName_ != f.functionName_ )
                return false;
            
            // Make sure arities are the same
            if ( getArity() != f.getArity() )
                return false;
            
            // Finally, make sure we can get the mgu of all arguments
            for ( int i = 0; i < getArity(); i++ )
            {
                if ( arguments_[i].mgu( f.arguments_[i], subsSoFar) == false )
                    return false;
            }
            
            // All good!
            return true;
        }
        else
        {
            throw new IllegalArgumentException(
                    "TermFunction.mgu: Don't know how to handle term of type "
                            + t.getClass().getName() );
        }
    }

    /**
     * Checks whether the function contains references to the input variable.
     * Used for unification of a variable and a function ('occurs' check).
     * 
     * @param variable The variable whose presence to check for.
     * @return True if <tt>variable</tt> appears anywhere within the function's arguments.
     */
    public boolean hasVariable(TermVariable variable)
    {
        for ( int i = 0; i < getArity(); i++ )
        {
            if(arguments_[i] instanceof TermVariable)
            {
                if(((TermVariable) arguments_[i]).equals(variable))
                    return true;
            }
            else if(arguments_[i] instanceof TermFunction)
            {
                if(((TermFunction) arguments_[i]).hasVariable(variable))
                    return true;
            }
        }
        return false;
    }
}
