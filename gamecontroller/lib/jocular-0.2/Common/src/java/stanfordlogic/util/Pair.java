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
package stanfordlogic.util;

/**
 *
 * @param <Type1> The first type of the pair.
 * @param <Type2> The second type of the pair.
 * 
 */
public class Pair<Type1, Type2>
{
    public Type1 first;
    public Type2 second;
    
    public Pair(Type1 a, Type2 b)
    {
        first = a;
        second = b;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append('<');
        sb.append(first);
        sb.append(';');
        sb.append(second);
        sb.append('>');
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Pair))
            return false;
        Pair pair = (Pair) obj;
        return first.equals(pair.first) && second.equals(pair.second);
    }
    
    @Override
    public int hashCode()
    {
        return first.hashCode() + second.hashCode();
    }
}
