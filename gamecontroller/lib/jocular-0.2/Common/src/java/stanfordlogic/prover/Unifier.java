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
package stanfordlogic.prover;

import java.util.logging.Level;
import java.util.logging.Logger;

import stanfordlogic.game.GameManager;
import stanfordlogic.util.Util;

/**
 * Functions for unifications.
 *
 * <b>WARNING</b>: the debugging is not thread-safe.
 *
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class Unifier
{
    private static int              unificationLevel_   = 0;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.prover.unify");
    
    private static void enterUnificationLevel(Fact f1, Fact f2)
    { 
        if (logger_.isLoggable(Level.FINER))
        {
            logger_.finer( Util.makeIndent( unificationLevel_ )
                    + "Unifier: attempting to unify " + f1.toString(GameManager.getSymbolTable()) + " and " + f2.toString(GameManager.getSymbolTable()) );
            
            unificationLevel_++;
        }
    }
    
    private static void exitUnificationLevel(Substitution subs)
    {
        if (logger_.isLoggable(Level.FINER))
        {
            unificationLevel_--;
            
            if ( unificationLevel_ < 0 )
                throw new RuntimeException("Unification level < 0!!");
            
            if ( subs == null )
                logger_.finer( Util.makeIndent( unificationLevel_ )
                        + "Unifier: failed to unify." );
            else
                logger_.finer( Util.makeIndent( unificationLevel_ )
                        + "Unifier: mgu = " + subs );
        }
    }
    
    public static Substitution mgu(Fact f1, Fact f2)
    {
        // Make sure this is even worth our time to check
        if ( f1.relationName_ != f2.relationName_ )
            return null;
        if ( f1.getArity() != f2.getArity() )
            return null;
        
        enterUnificationLevel(f1, f2);
            
        Substitution subs = new Substitution();
        
        if ( mgu(f1, f2, subs) )
        {
            exitUnificationLevel(subs);
            return subs;
        }
        else
        {
            exitUnificationLevel(null);
            return null;
        }
    }
    
    private static boolean mgu(Fact f1, Fact f2, Substitution subsSoFar)
    {
        // Make sure this is even worth our time to check
        if ( f1.relationName_ != f2.relationName_ )
            return false;
        if ( f1.getArity() != f2.getArity() )
            return false;
        
        // Find the mgu for each column of the facts
        for ( int i = 0; i < f1.getArity(); i++ )
        {
            // If there is no mgu, just die
            if ( mgu( f1.getTerm(i), f2.getTerm(i), subsSoFar ) == false )
                return false;
        }
        
        return true;
    }
    
    private static boolean mgu(Term t1, Term t2, Substitution subsSoFar)
    {
        return t1.mgu(t2, subsSoFar);
    }
}
