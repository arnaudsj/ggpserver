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
package stanfordlogic.jocular.game;

import java.util.logging.Logger;

import stanfordlogic.game.Gamer;
import stanfordlogic.gdl.Parser;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.Term;
import stanfordlogic.util.Triple;

/**
 *
 */
public class LegalGamer extends Gamer
{
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.game");
    
    public LegalGamer(String gameId, Parser p)
    {
        super(gameId, p);
    }

    @Override
    protected Triple<Term, String, String> moveThink()
    {
        GroundFact move = getAnAnswer(currentContext_, "legal", myRole_.toString(), "?x");
        
        if (move == null) {
            logger_.severe("No legal moves");
            return null;
        }
        else {
            // term 0 is the player, term 1 is the actual move
            Term action = move.getTerm(1);
            
            return new Triple<Term, String, String>(action, "I'm a silly legal player",
                                                    "I hope you can beat me");
        }
    }

    @Override
    public void stopIt()
    {
        // Nothing to do here.

    }

}
