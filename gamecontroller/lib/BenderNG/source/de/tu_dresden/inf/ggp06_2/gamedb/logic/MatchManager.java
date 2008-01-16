package de.tu_dresden.inf.ggp06_2.gamedb.logic;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
//import org.hibernate.Transaction;
//import org.hibernate.TransactionException;
import org.hibernate.criterion.Restrictions;
import de.tu_dresden.inf.ggp06_2.gamedb.objects.MatchInformation;

public class MatchManager {
    
    public MatchInformation createInformation(String matchId, String role) {
//        Session     session = HibernateUtil.currentSession();
//        Transaction tx      = null;

//        try {
//            tx = session.beginTransaction();
            MatchInformation info = new MatchInformation();
            info.setMatchId( matchId );
            info.setRole( role );
            info.setFinished( false );
            
//            session.save( info );
//            tx.commit();
            return info;
            
//        } catch ( TransactionException te ) {
//            return null;
//        }
    }
    
    /**
     * 
     * @param ident
     * @return
     */
    public MatchInformation getInfoByIdent( int ident ) 
                                                    throws HibernateException {
        Session session = HibernateUtil.currentSession();
        List    infos   = session.createCriteria(MatchInformation.class).
                                add( Restrictions.eq("ident", ident) ).list();
        
        if ( !infos.isEmpty() )
            return (MatchInformation) infos.get(0);
        
        return null;
    }
    
    public MatchInformation getInfoByMatchId( String matchId ) {
        Session session = HibernateUtil.currentSession();
        List    infos   = session.createCriteria(MatchInformation.class).
                                add( Restrictions.eq("matchId", matchId) ).list();
        
        if ( !infos.isEmpty() )
            return (MatchInformation) infos.get(0);
        
        return null;
    }

    /**
     * This method stores the given game information in the database.
     * If the given game information is a new one it will be save; otherwise it
     * will be updated.
     * 
     * @param info - The game information that you want to save or update.
     * @return the persistent game information object
     */
    public static MatchInformation saveOrUpdate( MatchInformation info ) 
                                                    throws HibernateException {
/*     
        Session     session = HibernateUtil.currentSession();
        Transaction tx      = null;

        try {
            tx = session.beginTransaction();
            session.save( info );
            tx.commit();
        } catch (HibernateException he) {
            if ( tx != null )
                tx.rollback();
            throw he;
        }
 */        
        return info;

    }

}
