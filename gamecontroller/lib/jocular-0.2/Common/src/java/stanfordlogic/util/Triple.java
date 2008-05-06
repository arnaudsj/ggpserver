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

import java.util.ArrayList;

/**
 *
 * @param <Type1> The first type of the triple.
 * @param <Type2> The second type of the triple.
 * @param <Type3> The third type of the triple.
 */
public class Triple<Type1, Type2, Type3>
{
    ArrayList<String> arr;
    
    public Type1 first;
    public Type2 second;
    public Type3 third;
    
    public Triple(Type1 a, Type2 b, Type3 c)
    {
        this.first = a;
        this.second = b;
        this.third = c;
    }
}
