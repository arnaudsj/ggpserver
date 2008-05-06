///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

package stanfordlogic.util;

import java.util.Map;
import java.util.HashMap;

import java.lang.RuntimeException;

public class CommandLineParser 
{

	private String usage = "usage: None specified.";
	
	private Map<String, Boolean> flags = new HashMap<String, Boolean>();
	private Map<String, String> params = new HashMap<String, String>();
	
	public void setUsage(String newUsage)
	{
		usage = newUsage;
	}
	
	public void addFlag(String flagName)
	{
		if ( flags.containsKey(flagName) || params.containsKey(flagName) )
		{
			throw new RuntimeException("The command line arg " + flagName + " is already in use!");
		}
		flags.put(flagName, false);
	}
	
	public void addParam(String paramName)
	{
		if ( flags.containsKey(paramName) || params.containsKey(paramName) )
		{
			throw new RuntimeException("The command line arg " + paramName + " is already in use!");
		}
		params.put(paramName, null);		
	}
	
	public boolean argSpecified(String argName)
	{
		if ( flags.containsKey(argName) )
		{
			return (flags.get(argName) == true);
		}
		else if ( params.containsKey(argName) )
		{
			return (params.get(argName) != null);
		}
		else
		{
			throw new RuntimeException("The command line arg " + argName + " does not exist!");						
		}
	}
	
	public String getArgAsString(String argName)
	{
		if ( flags.containsKey(argName) )
		{
			return (flags.get(argName) == true) ? "True" : "False";
		}
		else if ( params.containsKey(argName) )
		{
			return params.get(argName);
		}
		else
		{
			throw new RuntimeException("The command line arg " + argName + " does not exist!");						
		}		
	}
	
	public int getArgAsInt(String argName)
	{
		return Integer.parseInt(getArgAsString(argName));
	}
	
	public void parse(String[] args)
	{
		for ( String arg : args )
		{
			int equalsIndex = arg.indexOf('=');
			
			// Case: arg is not of the form arg=val
			if ( equalsIndex == -1 )
			{
				if ( flags.containsKey(arg) )
				{
					flags.put(arg, true);
				}
				else
				{
					System.out.println(usage);
					System.exit(1);
				}
			}
			
			// Case: arg is of the form arg=val
			else
			{
				String param = arg.substring(0, equalsIndex);
				String value = arg.substring(equalsIndex+1);
				
				if ( params.containsKey(param) )
				{
					params.put(param, value);
				}
				else
				{
					System.out.println(usage);
					System.exit(1);
				}
			}
		}
	}
	
}
