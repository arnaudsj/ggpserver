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
package stanfordlogic.game;

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlList;

/**
 *
 */
public interface GamerFactory
{
    public Gamer makeGamer(String gameId, GdlAtom role, GdlList description,
            int startClock, int playClock);
}
