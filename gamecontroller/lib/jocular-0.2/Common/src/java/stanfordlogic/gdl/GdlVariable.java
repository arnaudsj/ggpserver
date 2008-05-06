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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class GdlVariable extends GdlExpression
{
    final private int token_;
    
	private GdlVariable(SymbolTable symTab, int token)
	{
		super(symTab);
        token_ = token;
	}
    
    static private Map<Integer, GdlVariable> varMap = new HashMap<Integer, GdlVariable>();
    
    static public GdlVariable getGdlVariable(SymbolTable symTab, int token)
    {
        // if it's in the map already, return that.
        GdlVariable var = varMap.get(token);
        
        if ( var != null )
            return var;
        
        // Else, create it and add it to the map.
        var = new GdlVariable(symTab, token);
        varMap.put(token, var);
        return var;
    }

	@Override
	public String toString()
	{
		return "?" + symbolTable_.get(token_);
	}
    
    public int getToken()
    {
        return token_;
    }

    @Override
    public boolean equals( Object obj )
    {
        // GdlVariables are equal if and only if they share the same pointer.
        if ( this == obj )
            return true;
        
        return false;
        
        /*if ( (obj instanceof GdlVariable) == false )
            return false;
        
        GdlVariable v = (GdlVariable) obj;
        
        return this.getToken() == v.getToken();*/
    }

    @Override
    public int hashCode()
    {
        return getToken();
    }
    
    
	
}
