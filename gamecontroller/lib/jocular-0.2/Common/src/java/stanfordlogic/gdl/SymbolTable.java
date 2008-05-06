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
 * Simple symbol table implementation. Maps identifiers to unique token numbers, and
 * token numbers to identifiers.
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 * 
 */
public class SymbolTable
{
	private Map<String, Integer> idToToken_;
	private Map<Integer, String> tokenToId_;
	
	private int nextTokenNum_;
	
	public SymbolTable()
	{
		idToToken_ = new HashMap<String, Integer>();
		tokenToId_ = new HashMap<Integer, String>();
		nextTokenNum_ = 256;
	}

	/**
	 * Clear the symbol table of all mappings.
	 *
	 */
	public void clear()
	{
		idToToken_.clear();
		tokenToId_.clear();
	}

	/**
	 * Returns true if the symbol table contains key <i>key</i>.
	 * 
	 * @param key The key to check for.
	 * @return True if the table contains a mapping for <i>key</i>.
	 */
	synchronized public boolean containsKey( Object key )
	{
		if ( key instanceof String )
			return idToToken_.containsKey(key);
		else if ( key instanceof Integer )
			return tokenToId_.containsKey(key);
		
		throw new ClassCastException("Symbol table cannot contain keys of type "
				+ key.getClass().getName() );
	}

	/**
	 * Returns true if the symbol table contains value <i>value</i>.
	 * 
	 * @param value The value to check for.
	 * @return True if the table contains the value <i>value</i>.
	 */
	synchronized public boolean containsValue( Object value )
	{
		if ( value instanceof String )
			return tokenToId_.containsValue(value);
		else if ( value instanceof Integer )
			return idToToken_.containsValue(value);
		
		throw new ClassCastException("Symbol table cannot contain values of type "
				+ value.getClass().getName() );
	}

	synchronized public int get(String identifier)
	{
		Integer token = idToToken_.get(identifier);
		
		if ( token == null )
		{
			// Token not found; need to add this symbol
			idToToken_.put(identifier, nextTokenNum_);
			tokenToId_.put(nextTokenNum_, identifier);
			
			token = nextTokenNum_;
			nextTokenNum_++;
		}
		
		return token;
	}
	synchronized public String get(Integer token)
	{
		String identifier = tokenToId_.get(token);
		
		// If not found, just return null.
		
		return identifier;
	}

	synchronized public boolean isEmpty()
	{
		// Don't need to check tokenToId because it is built in parallel
		return idToToken_.isEmpty();
	}

	// Not sure if we need this...
	
	/*public Object put( Object key, Object value )
	{
		if ( key instanceof String )
		{
			idToToken_.put((String) key, (Integer) value);
			tokenToId_.put((Integer) value, (String) key);
			return value;
		}
		if ( key instanceof Integer )
		{
			tokenToId_.put((Integer) key, (String) value);
			idToToken_.put((String) value, (Integer) key);
			return value;
		}

		throw new ClassCastException("Symbol table cannot contain keys of type "
				+ key.getClass().getName() );
	}*/

	/*public Object remove( Object key )
	{
		if ( key instanceof String )
		{
			Integer token = idToToken_.get(key);
			idToToken_.remove(key);
			tokenToId_.remove(token);
			return token;
		}
		if ( key instanceof Integer )
		{
			String identifier = tokenToId_.get(key);
			tokenToId_.remove(key);
			idToToken_.remove(identifier);
			return identifier;
		}
		
		throw new ClassCastException("Symbol table cannot contain keys of type "
				+ key.getClass().getName() );
	}*/

	/**
	 * Get the size of the symbol table.
	 * 
	 * @return The number of mappings in the symbol table.
	 */
	synchronized public int size()
	{
		// Don't need to check tokenToId because it is built in parallel
		return idToToken_.size();
	}
    
    synchronized public int getHighestToken()
    {
        return nextTokenNum_;
    }

}
