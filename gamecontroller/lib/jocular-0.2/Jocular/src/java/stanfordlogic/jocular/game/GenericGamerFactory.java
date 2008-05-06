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

import java.lang.reflect.Constructor;

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
public class GenericGamerFactory implements GamerFactory
{
    private Class<? extends Gamer> gamerType_ = LegalGamer.class;
    
    private Gamer makeGamer(String gameId, Parser parser)
    {
        try {
            Constructor<? extends Gamer> c = gamerType_.getConstructor(new Class [] {String.class, Parser.class});
            Gamer g = c.newInstance(new Object[] {gameId, parser});
            return g;
        }
        catch (Exception e) {
            // this is really, really bad.
            e.printStackTrace();
            return null;
        }
    }
    
    public void setGamerType(Class<? extends Gamer> gamerType)
    {
        gamerType_ = gamerType;
    }
    public Class<? extends Gamer> getGamerType()
    {
        return gamerType_;
    }

    public Gamer makeGamer(String gameId, GdlAtom role, GdlList description,
                           int startClock, int playClock)
    {
        Parser parser = GameManager.getParser();
        
        Gamer gamer = makeGamer(gameId, parser);
        
        GameInformation gameInfo = new MetaGdl(parser).examineGdl(description);
        
        KnowledgeBase staticKb = new BasicKB();
        staticKb.loadWithFacts(gameInfo.getAllGrounds());
        
        AbstractReasoner reasoner = new BasicReasoner(staticKb, gameInfo.getIndexedRules(), parser);
        
        TermObject myRole = (TermObject) TermObject.buildFromGdl(role);
        
        gamer.initializeGame(myRole, playClock, gameInfo, reasoner);
        
        return gamer;
    }

}
