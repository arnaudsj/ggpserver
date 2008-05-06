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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.GdlVariable;
import stanfordlogic.gdl.SymbolTable;


/**
 * The variable fact is a fact that contains one or more variables.
 * 
 * <p>Like the fact, it has columns for each element of the fact, but
 * it also has a parallel array marking whether or not each column
 * is a variable.
 */
public class VariableFact extends Fact
{
    private Set<TermVariable> variables_;
    
    public VariableFact(int relName, Term ... columns )
    {
        this(true, relName, columns);
    }
    
    public VariableFact(boolean cloneCols, int relName, Term ... columns)
    {
        super(relName);
        
        if ( columns == null )
            terms_ = EMPTY_TERMS;
        else
        {
            if ( cloneCols )
                terms_ = columns.clone();
            else
                terms_ = columns;
        }
        
        variables_ = new TreeSet<TermVariable>();
        buildVariableSet();
    }
    
    private void buildVariableSet()
    {
        addVarsFromTerms(terms_);
    }
    
    private void addVarsFromTerms(Term [] terms)
    {
        for ( Term t : terms )
        {
            if ( t instanceof TermVariable )
                variables_.add( (TermVariable) t);
            else if ( t instanceof TermFunction )
                addVarsFromTerms( ((TermFunction) t).arguments_ );
        }
    }
    
    public Set<TermVariable> getVariables()
    {
        return variables_;
    }

    @Override
    public Fact applySubstitution( Substitution sigma )
    {
        Term [] columns = new Term [terms_.length];
        
        
        boolean vars = false;
        for ( int i = 0; i < terms_.length; i++ )
        {
            columns[i] = terms_[i].applySubstitution(sigma);
            if ( columns[i].hasVariables() )
            {
                vars = true;
            }
        }
        
        if ( vars )
            return new VariableFact(false, relationName_, columns);
        else
            return new GroundFact(false, relationName_, columns);
    }

    /* (non-Javadoc)
     * @see camembert.knowledge.Fact#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        
        if ( obj instanceof VariableFact )
        {
            VariableFact f = (VariableFact) obj;
            
            return relationName_ == f.relationName_
                    && Arrays.equals( terms_,  f.terms_ );
        }
        
        // Not equals.
        return false;
    }

    
    @Override
    public boolean hasOnlyTermObjects()
    {
        // By definition, variable facts have things other than objects: variables!
        return false;
    }
    
    
    @Override
    public boolean canMapVariables( Expression other )
    {
        if ( other instanceof VariableFact == false )
            return false;
        
        VariableFact vf = (VariableFact) other;
        
        if ( relationName_ != vf.relationName_ || getArity() != vf.getArity() )
            return false;
        
        Map<TermVariable, TermVariable> varMappings = new HashMap<TermVariable, TermVariable>();
        
        for ( int i = 0; i < getArity(); i++ )
        {
            if ( getTerm(i).canMapVariables(vf.getTerm(i), varMappings) == false )
                return false;
        }
        
        return true;
    }

    @Override
    public void printToStream( PrintStream target, SymbolTable symtab )
    {
        target.print('(');
        target.print( symtab.get( relationName_ ) );
        
        if ( terms_.length > 0 )
        {
            target.print( ' ' );

            // Print all but last column
            int i;
            for ( i = 0; i < terms_.length - 1; i++ )
            {
                target.print( terms_[i].toString( symtab ) );
                target.print( ' ' );
            }

            // Print the last column
            target.print( terms_[i].toString( symtab ) );
        }
        
        target.print(')');
    }
    
    public static Fact fromExpression(GdlExpression exp)
    {
        if ( exp instanceof GdlAtom )
            return GroundFact.fromExpression(exp);
        else if ( exp instanceof GdlList )
            return fromList((GdlList) exp);
        
        // unknown expression type
        throw new IllegalArgumentException(
                "GroundFact.fromExpression: don't know how to handle expressions of type "
                        + exp.getClass().getName() );
    }
    
    
    /**
     * Construct a variable fact from a GdlList. Note that the list <i>must</i>
     * be a list of atoms, in other words, there cannot be any nested lists. The
     * fact is constructed by taking the first element of the list as the fact's
     * relation name, and every subsequent element as a column. If an atom is
     * found to be a variable, then that column is marked as a variable.
     * 
     * @param list
     *            The list to build the fact from.
     * 
     * @throws IllegalAccessException
     *             when the passed list is not a list of atoms.
     * 
     * @see cs227b.paulatim.gdl.GdlList
     * 
     * @return A variable fact representing the data from the list.
     * 
     */
    public static Fact fromList(GdlList list)
    {
        int relName = ((GdlAtom) list.getElement(0)).getToken();
        
        Term [] terms = new Term[list.getArity()];
        
        // Turn each element of the list into a term.
        // Make sure to turn same variables into the same term.
        
        boolean vars = false;
        
        Map<GdlVariable, TermVariable> varMap = new HashMap<GdlVariable, TermVariable>();
        
        for ( int i = 0; i < list.getArity(); i++ )
        {
            GdlExpression exp = list.getElement(i+1);
            
            if ( (exp instanceof GdlVariable) == false )
            {
                terms[i] = Term.buildFromGdl( exp, varMap );
                
                // Check to see if this term has variables in it.
                // (But don't bother if we already know that we have variables.)
                if ( !vars && terms[i].hasVariables() )
                    vars = true;
            }
            else
            {
                GdlVariable var = (GdlVariable) exp;
                terms[i] = new TermVariable(var.getToken());
                vars = true;
            }
            
        }
        
        // Only return a variable fact if this actually has variables 
        if ( vars )
            return new VariableFact(relName, terms);
        else
            return new GroundFact(relName, terms);
    }
    
    /**
     * Old version of fromList. Does uniquefication, which is bad.
     * 
     * @param list
     * @return
     */
    public static VariableFact oldFromList(GdlList list)
    {
        int relName = ((GdlAtom) list.getElement(0)).getToken();
        
        Term [] terms = new Term[list.getArity()];
        
        // Turn each element of the list into a term.
        // Make sure to turn same variables into the same term.
        
        Map<GdlVariable, TermVariable> varMap = new HashMap<GdlVariable, TermVariable>();
        
        for ( int i = 0; i < list.getArity(); i++ )
        {
            GdlExpression exp = list.getElement(i+1);
            
            if ( (exp instanceof GdlVariable) == false )
                terms[i] = Term.buildFromGdl( exp, varMap );
            else
            {
                GdlVariable var = (GdlVariable) exp;
                
                TermVariable termVar = varMap.get(var);
                
                if ( termVar == null )
                {
                    termVar = TermVariable.makeTermVariable();
                    varMap.put(var, termVar);
                }
                
                terms[i] = termVar;
            }
        }
        
        return new VariableFact(relName, terms);
    }
    
    @Override
    public VariableFact uniquefy(Map<TermVariable, TermVariable> newVarMap)
    {
        Term [] newTerms = new Term [terms_.length];

        for ( int i = 0; i < terms_.length; i++ )
            newTerms[i] = terms_[i].uniquefy(newVarMap);

        return new VariableFact(false, relationName_, newTerms);
    }
    
}
