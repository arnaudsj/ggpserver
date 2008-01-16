package de.tu_dresden.inf.ggp06_2.gamedb.logic;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
//import org.hibernate.TransactionException;
import org.hibernate.criterion.Restrictions;
import de.tu_dresden.inf.ggp06_2.gamedb.objects.GameInformation;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.simulator.Game;

public class GameManager {
    
    // standford games within this project
    public final static String[] gameFiles = new String[] { 
        "./testdata/games/8Puzzel.kif", 
        "./testdata/games/Buttons.kif",
        "./testdata/games/Chess.kif", 
        "./testdata/games/CircleSolitaire.kif", 
        "./testdata/games/Corridor.kif",
        "./testdata/games/Hanoi.kif",
        "./testdata/games/Maze.kif",
        "./testdata/games/Othello.kif",
        "./testdata/games/Queens.kif",
        "./testdata/games/Racetrack.kif",
        "./testdata/games/SimultaneousTicTacToe.kif",
        "./testdata/games/Tetris.kif", 
        "./testdata/games/Tictactoe1.kif",
        "./testdata/games/Tictactoe2.kif" };    
    
    public GameInformation createInformation(String gdl) {
//        Session     session = HibernateUtil.currentSession();
//        Transaction tx      = null;

//        try {
//            tx = session.beginTransaction();
            GameInformation info = new GameInformation();
            info.setGdl( gdl );
            
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
    public Game getGameByIdent( int ident ) {
        Session session = HibernateUtil.currentSession();
        List    infos   = session.createCriteria(GameInformation.class).
                                add( Restrictions.eq("ident", ident) ).list();
        
        // if we found a game we can leave here
        if ( !infos.isEmpty() )
            return new Game( (GameInformation) infos.get(0) );
        
        return null;
    }
    
    public Game getGameByGDL( String gdl ) {
//        Session session = HibernateUtil.currentSession();
//        List    infos   = session.createCriteria(GameInformation.class).
//                                add( Restrictions.eq("gdl", gdl) ).list();
        // if we found a game we can leave here
//        if ( !infos.isEmpty() )
//            return new Game( (GameInformation) infos.get(0) );

        // ok we return the new game
        Game game = new Game( createInformation(gdl) );

        // if we created a new game than valid is still false
//        if ( game.info.isValid() ) {
//            game.info.setValid( true );
//            saveOrUpdate( game.info );
//        }
        return game;
    }

    /**
     * This method returns the game that is described by the gdl in a file.
     * 
     * This method is there just for convenience.
     * @param filename
     * @return
     */
    public Game getGameByFile( String filename ) {
        return getGameByGDL( Parser.parseFile( filename ).toString() );    
    }
    
    /**
     * This method stores the given game information in the database.
     * If the given game information is a new one it will be save; otherwise it
     * will be updated.
     * 
     * @param info - The game information that you want to save or update.
     * @return the persistent game information object
     */
    public static GameInformation saveOrUpdate( GameInformation info ) 
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
