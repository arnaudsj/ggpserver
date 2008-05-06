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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.Substitution;
import stanfordlogic.prover.TermFunction;
import stanfordlogic.prover.VariableFact;


/**
 * A basic knowledge base implementation. Uses a mapping of relation
 * name to set of things true in that relation.
 * 
 * <p>
 * For example:
 * 
 * <pre>
 *   true
 *    |- init(1,1,b) ; init(1,2;x) ; init(1,3,o) ; ...
 *   succ
 *    |- 1,2 ; 2,3 ; 3,4 ; ...
 * </pre>
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class BasicKB extends KnowledgeBase
{
    private Map<Integer, Set<GroundFact>>         database_;
    private int                                   numFacts_;

    public BasicKB( )
    {
        database_ = new TreeMap<Integer, Set<GroundFact>>();
        numFacts_ = 0;
    }
    
    @Override
    public void clear()
    {
        numFacts_ = 0;
        database_.clear();
    }
    
    @Override
    public int getNumFacts()
    {
        return numFacts_;
    }
    
    @Override
    public boolean isTrue( GroundFact fact )
    {
        return getFacts( fact.getRelationName() ).contains(fact);
    }

    @Override
    public void setTrue( GroundFact fact )
    {
        Set<GroundFact> facts = getFacts( fact.getRelationName() );

        // increment numFacts if the element was actually added
        if ( facts.add( fact ) )
            numFacts_ ++;
    }
    
    @Override
    public void setFalse( GroundFact fact )
    {
        Set<GroundFact> facts = getFacts( fact.getRelationName() );
        
        // decrement numFacts if the element was actually removed
        if ( facts.remove( fact ) )
            numFacts_ --;
    }

    /**
     * Get the facts for a given token of a given arity.
     * 
     * Note that a fact is really just a row in a table. For a relation of arity
     * 3 named 'cell', we could have as a fact (cell 1 2 x) which would mean
     * that the fact as represented here in the code would be [1,2,x].
     * 
     * @param token
     *            The token whose facts to get.
     * 
     * @return A list of facts for the token/arity.
     */
    private Set<GroundFact> getFacts( int token )
    {
        Set<GroundFact> facts;

        facts = database_.get( token );

        // Make sure that this entry in the database exists
        if ( facts == null )
        {
            facts = new TreeSet<GroundFact>();
            database_.put( token, facts );
        }

        return facts;
    }
    
    private class FactsIterator implements Iterator<GroundFact>
    {
        private Iterator<Integer> relationIterator_;

        private Iterator<GroundFact> currentIterator_ = null;

        private FactsIterator()
        {
            relationIterator_ = database_.keySet().iterator();
            advanceIterator();
        }
        
        private void advanceIterator()
        {
            if ( relationIterator_.hasNext() )
                currentIterator_ = database_.get(relationIterator_.next()).iterator();
            else
                currentIterator_ = null;
        }

        public boolean hasNext()
        {
            if ( currentIterator_ == null )
                return false;
            else
            {
                if ( currentIterator_.hasNext() )
                    return true;
                else
                {
                    advanceIterator();
                    return hasNext();
                }
            }
        }

        public GroundFact next()
        {
            // Get the current element
            return currentIterator_.next();
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Can't remove from NaiveKB iteration");
        }
    }

    @Override
    public Iterator<GroundFact> getIterator()
    {
        return new FactsIterator();
    }

    @Override
    public void stateToGdl( PrintStream target, SymbolTable symtab )
    {
        // iterate over all facts, printing them out
        for ( Set<GroundFact> facts : database_.values() )
        {
            for ( GroundFact f : facts )
                f.printToStream( target, symtab );
        }
        
    }

    @Override
    public List<GroundFact> getFacts(boolean sorted)
    {
        List<GroundFact> c = new ArrayList<GroundFact>();
        
        // iterate through all facts, adding them to the collection
        for ( Set<GroundFact> facts : database_.values() )
        {
            for ( GroundFact f : facts )
                c.add( f );
        }
        
        if ( sorted )
            Collections.sort(c);
        
        return c;
    }
    
    @Override
    public List<Substitution> getUnifiable(VariableFact fact)
    {
        List<Substitution> result = new ArrayList<Substitution>();
        
        Set<GroundFact> facts = database_.get(fact.getRelationName());
        if( facts == null )
            return result;
        
        if(fact.getArity() > 0 && fact.getTerm(0) instanceof TermFunction)
        {
            for(GroundFact nFact : facts)
            {
                // THINK: do we really want to get getArity() > 0 here?
                // maybe the variable fact should bind to a 0-arity function.
                // THINK: write a junit test case for this.
                
                if(nFact.getArity() > 0 && nFact.getTerm(0) instanceof TermFunction)
                {
                    if(((TermFunction) nFact.getTerm(0)).functionName_ == ((TermFunction) fact.getTerm(0)).functionName_)
                    {
                        Substitution s = fact.unify(nFact);
                        if ( s != null )
                            result.add(s);
                    }
                }
            }
        }
        else
        {
            for(GroundFact nFact : facts)
            {
                Substitution s = fact.unify(nFact);
                if ( s != null )
                    result.add( s );
            }
        }
        return result;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(! (other instanceof BasicKB))
            return false;
        List<GroundFact> mine = getFacts();
        List<GroundFact> his = ((BasicKB) other).getFacts();
        if(mine.size() != his.size())
            return false;
        for(int i=0; i<mine.size(); i++ )
        {
            if(!mine.get(i).equals(his.get(i)))
                return false;
        }
        return true;
    }
}
