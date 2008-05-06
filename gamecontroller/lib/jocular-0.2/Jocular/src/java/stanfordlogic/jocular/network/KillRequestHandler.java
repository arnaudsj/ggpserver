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
package stanfordlogic.jocular.network;

import java.io.IOException;
import java.net.Socket;

import stanfordlogic.gdl.GdlList;
import stanfordlogic.network.RequestHandler;

/**
 *
 */
public class KillRequestHandler extends RequestHandler
{
    
    protected KillRequestHandler( Socket socket, RequestHandler.HttpHeader header,
                                  GdlList content, String matchId ) throws IOException
    {
        super(socket, header, matchId);
    }

    /* (non-Javadoc)
     * @see camembert.structure.RequestHandler#execute()
     */
    @Override
    protected void execute() throws IOException
    {
		manager_.shutdown();
    }
}
