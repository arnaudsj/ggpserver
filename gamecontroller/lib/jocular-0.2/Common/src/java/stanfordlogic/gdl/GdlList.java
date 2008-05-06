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
package stanfordlogic.gdl;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public final class GdlList extends GdlExpression implements Iterable<GdlExpression>
{
	/** The elements of the list. */
	final private GdlExpression [] elements_;
    
    /** Are all the elements atoms? */
    final private boolean atomList_;
	
	public GdlList(SymbolTable symtab, GdlExpression [] elements)
	{
		super(symtab);
		elements_ = elements.clone();
        
        // See if this is a list of atoms, or if it has
        // other kinds of elements in it.
        boolean atomList = true;
        for ( GdlExpression exp: elements )
        {
            if ( (exp instanceof GdlAtom) == false )
            {
                atomList = false;
                break;
            }
        }
        atomList_ = atomList;
	}

    public static GdlList buildFromWords(SymbolTable symbolTable, String ... args)
    {
        GdlExpression[] atoms = new GdlExpression[args.length];
        for(int i=0; i<args.length; i++)
        {
            String word = args[i];
            
            if(word.startsWith("?"))
            {
                word = word.substring(1);
                int token = symbolTable.get(word);
                atoms[i] = GdlVariable.getGdlVariable(symbolTable, token);
            }
            else
            {
                int token = symbolTable.get(word);
                atoms[i] = new GdlAtom(symbolTable, token);
            }
        }
        return new GdlList(symbolTable, atoms);
    }
    
    /**
     * Check if the list is all atoms.
     * 
     * @return True if all elements of the list are GdlAtoms.
     */
    public boolean isAtomList()
    {
        return atomList_;
    }
	
	public int getSize()
	{
		return elements_.length;
	}
    
    /**
     * Get the arity of the list. Arity is defined as the size minus one. So a
     * list of the form (foo bar baz) has arity 2, and a list of the form
     * (foobar) has arity 0.
     * 
     * @return The arity of the list.
     */
    public int getArity()
    {
        return getSize() - 1;
    }
	
	public GdlExpression getElement(int elem)
	{
		return elements_[elem];
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		
		for ( GdlExpression exp : elements_ )
		{
			sb.append(exp.toString());
			sb.append(" ");
		}
		
		// Remove the last space
		sb.deleteCharAt( sb.length()-1 );
		
		sb.append(")");
		return sb.toString();
	}

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        
        if ( obj instanceof GdlList )
        {
            GdlList list = (GdlList) obj;
            
            return Arrays.equals(this.elements_, list.elements_);
        }
        
        return false;
    }
    
    
    public class GdlListIterator implements Iterator<GdlExpression>
    {
        int cursor_ = 0;
        
        public boolean hasNext()
        {
            return cursor_ < elements_.length;
        }

        public GdlExpression next()
        {
            return elements_[cursor_++];
        }

        public void remove()
        {
            // nope, can't do
            throw new UnsupportedOperationException("GdlListIterator can't remove elements");
        }
        
    }
    
    public Iterator<GdlExpression> iterator()
    {
        return new GdlListIterator();
    }
}
