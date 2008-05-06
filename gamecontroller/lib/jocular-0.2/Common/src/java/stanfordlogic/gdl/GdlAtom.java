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

/**
 * An atom in GDL.
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class GdlAtom extends GdlExpression
{
	final int token_;
	
	public GdlAtom(SymbolTable symTab, int token)
	{
		super(symTab);
		
		token_ = token;
	}
    
    /**
     * Get the token number of this atom. Using the symbol table, or the
     * toString() method, you can translate this to a string.
     * 
     * @return The token number of this atom.
     */
    public int getToken()
    {
        return token_;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return symbolTable_.get(token_);
	}

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        
        // If both are GdlAtoms, check to see if tokens are equal
        if ( obj.getClass() == GdlAtom.class )
        {
            GdlAtom rhs = (GdlAtom) obj;
            
            return rhs.token_ == token_;
        }
        
        // If obj is a string, check to see if its translation is
        // equal to our token (case insensitive)
        else if ( obj instanceof String )
        {
            String str = ((String) obj).toLowerCase();
            
            return str.equals( symbolTable_.get(token_) );
        }
        
        // If obj is an integer, check to see if its value is the same
        // as our token value
        else if ( obj instanceof Integer )
        {
            return token_ == ((Integer) obj);
        }
        
        // In all other cases...
        return false;
    }
	
    
	
}
