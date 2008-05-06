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
package stanfordlogic.knowledge;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import stanfordlogic.prover.Fact;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.Substitution;
import stanfordlogic.prover.VariableFact;

import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.game.GameManager;


/** The knowledge base is a collection of what is true in the
 * current state of the world.
 *
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public abstract class KnowledgeBase
{
    /** Hash codes */
    protected Object                    cleanHash_ = null;
    protected Object                    modHash_ = null;
    
    
    /**
     * Setter for the hash object.
     * 
     * @param hash The object used for the hashing.
     * @param clean True if we're setting the 'clean' hash.
     */
    public void setHash(Object hash, boolean clean)
    {
        if(clean)
            cleanHash_ = hash;
        else
            modHash_ = hash;
    }
    
    /**
     * Getter for the hash object.
     * 
     * @param clean True if we're getting the 'clean' hash.
     * @return The object used for hashing the knowledge base.
     */
    public Object getHash(boolean clean)
    {
        if(clean)
            return cleanHash_;
        return modHash_;
    }    
    
    /**
     * Initialize it with a set of facts
     * @param symtab
     *            The symbol table to use for the facts in this knowledge base.  
     * @param facts
     *            The facts to store in the KB
     */
    public void loadWithFacts(List<GroundFact> facts)
    {
        for(GroundFact fact: facts)
            setTrue(fact);
    }
    
    /**
     * Clear all relations such that nothing is true anymore.
     */
    public abstract void clear();
    
    /**
     * Get the number of facts true in the current state.
     * 
     * @return The number of true facts.
     */
    public abstract int getNumFacts();
    
    public abstract boolean isTrue(GroundFact fact);
    
    public boolean isTrue(Fact fact)
    {
        // If it's not a ground fact, it's false
        if ( fact instanceof GroundFact == false )
            return false;
        
        return isTrue( (GroundFact) fact );
    }
    
    /**
     * Mark a given fact as 'true' in the current state.
     * 
     * @param fact Fact to set as true.
     */
    public abstract void setTrue(GroundFact fact);
    
    /**
     * Mark a given fact as 'false' in the current state.
     * Note that given 'negation as failure' this means the same
     * thing as removing a fact from the database.
     * 
     * @param fact Fact to set as false.
     */
    public abstract void setFalse(GroundFact fact);
    
    /**
     * Get the GDL state as a string. Note that in many cases using
     * the Writer version of this method will be more efficient, since the string
     * memory doesn't have to be allocated twice.
     * 
     * @return A string containing the state of this knowledge base in GDL sentences.
     */
    public String stateToGdl()
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(output);
        stateToGdl(ps);
        ps.flush();
        return output.toString();
    }
 
    /**
     * Write the state in GDL to a Writer (character stream). Uses the Game Manager's
     * symbol table.
     * 
     * @param target The Writer to output to.
     */
    final public void stateToGdl(PrintStream target)
    {
        stateToGdl(target, GameManager.getSymbolTable());
    }
    
    public abstract void stateToGdl(PrintStream target, SymbolTable symTab);
    
    /**
     * Return a sorted list of facts true in this database. The ordering is
     * according to the elements' Comparable interface.
     * 
     * @return A list of all facts in this database.
     */
    public List<GroundFact> getFacts()
    {
        return getFacts(true);
    }
    
    /**
     * Get an iterator for the facts in this knowledge base. Contrary to
     * getFacts(), this method tries to create the smallest amount of memory
     * possible. The iterator is backed by the kb's facts, which means that
     * if you change the kb during iteration the state of the iterator will
     * probably be undefined.
     * 
     * @return An iterator over the kb facts.
     */
    public abstract Iterator<GroundFact> getIterator();
    
    /**
     * Similar to getIterator, but returns an iterable.
     * 
     * @return An iterable over the kb facts.
     */
    final public Iterable<GroundFact> getIterable()
    {
        return new Iterable<GroundFact>() {
            public Iterator<GroundFact> iterator() {
                return getIterator();
            }
        };
    }
    
    /**
     * Get the number of differences with another kb. A 'difference' is defined
     * as a fact in one KB but not the other.
     * 
     * <p>
     * Formally: # differences = #(this - other) + #(other - this)
     * 
     * @param otherKb
     *            The KB to compare differences with.
     * @return The number of differences between the two kb.
     */
    public int getDifferences(KnowledgeBase otherKb)
    {
        int count = 0;
        
        // Count things in this but not in other
        Iterator<GroundFact> myIt = this.getIterator();
        while ( myIt.hasNext() )
        {
            if ( otherKb.isTrue(myIt.next()) == false )
                count++;
        }
        
        // Count things in other but not in this
        Iterator<GroundFact> otherIt = otherKb.getIterator();
        while ( otherIt.hasNext() )
        {
            if ( this.isTrue(otherIt.next()) == false )
                count++;
        }
        
        return count;
    }
    
    /**
     * Return a list of all facts true in this database.
     * 
     * @param sorted True if the list should be sorted.
     * @return A list of all facts true in this database.
     */
    public abstract List<GroundFact> getFacts(boolean sorted);
    
    /**
     * Retrieves all the facts in the kb that are unifiable with the input (variable) fact.
     * @param fact      The template fact to match kb facts against
     * @return          A list of fact that are unifiable.
     */
    public abstract List<Substitution> getUnifiable(VariableFact fact);
}
