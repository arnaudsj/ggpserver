package de.tu_dresden.inf.ggp06_2.resolver.helper;

import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;

/**
 * The TheoryLogger class contains the debugging log for all logic related
 * events within the resolver classes. The resulting log should only show how
 * the resolver parts are working together while proving a statement.
 * It should not include to much detail of the underlying class structure or
 * java details!
 *
 * This logger can be access through the Theory class either through the method
 * getDebugLog() or directly with the public variable debugLog.
 *
 * So far the main methods of the TheoryLogger are:
 * <ul>
 *   <li>enterChain</li>
 *   <li>exitChain</li>
 *   <li>chainBody</li>
 * </ul>
 *
 * This class is also used to get nicer debug output strings. So here are also
 * some static render methods.
 * <ul>
 *   <li>renderMap</li>
 * </ul>
 *
 * If you think about more logic debug methods and/or nice rendering method for
 * debug purposes this is the class where the methods should go in.
 *
 * A method for logging should be a non-static method whereas
 * a method for rendering should be a static method.
 *
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 *
 */
public class TheoryLogger {

    /* Stores the logger for this class */
    public final static Logger logger = Logger.getLogger(Theory.class);

    /* proofLevel is only used to get the right indentation */
    int proofLevel;

    /**
     * Constructor creates a new Theory Logger.
     */
    public TheoryLogger() {
        proofLevel = 0;
    }


    /**
     * This method is a debug hook for entering a prove chain.
     * @param expression
     */
    public void enterChain(Expression expression) {
        ++proofLevel;
        //logger.debug( indent() + "Prove: " + expression );
    }


    /**
     * This method is a debug hook for entering a prove chain.
     * @param expression
     */
    public void enterChain(ExpressionList list) {
        ++proofLevel;
        //logger.debug( indent() + "Prove: " + list );
    }


    /**
     * This method is a debug hook for exiting a prove chain.
     * @param expression
     */
    public void exitChain(Expression term, boolean b, Object exitNode) {
        if (b)
            logger.debug( indent() + "Exit: " + b + " " + term );
        else
            logger.debug( indent() + "Exit: " + b );
        --proofLevel;
    }


    /**
     * This method is a debug hook for exiting a prove chain.
     * @param expression
     */
    public void exitChain(ExpressionList term, boolean b, Object exitNode) {
        if (b)
            logger.debug( indent() + "Exit: " + b + " " + term );
        else
            logger.debug( indent() + "Exit: " + b );
        --proofLevel;
    }


    public void chain(Object obj) {
        logger.debug( indent() +
                      "chain - chaining to: " +
                      obj.getClass().getSimpleName() );
    }


    public void chainBody(Object obj) {
        logger.debug( indent() +
                      "chain - going to chainBody: " +
                      obj.getClass().getSimpleName() );
    }

    /**
     * This method returns the indentation for the debug output.
     */
    private String indent() {
        StringBuilder sb = new StringBuilder("");
        for ( int i = 0; i < proofLevel; i++ )
            sb.append( " " );
        return sb.toString();
    }

}
