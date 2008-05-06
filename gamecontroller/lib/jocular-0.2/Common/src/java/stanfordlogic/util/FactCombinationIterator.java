///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

package stanfordlogic.util;

import java.util.Iterator;
import java.util.List;

import stanfordlogic.knowledge.FactProcessor;
import stanfordlogic.prover.GroundFact;

/**
 * Generates combinations of facts. Used to generate all combinations of several
 * lists of facts, keeping one fact constant. Given the constant fact and
 * several lists of facts, generates all combinations that can be formed of the
 * constant fact and a fact from each list of facts.
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class FactCombinationIterator implements Iterator<GroundFact[]>, Iterable<GroundFact[]>
{
    private boolean                     endOfIteration_;
    private int[]                       currentIndeces_;
    private GroundFact[]                currentFacts_;
    private List<List<GroundFact>>      items_;
    private FactProcessor               processor_;
    
    private int                         changeLevels_;
    
    private static final FactProcessor dummyProcessor = new FactProcessor() {
        @Override
        public GroundFact processFact(GroundFact fact)
        {
            return fact;
        }
    };
    
    /**
     * 
     * 
     * @param staticFact The fact that will appear in all results.
     * @param items The facts from which to generate combinations.
     */
    public FactCombinationIterator(GroundFact staticFact, List<List<GroundFact>> items)
    {
        this(staticFact, items, dummyProcessor);
    }
    
    /**
     * 
     * 
     * @param staticFact The fact that will appear in all results.
     * @param items The facts from which to generate combinations.
     * @param processor The processor to apply to all facts.
     */
    public FactCombinationIterator(GroundFact staticFact, List<List<GroundFact>> items, FactProcessor processor)
    {
        processor_ = processor;
        items_ = items;
        currentIndeces_ = new int[items_.size() + 1];
        currentFacts_ = new GroundFact[items_.size() + 1];
        currentFacts_[0] = processor.processFact(staticFact);
        changeLevels_ = items_.size();
    }
    
    public GroundFact[] next()
    {
        prepareFacts();
        prepareNextIndeces();
        return currentFacts_;
    }
    
    private void prepareNextIndeces()
    {
        changeLevels_ = 0;
        incrementIndex(currentIndeces_.length - 1);
    }
    
    private void incrementIndex(int index)
    {
        if(index <= 0)
        {
            endOfIteration_ = true;
            return;
        }
        changeLevels_++;
        int newIndexValue = currentIndeces_[index] + 1;
        if(newIndexValue >= items_.get(index-1).size())
        {
            currentIndeces_[index] = 0;
            incrementIndex(index - 1);
        }
        else
        {
            currentIndeces_[index] = newIndexValue;
        }
    }
    
    private void prepareFacts()
    {
        for(int i=0; i<changeLevels_; i++)
        {
            int index = currentFacts_.length - 1 - i;
            currentFacts_[index] = processor_.processFact(items_.get(index-1).get(currentIndeces_[index]));
        }
    }
    
    public boolean hasNext()
    {
        return !endOfIteration_;
    }
    
    public Iterator<GroundFact[]> iterator()
    {
        return this;
    }
    
    public void remove()
    {

    }
}
