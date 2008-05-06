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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;


/**
 *
 */
public class LogPropertiesLoader
{

    static public Handler getDefaultHandler()
    {
        Handler h = new ConsoleHandler();
        
        // Set the handler to accept *all* messages; use the Logger's level for
        // more fine-grained control 
        h.setLevel(Level.ALL);
        return h;
    }

    public LogPropertiesLoader() throws IOException
    {
        // First, read in the default properties

        String fname = System.getProperty("java.home");
        if (fname == null)
        {
            throw new Error("Can't find java.home ??");
        }
        File f = new File(fname, "lib");
        f = new File(f, "logging.properties");
        fname = f.getCanonicalPath();
        
        Properties props = new Properties();
        
        props.load(new FileInputStream(fname));

        
        // Now, edit the settings we're interested in:
        props.setProperty("java.util.logging.handlers",
                           "java.util.logging.ConsoleHandler");
        props.setProperty("java.util.logging.ConsoleHandler.formatter",
                           "stanfordlogic.util.LogFormatter");
        
        // Create a new input stream for these properties:
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        props.store(output,"");
        output.flush();
        
        String str = output.toString();
        
        ByteArrayInputStream input = new ByteArrayInputStream(str.getBytes());
        LogManager.getLogManager().readConfiguration(input);
    }
}
