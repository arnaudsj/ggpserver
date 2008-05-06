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
import stanfordlogic.game.Gamer;
import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.network.RequestHandler;
import stanfordlogic.util.Triple;
import stanfordlogic.util.Util;

/**
 *
 */
public final class PlayRequestHandler extends RequestHandler
{
    private GdlList content_;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.network");
    
    protected PlayRequestHandler( Socket socket, RequestHandler.HttpHeader header,
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
        if ( content_.getSize() != 3 )
        {
            throw new IllegalArgumentException( "PLAY request should have exactly three arguments, not "
                    + content_.getSize() );
        }
        
        GdlExpression prevExp = content_.getElement(2);
        GdlList prevMoves;
        
        StringBuilder prevMovesStr = new StringBuilder();
        
        if ( prevExp instanceof GdlList )
        {
            prevMoves = (GdlList) content_.getElement(2);
            
            prevMovesStr.append(" Previous moves: ");
            
            for ( GdlExpression exp: prevMoves )
            {
                prevMovesStr.append( exp.toString() );
                prevMovesStr.append("  ");
            }
        }
        else
        {
            // make sure it's an atom containing NIL
            if ( prevExp instanceof GdlAtom == false || prevExp.equals( GameManager.getParser().TOK_NIL ) == false )
                throw new IllegalArgumentException("PLAY request doesn't have LIST and doesn't have NIL atom as prev-moves!");
            prevMoves = null; // empty prev moves
        }
        
        Gamer game = GameManager.getGame(gameId_);
        if(game == null)
        {
            logger_.severe("No game found for play request ID: " + gameId_);
            finish();
            return;
        }

        logger_.info(gameId_ + ": Beginning move think." + prevMovesStr);
        
        Triple<GdlExpression,String,String> next;
        
        try {
            next = game.play(prevMoves);
        }
        catch (Exception e)
        {
            logger_.severe(gameId_ + ": Exception while processing 'game.play':" + e.toString());
            
            // Build the stack trace:
            StackTraceElement [] stack = e.getStackTrace();
            
            StringBuilder sb = new StringBuilder();
            
            for (int i = 0; i < stack.length; i++)
            {
                StackTraceElement elem = stack[i];
                sb.append(elem.getFileName() + ":" + elem.getLineNumber());
                if (i < stack.length - 1) {
                    sb.append(" <- ");
                }
            }
            
            logger_.severe(gameId_ + ": " + sb.toString());
            
            GdlAtom nil = new GdlAtom(GameManager.getSymbolTable(), GameManager.getParser().TOK_NIL);
            next = new Triple<GdlExpression, String, String>(nil, "exception", "oh crap");
        }
        
        String moveStr = next.first.toString();
        logger_.info(gameId_ + ": End of move think. Making move: " + moveStr );
        
        StringBuilder answer = new StringBuilder(128);
        
        answer.append( moveStr );
        
        // is there an explanation?
        if ( next.second != null )
        {
            answer.append(" (explanation \"");
            answer.append( Util.escapeChars(next.second) );
            answer.append("\")");
        }
        
        // Is there a taunt?
        if ( next.third != null )
        {
            answer.append(" (taunt \"");
            answer.append( Util.escapeChars(next.third) );
            answer.append("\")");
        }
        
        sendAnswer(answer.toString());

        // THINK: we perform garbage collection here: is that right?
        System.gc();
        
        // All done
        finish();
    }

}
