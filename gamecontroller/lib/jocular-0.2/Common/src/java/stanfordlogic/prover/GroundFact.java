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

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.SymbolTable;


/**
 *
 */
public class GroundFact extends Fact implements Comparable<GroundFact>
{
    final private boolean onlyTermObjects_;
    
    public GroundFact(int relationName)
    {
        this(false, relationName, (Term[]) null);
    }
    
    /**
     * Construct a fact of relation <tt>relationName</tt> with columns <tt>cols</tt> of
     * object constants.
     * 
     * @param relationName The name of the relation.
     * @param cols The columns of this fact, all object constants.
     */
    public GroundFact(int relationName, int ... cols)
    {
        super(relationName);
        
        if ( cols == null )
            terms_ = EMPTY_TERMS;
        else
        {
            terms_ = new Term [cols.length];

            for ( int i = 0; i < cols.length; i++ )
                terms_[i] = TermObject.makeTermObject(cols[i]);
        }
        
        onlyTermObjects_ = true;
    }
    
    public GroundFact(int relationName, Term ... cols)
    {
        this(true, relationName, cols);
    }
    
    public GroundFact(boolean clone, int relationName, Term ... cols)
    {
        super(relationName);
        
        boolean onlyTermObjects = true; 
        
        if ( cols == null )
            terms_ = EMPTY_TERMS;
        else
        {
            if ( clone )
                terms_ = cols.clone();
            else
                terms_ = cols;
            
            // Assert that none of the columns have variables.
            for ( Term t : cols )
            {
                if ( t.hasVariables() )
                    throw new IllegalArgumentException("GroundFact cannot be constructed with variables!");
                
                if ( t instanceof TermObject == false )
                    onlyTermObjects = false;
            }
        }
        
        onlyTermObjects_ = onlyTermObjects;
    }
    
    /**
     * Construct a fact of relation <tt>relName</tt> with columns
     * <tt>cols</tt>. Takes strings as arguments, and uses symbol table
     * <tt>symTab</tt> to convert them to integer form. All columns must be
     * object constants.
     * 
     * @param symTab
     *            The symbol table for converting strings to token numbers.
     * @param relName
     *            The name of the relation, to be converted using
     *            <tt>symtab</tt>.
     * @param cols
     *            The columns, to be converted using <tt>symtab</tt>, all
     *            representing object constants.
     */
    public GroundFact(SymbolTable symTab, String relName, String ... cols )
    {
        super( symTab.get(relName) );
        
        if ( cols == null || cols.length == 0 )
            terms_ = EMPTY_TERMS;
        else
        {
            terms_ = new Term [cols.length];
            
            for ( int i = 0; i < cols.length; i++ )
            {
                terms_[i] = TermObject.makeTermObject( symTab.get( cols[i] ) );
            }
        }
        
        onlyTermObjects_ = true;
    }

    /**
     * Construct a fact of arity zero from <tt>atom</tt>. The atom's token is
     * taken to be the relation name, and the fact has zero columns.
     * 
     * @param atom The atom to build the fact from.
     */
    public GroundFact(GdlAtom atom)
    {
        this( atom.getToken() );
    }
    
    /**
     * Clone this ground fact, but with a new relation name. Keeps all columns
     * intact.
     * 
     * @param newRelName The new name for the ground fact relation.
     * @return The cloned fact with the different name.
     */
    public GroundFact clone(int newRelName)
    {
        // False means don't clone the terms.
        return new GroundFact(false, newRelName, this.terms_);
    }
    
    @Override
    public GroundFact applySubstitution( Substitution sigma )
    {
        // Ground facts do not have variables, so there is nothing to do.
        return this;
    }
    
    @Override
    public boolean hasOnlyTermObjects()
    {
        return onlyTermObjects_;
    }
    
    @Override
    public boolean canMapVariables( Expression other )
    {
        if ( other instanceof GroundFact == false )
            return false;
        
        GroundFact gf = (GroundFact) other;
        
        if ( relationName_ != gf.relationName_ || getArity() != gf.getArity() )
            return false;
        
        // For ground facts, no variables, so things must be straight equal.
        for ( int i = 0; i < getArity(); i++ )
        {
            if ( getTerm(i).equals(gf.getTerm(i)) == false )
                return false;
        }
        
        return true;
    }

    /**
     * Construct a fact from a GdlList. Note that the list <i>must</i> be
     * a list of atoms, in other words, there cannot be any nested lists. The fact
     * is constructed by taking the first element of the list as the fact's relation name,
     * and every subsequent element as a column.
     * 
     * @param list The list to build the fact from.
     * 
     * @throws IllegalAccessException when the passed list is not a list of atoms.
     * 
     * @see cs227b.paulatim.gdl.GdlList
     * 
     * @return A ground fact representing the data from the list.
     * 
     */
    public static GroundFact fromList(GdlList list)
    {
        int relName = ((GdlAtom) list.getElement(0)).getToken();
        
        Term [] terms = new Term [list.getArity()];
        
        for (int i = 0; i < list.getArity(); i++ )
            terms[i] = Term.buildFromGdl( list.getElement(i+1)  );
        
        return new GroundFact(relName, terms);
    }
    
    public static GroundFact fromExpression(GdlExpression exp)
    {
        if ( exp instanceof GdlList )
            return fromList( (GdlList) exp);
        else if ( exp instanceof GdlAtom )
            return new GroundFact( (GdlAtom) exp );
        
        // unknown expression type
        throw new IllegalArgumentException(
                "GroundFact.fromExpression: don't know how to handle expressions of type "
                        + exp.getClass().getName() );
    }
    
    @Override
    public void printToStream(PrintStream target, SymbolTable symtab)
    {
        target.print('(');
        target.print( symtab.get( relationName_ ) );
        
        if ( terms_.length > 0 )
        {
	        target.print(' ');
            int i;
            for ( i = 0; i < terms_.length - 1; i++ )
            {
                target.print( terms_[i].toString( symtab ) );
                target.print( ' ' );
            }
            target.print( terms_[i].toString( symtab ) );
        }
        
        target.print(')');
    }
    
    /**
     * Compare this fact to fact <tt>f</tt>. Facts are compared first
     * according to their relation name, second according to the number of
     * columns and finally according to the tokens in the columns.
     * 
     * @param f
     *            The fact to compare against.
     * @return -1 if <tt>this</tt> &lt; <tt>f</tt><br>
     *         0 if <tt>this</tt> == <tt>f</tt><br>
     *         1 if <tt>this</tt> &gt; <tt>f</tt>
     */
    public int compareTo( GroundFact f )
    {
        if ( this == f )
            return 0;
        
        int comp = relationName_ - f.relationName_;
        if ( comp != 0 )
            return (comp > 0) ? 1 : -1;
            
        // relation names are equal at this point
        
        comp = terms_.length - f.terms_.length;
        if ( comp != 0 )
            return (comp > 0) ? 1 : -1;
        
        // column lengths are equal at this point
        
        for ( int i = 0; i < terms_.length; i++ )
        {
            comp = terms_[i].compareTo(f.terms_[i]);
            if ( comp != 0 )
                return comp;
        }
        
        // Both are equal at this point
        return 0;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        
        if ( obj instanceof GroundFact )
        {
            GroundFact f = (GroundFact) obj;
            
            return relationName_ == f.relationName_
                    && Arrays.equals( terms_, f.terms_ );
        }
        
        // Not equal.
        return false;
    }

    @Override
    public GroundFact uniquefy(Map<TermVariable, TermVariable> newVarMap)
    {
        // Nothing to do, by definition.
        return this;
    }

    @Override
    public Substitution unify( Fact f )
    {
        // If this == f, then return a successful unification, using
        // an empty substitution; the facts are the same.
        if ( this.equals(f) )
            return new Substitution();
        
        // General case: use normal unify algorithm.
        return super.unify(f);
    }
    
}
