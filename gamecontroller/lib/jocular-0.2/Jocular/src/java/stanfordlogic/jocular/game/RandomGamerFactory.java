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

import stanfordlogic.game.GameManager;
import stanfordlogic.game.Gamer;
import stanfordlogic.game.GamerFactory;
import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.Parser;
import stanfordlogic.knowledge.BasicKB;
import stanfordlogic.knowledge.GameInformation;
import stanfordlogic.knowledge.KnowledgeBase;
import stanfordlogic.knowledge.MetaGdl;
import stanfordlogic.prover.AbstractReasoner;
import stanfordlogic.prover.BasicReasoner;
import stanfordlogic.prover.TermObject;

/**
 *
 */
public class RandomGamerFactory extends Object implements GamerFactory
{

    public Gamer makeGamer(String gameId, GdlAtom role, GdlList description,
                           int startClock, int playClock)
    {
        Parser parser = GameManager.getParser();
        
        Gamer gamer = new RandomGamer(gameId, parser);
        
        GameInformation gameInfo = new MetaGdl(parser).examineGdl(description);
        
        KnowledgeBase staticKb = new BasicKB();
        staticKb.loadWithFacts(gameInfo.getAllGrounds());
        
        AbstractReasoner reasoner = new BasicReasoner(staticKb, gameInfo.getIndexedRules(), parser);
        
        TermObject myRole = (TermObject) TermObject.buildFromGdl(role);
        
        gamer.initializeGame(myRole, playClock, gameInfo, reasoner);
        
        return gamer;
    }

}
