package de.tu_dresden.inf.ggp06_2.gamedb.logic;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
//import org.hibernate.TransactionException;
import org.hibernate.criterion.Restrictions;
import de.tu_dresden.inf.ggp06_2.gamedb.objects.StrategyInformation;

public class StrategyManager {
    
    public StrategyInformation createInformation(String name) {
//        Session     session = HibernateUtil.currentSession();
//        Transaction tx      = null;

//        try {
//            tx = session.beginTransaction();
            StrategyInformation info = new StrategyInformation();
            info.setName( name );
            
//            session.save( info );
//            tx.commit();
            return info;
//            
//        } catch ( TransactionException te ) {
//            return null;
//        }
    }
    
    /**
     * 
     * @param ident
     * @return
     */
    public StrategyInformation getInfoByIdent( int ident ) 
                                                    throws HibernateException {
        Session session = HibernateUtil.currentSession();
        List    infos   = session.createCriteria(StrategyInformation.class).
                                add( Restrictions.eq("ident", ident) ).list();
        
        if ( !infos.isEmpty() )
            return (StrategyInformation) infos.get(0);
        
        return null;
    }
    
    public StrategyInformation getInfoByName( String name ) {
        Session session = HibernateUtil.currentSession();
        List    infos   = session.createCriteria(StrategyInformation.class).
                                add( Restrictions.eq("name", name) ).list();
        
        if ( !infos.isEmpty() )
            return (StrategyInformation) infos.get(0);
        
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
    public static StrategyInformation saveOrUpdate( StrategyInformation info ) 
                                                    throws HibernateException {
     
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
        return info;
    }

}
