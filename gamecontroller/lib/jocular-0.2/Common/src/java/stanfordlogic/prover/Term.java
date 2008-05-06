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

import java.util.HashMap;
import java.util.Map;

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.GdlVariable;
import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.game.GameManager;


/**
 *
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public abstract class Term implements Comparable<Term>
{
    @Override
    public final String toString()
    {
        return toString(GameManager.getSymbolTable());
    }
    public abstract String toString(SymbolTable symtab);
    
    public int compareTo( Term t )
    {
        if ( t instanceof TermObject )
            return this.compareTo((TermObject) t);
        else if ( t instanceof TermFunction )
            return this.compareTo((TermFunction) t);
        else
            return this.compareTo((TermVariable) t);
    }
    
    @Override
    public abstract Term clone();
    
    public abstract Term applySubstitution( Substitution sigma );
    
    /**
     * Get the total number of columns needed to represent this term.
     * Objects and variables only need one column: the name of the term. Term functions
     * need one column for their name, and then the sum of the needed columns for each
     * of their arguments.
     * 
     * @return The total number of columns needed to represent this term.
     */
    public abstract int getTotalColumns();
    
    
    protected abstract int compareTo(TermObject t);
    protected abstract int compareTo(TermFunction t);
    protected abstract int compareTo(TermVariable t);
    
    public abstract boolean hasVariables();
    
    public abstract boolean hasTermFunction(int functionName);
    public abstract boolean hasVariable(int varName);
    
    public abstract boolean canMapVariables(Term other, Map<TermVariable,TermVariable> varMap);
    
    public abstract Term uniquefy(Map<TermVariable,TermVariable> newVarMap);
    
    public abstract boolean mgu(Term t, Substitution subsSoFar);
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static Term buildFromGdl(GdlExpression expression)
    {
        return buildFromGdl(expression, new HashMap<GdlVariable, TermVariable>() );
    }
    
    public static Term buildFromGdl(GdlExpression expression, Map<GdlVariable, TermVariable> varMap)
    {
        if ( expression instanceof GdlAtom )
        {
            return TermObject.makeTermObject( ((GdlAtom) expression).getToken() );
        }
        else if ( expression instanceof GdlList )
        {
            GdlList list = (GdlList) expression;
            
            // Grab the function name
            int name = ((GdlAtom) list.getElement(0)).getToken();
            
            // Convert each term
            Term [] terms = new Term [list.getArity()];
            
            for ( int i = 0; i < list.getArity(); i++ )
            {
                GdlExpression elem = list.getElement(i+1);
                
                if ( elem instanceof GdlVariable == false)
                    terms[i] = Term.buildFromGdl(elem, varMap);
                else
                {
                    /*TermVariable termVar = varMap.get(elem);
                    
                    if ( termVar == null )
                    {
                        termVar = TermVariable.makeTermVariable();
                        varMap.put( (GdlVariable) elem, termVar);
                    }*/
                    TermVariable termVar = new TermVariable(((GdlVariable) elem).getToken());
                    
                    terms[i] = termVar;
                }
            }
            
            return new TermFunction(name, terms);
        }
        else
        {
            throw new IllegalArgumentException(
                    "Term.buildFromGdl: cannot handle GDL of type "
                    + expression.getClass().getName() );
        }
    }
}
