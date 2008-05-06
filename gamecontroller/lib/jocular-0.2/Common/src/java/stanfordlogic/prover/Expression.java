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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.game.GameManager;


/**
 *
 * A logical sentence.
 */
public abstract class Expression
{
    final protected static Expression [] EMPTY_SENTENCES = new Expression[0];
    final protected static Term [] EMPTY_TERMS = new Term [0];
    
    final protected static Substitution EMPTY_SUB = new Substitution();
    
    @Override
    public final String toString()
    {
        return toString(GameManager.getSymbolTable());
    }
    
    final public String toString(SymbolTable symtab)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        printToStream( new PrintStream( stream ), symtab );
        return stream.toString();
    }
    
    public abstract Expression applySubstitution(Substitution sigma);
    
    public Expression uniquefy()
    {
        Map<TermVariable, TermVariable> newVarMap = new HashMap<TermVariable, TermVariable>();
        
        return this.uniquefy(newVarMap);
    }
    
    public abstract Expression uniquefy(Map<TermVariable, TermVariable> varMap);
    
    public abstract void printToStream(PrintStream target, SymbolTable symtab);
    
    /**
     * Return true if this expression contains a functional term with name <tt>functionName</tt>.
     * 
     * @param functionName The name of the term function to check for.
     * @return True if this expression contains the term function.
     */
    public abstract boolean hasTermFunction(int functionName);
    
    /**
     * Return true if this expression contains a given variable.
     * 
     * @param varName The variable to search for
     * @return True if this expression contains the variable.
     */
    public abstract boolean hasTermVariable(int varName);
    
    /**
     * Can this expression be mapped to the other
     * expression? An expression can be mapped to another expression if
     * and only if there is a unifier from the one to the other that only
     * makes variable assignments.
     * 
     * <p>Note that this relationship is <b>not</b> symmetric:
     * one expression can be mapped to another expression, despite that second
     * relation not mapping to the first.
     * 
     * @param other The expression with which to test mapping.
     * @return True if <tt>this</tt> can be mapped to <tt>other</tt>.
     */
    public abstract boolean canMapVariables( Expression other );
}
