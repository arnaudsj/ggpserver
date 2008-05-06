///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

package stanfordlogic.game;

import java.util.List;
import java.util.Random;

import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.Parser;
import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.prover.AbstractReasoner;
import stanfordlogic.prover.Fact;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.ProofContext;
import stanfordlogic.prover.VariableFact;


public class ReasoningEntity
{
    /** Parser */
    final protected Parser              parser_;
    
    /** Symbol table */
    final protected SymbolTable         symbolTable_;
    
    /** Reasoner */
    protected AbstractReasoner          reasoner_;
    
    /** Random generator */
    protected Random                    random_;
    
    final protected Fact QUERY_TERMINAL;
    final protected Fact QUERY_NEXT;

    protected ReasoningEntity(Parser parser)
    {
        parser_ = parser;
        symbolTable_ = parser_.getSymbolTable();
        
        QUERY_TERMINAL = makeQuery("terminal");
        QUERY_NEXT = makeQuery("next", "?x");
        
        random_ = new Random();
    }
    
    public AbstractReasoner getReasoner()
    {
        return reasoner_;
    }
    
    protected Fact makeQuery(String ... args)
    {
        GdlList list = GdlList.buildFromWords(symbolTable_, args);
        Fact query = VariableFact.fromList(list);
            
        return query;
    }
    
    /**
     * Wrapper around Reasoner#getAllAnswers
     * 
     * @param context
     *            The context to be used in the proof. Contains volatile data,
     *            cache, etc.
     * @param args
     *            The question (query) as a list of words
     * 
     * @return a list of facts answering the input query
     * @see camembert.knowledge.reasoner.getAllAnswers
     */
    protected List<GroundFact> getAllAnswers(ProofContext context, String ... args)
    {
        Fact question = makeQuery(args);
        return reasoner_.getAllAnswers(question, context);
    }
    
    protected Iterable<GroundFact> getAllAnswersIterable(ProofContext context, String ... args)
    {
        Fact question = makeQuery(args);
        return reasoner_.getAllAnswersIterable(question, context);
    }
    
    /**
     * Wrapper around Reasoner#getAnAnswer
     * 
     * @param context
     *            The context to be used in the proof. Contains volatile data,
     *            cache, etc.
     * @param args
     *            The question (query) as a list of words
     *            
     * @return a fact answering the input query
     * @see camembert.knowledge.reasoner.getAnAnswer
     */
    protected GroundFact getAnAnswer(ProofContext context, String ... args)
    {
        Fact question = makeQuery(args);
        return reasoner_.getAnAnswer(question, context);
    }
    
}
