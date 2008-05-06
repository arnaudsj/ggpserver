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

import java.net.Socket;

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.network.RequestHandler;
import stanfordlogic.network.RequestHandler.HttpHeader;

/**
 *
 */
public class RequestHandlerFactory extends stanfordlogic.network.RequestHandlerFactory
{

    @Override
    protected RequestHandler createFromList( Socket socket, HttpHeader header, GdlList list ) throws Exception
    {
//      The list we're interested in is actually the first element of 'list'.
        list = (GdlList) list.getElement(0);
        
        // the first element of the list should be an atom, telling us what to do
        if ( ( list.getElement(0) instanceof GdlAtom ) == false )
        {
            throw new IllegalArgumentException(
                    "First element of message received in list is not an atom! Got: " + list.getElement(0) );
        }
        GdlAtom command = (GdlAtom) list.getElement(0);
        
        String matchId = list.getElement(1).toString();
        
        RequestHandler result = null;
        
        if ( command.equals("start") )
        {
            result = new StartRequestHandler(socket, header, list, matchId);
        }
        else if ( command.equals("play") )
        {
            result = new PlayRequestHandler(socket, header, list, matchId);
        }
        else if ( command.equals("stop") )
        {
            result = new StopRequestHandler(socket, header, list, matchId);
        }
        else if ( command.equals("kill") )
        {
            // FIXME: make this more secure!!!
            result = new KillRequestHandler(socket, header, list, matchId);
        }
        else
        {
            throw new IllegalArgumentException("Cannot handle request of type: " + command );
        }
        
        return result;
    }
    
}
