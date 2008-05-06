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
import java.util.logging.Logger;

import stanfordlogic.game.GameManager;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.network.RequestHandler;

/**
 *
 */
public class StopRequestHandler extends RequestHandler
{
    private GdlList content_;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.network");
    
    protected StopRequestHandler( Socket socket, RequestHandler.HttpHeader header,
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
        // Tell the game manager that the game ended, passing in the previous moves
        GdlExpression prevMoves = content_.getElement(2);
        
        if (prevMoves instanceof GdlList == false) {
            logger_.severe(gameId_ + ": Previous move list in STOP message was not a GDL list!");
            finish();
            return;
        }
        
        GameManager.endGame(gameId_, (GdlList) prevMoves);
        
        sendAnswer("DONE");

        // THINK: we perform garbage collection here: is that right?
        System.gc();
        
        // All done
        finish();
    }
}
