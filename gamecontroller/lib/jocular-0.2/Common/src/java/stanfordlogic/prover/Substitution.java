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
import java.util.Set;
import java.util.TreeMap;

import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.game.GameManager;


/**
 *
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class Substitution
{
    final private Map<TermVariable, Term> substitutions_;
    
    public Substitution()
    {
        substitutions_ = new TreeMap<TermVariable, Term>();
    }
    
    public Substitution(Map<TermVariable, Term> substs)
    {
        substitutions_ = substs;
    }
    
    public void addMapping(TermVariable from, Term to)
    {
        // Is there already a mapping for 'to'?
        Term mapping = substitutions_.get(to);
        
        if ( mapping != null )
            substitutions_.put(from, mapping);
        else
            substitutions_.put(from, to.clone());
        
        // Fix everything that maps to 'from'.
        updateMappings(from, to);
    }
    
    private void updateMappings(TermVariable from, Term to)
    {
        for ( TermVariable key : substitutions_.keySet() )
        {
            Term mapping = getMapping(key);
            
            if ( mapping.equals(from) )
                substitutions_.put(key, to);
            else if ( mapping instanceof TermFunction )
            {
                updateFunction( (TermFunction) mapping, from, to );
                ((TermFunction) mapping).updateHasVariables();
            }
        }
    }
    
    private void updateFunction(TermFunction f, TermVariable from, Term to)
    {
        for ( int i = 0; i < f.getArity(); i++ )
        {
            Term arg = f.getTerm(i);
            
            if ( arg.equals(from) )
                f.arguments_[i] = to;
            else if ( arg instanceof TermFunction )
                updateFunction( (TermFunction) arg, from, to );
        }
    }

    public Expression apply(Expression exp)
    {
        // If we don't have any mappings, don't bother with substitution
        if ( substitutions_.size() == 0 )
            return exp;
        
        return exp.applySubstitution(this);
    }
    
    public Expression [] apply(Expression [] exprs)
    {
        // If we don't have any mappings, don't bother with substitution
        if ( substitutions_.size() == 0 )
            return exprs;
        
        Expression [] result = new Expression[exprs.length];
        
        int i = 0;
        for ( Expression s : exprs )
            result[i++] = apply(s);
        
        return result;
    }
    
    public Term getMapping(TermVariable var)
    {
        return substitutions_.get(var);
    }
    
    public void removeMapping(TermVariable var)
    {
        substitutions_.remove(var);
    }
    
    public Substitution copy()
    {
        Substitution result = new Substitution();
        
        for ( TermVariable key : substitutions_.keySet() )
            result.substitutions_.put(key, substitutions_.get(key).clone());
        
        return result;
    }
    
    public Substitution copy(Substitution add)
    {
        Substitution result = this.copy();
        
        for ( TermVariable key : add.substitutions_.keySet() )
            result.addMapping(key, add.substitutions_.get(key));
        
        return result;
    }
    
    public Set<TermVariable> getMappedVars()
    {
        return substitutions_.keySet();
    }
    
    /**
     * Gets the mapping for a function.
     * Iterates over all the Term of the functions and recursively tries to map them.
     * 
     * @param func The function whose mappings to compute.
     * @return The function after all substitutions have been made.
     */
    public TermFunction getMapping(TermFunction func)
    {
        TermFunction result = new TermFunction(true, func.functionName_, func.arguments_);
        for ( int i = 0; i < result.getArity(); i++ )
        {
            if(result.arguments_[i] instanceof TermVariable)
            {
                Term replacement = getMapping((TermVariable) result.arguments_[i]);
                if(replacement != null)
                    result.arguments_[i] = replacement;
            }
            else  if(result.arguments_[i] instanceof TermFunction)
            {
                Term replacement = getMapping((TermFunction) result.arguments_[i]);
                if(replacement != null)
                    result.arguments_[i] = replacement;
            }
        }
        return result;
    }
    
    public int numMappings()
    {
        return substitutions_.size();
    }
    
    @Override
    public String toString()
    {
        return toString(GameManager.getSymbolTable());
    }
    
    public String toString(SymbolTable symtab)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        
        Set<TermVariable> keys = substitutions_.keySet();
        
        for ( TermVariable tv : keys )
        {
            sb.append( tv.toString(symtab) );
            sb.append( " <- " );
            sb.append( getMapping(tv).toString(symtab) );
            sb.append(". ");
        }
        
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        
        if ( obj instanceof Substitution == false )
            return false;
        
        Substitution sub = (Substitution) obj;
        
        return substitutions_.equals(sub.substitutions_);
    }
    
    
}
