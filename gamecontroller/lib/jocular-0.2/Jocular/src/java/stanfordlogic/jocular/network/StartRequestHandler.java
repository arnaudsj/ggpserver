///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

package stanfordlogic.jocular.network;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import stanfordlogic.game.GameManager;
import stanfordlogic.game.Gamer;
import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.network.RequestHandler;


public final class StartRequestHandler extends RequestHandler
{
    private GdlList content_;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.network");
    
    protected StartRequestHandler( Socket socket, RequestHandler.HttpHeader header,
            GdlList content, String matchId ) throws IOException
    {
        super(socket, header, matchId);
        content_ = content;
    }
    
    /* (non-Javadoc)
     * @see camembert.structure.RequestHandler#execute()
     */
    @Override
    protected void execute() throws IOException
    {
        // set up the game with the right information
        if ( content_.getSize() != 6 )
        {
            throw new IllegalArgumentException( "START request should have exactly six arguments, not "
                    + content_.getSize() );
        }
        
        GdlAtom role = (GdlAtom) content_.getElement(2);
        GdlList description = (GdlList) content_.getElement(3);
        
        int start = Integer.parseInt( content_.getElement(4).toString() );
        int play = Integer.parseInt( content_.getElement(5).toString() );
        
        Gamer gamer = GameManager.newGame(gameId_, role, description, start, play);
        
        if (gamer != null) {
            logger_.info(gameId_ + ": Game successfully created.");
        }
        else {
            logger_.severe(gameId_ + ": Could not create gamer from start message!");
        }
        
        // Tell Game Master that we're ready.
        sendAnswer("READY");
        
        // All done
        finish();
    }
}
