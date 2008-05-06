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

/** General utility functions.
 *
 * @author Some functions based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class Util
{
    
    /** Take a string and escape backslashes and quotes.
     * 
     * @param str The string to escape.
     * @return The string in which all backslashes and quotes have been escaped.
     */
    public static String escapeChars(String str)
    {
        StringBuilder builder = new StringBuilder(str.length() + 6);
        
        for ( int i = 0; i < str.length(); i++ )
        {
            char c = str.charAt(i);
            
            // if we have to quote this, stick a backslash in front.
            if ( c == '"' || c == '\\' )
                builder.append('\\');
            
            builder.append(c);
        }
        
        return builder.toString();
    }
    
    
    public static String makeIndent(int howMuch)
    {
        return makeIndent(howMuch, "  ");
    }
    
    public static String makeIndent(int howMuch, String indent)
    {
        StringBuilder sb = new StringBuilder();
        
        while ( howMuch-- > 0 )
            sb.append(indent);
        
        return sb.toString();
    }
    
    public static long extractMatchID(String str)
    {
        if(str == null || !str.startsWith("match."))
            return -1;
        long res;
        try
        {
            res = Long.parseLong(str.substring(6));
        }
        catch(NumberFormatException e)
        {
            res = -2;
        }
        return res;
    }
}
