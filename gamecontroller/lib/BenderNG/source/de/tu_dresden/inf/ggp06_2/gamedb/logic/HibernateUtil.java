package de.tu_dresden.inf.ggp06_2.gamedb.logic;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.cfg.Configuration;

/**
 * @author Ingo Keller
 * @version v0.03
 *
 * This is the HibernateUtil which is a singleton that handles the
 * hibernate sessions and the thread-safe access
 * Its just a tutorial extension.
 */

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("HibernateUtil(): Initial SessionFactory creation failed.");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static final ThreadLocal<Session> threadSession = 
                                                     new ThreadLocal<Session>();
        
    /**
     * This method returns the current hibernate session. Depending on the
     * context it the method is used the hibernate session is retrieved 
     * from either a http session (http session has to be available) or 
     * from a ThreadLocal variable (e.g. in JUnit Test Environment).
     * @return Session - current hibernate session object
     */
    public synchronized static Session currentSession() {
        
        Session s = threadSession.get();
            
        // Open a new Session, if this HttpSession none yet
        if (s == null) {
            s = sessionFactory.openSession();
            threadSession.set(s);         
        }
        return s;
    }

    /**
     * This method closes a hibernate session if one is available.
     */
    public static void closeHibernateSession() {
        Session s = threadSession.get();
        if (s != null )
            s.close();
    }

    /**
     * This method updates a object within a hibernate session.
     * @param obj - object that should be updateded
     * @return boolean - true if success, otherwise false
     */
    public static boolean updateObject( Object obj ){
        try {

            Session     session = HibernateUtil.currentSession();
            Transaction tx      = session.beginTransaction();
            session.update(obj);
            tx.commit();
            return true;
        }        
        catch (TransactionException te) {
            System.err.println("Error while transaction ");
            te.printStackTrace();
            return false;
        }
    }
}
