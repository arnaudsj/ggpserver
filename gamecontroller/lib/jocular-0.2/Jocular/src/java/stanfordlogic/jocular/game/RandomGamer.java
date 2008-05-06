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

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import stanfordlogic.game.Gamer;
import stanfordlogic.gdl.Parser;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.Term;
import stanfordlogic.util.Triple;

/**
 *
 */
public class RandomGamer extends Gamer
{
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.game");
    
    private final Random random_;
    
    public RandomGamer(String gameId, Parser p)
    {
        super(gameId, p);
        
        random_ = new Random();
        
        // Seed the random generator if you so desire:
        // (Currently not seeded for debugging)
        //random_.setSeed(System.currentTimeMillis());
    }

    @Override
    protected Triple<Term, String, String> moveThink()
    {
        List<GroundFact> moves = getAllAnswers(currentContext_, "legal", myRole_.toString(), "?x");
        
        if (moves.size() == 0) {
            logger_.severe("No legal moves");
            return null;
        }
        
        if (logger_.isLoggable(Level.FINE))
        {
            logger_.fine("My legal moves: ");
            
            StringBuilder sb = new StringBuilder();
            sb.append(" ");
            for (GroundFact move: moves)
            {
                sb.append(move);
                sb.append(" ");
            }
            logger_.fine(sb.toString());
        }
        
        GroundFact move;

        move = moves.get(random_.nextInt(moves.size()));

        // term 0 is the player, term 1 is the actual move
        Term action = move.getTerm(1);

        return new Triple<Term, String, String>(action, "I'm a silly random player",
                                                "I hope you can beat me");
    }

    @Override
    public void stopIt()
    {
        // Nothing to do.
    }

}
