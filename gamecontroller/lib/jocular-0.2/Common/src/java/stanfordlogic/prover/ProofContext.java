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


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import stanfordlogic.gdl.Parser;
import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.knowledge.KnowledgeBase;
import stanfordlogic.util.Util;


/**
 * A context in which proofs are made. In addition to the static knowledge that
 * the reasoner uses (and which does not change proof by proof) the reasoner
 * needs to deal with current knowledge, which is volatile.
 * 
 * <p><b>NOTE</b>: the proof debugging is not threadsafe! 
 */
public class ProofContext
{
    private KnowledgeBase volatileKb_;
    
    final protected Parser parser_;
    final protected SymbolTable symbolTable_;
    
    private int proofLevel_ = 0;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.prover");
    
    public ProofContext(KnowledgeBase volatileKb, Parser p)
    {
        volatileKb_ = volatileKb;
        parser_ = p;
        symbolTable_ = p.getSymbolTable();
    }
    
    public KnowledgeBase getVolatileKb()
    {
        return volatileKb_;
    }
    
    public void setVolatileKb(KnowledgeBase kb)
    {
        volatileKb_ = kb;
    }
    
    public boolean isDistinctGround(GroundFact g)
    {
        return g.getRelationName() == parser_.TOK_DISTINCT;
    }
    
    public boolean isDoesFact(Fact f)
    {
        return f.getRelationName() == parser_.TOK_DOES;
    }
    
    protected void enterProof(Expression s, boolean all)
    {
        if (logger_.isLoggable(Level.FINER))
        {
            String str = s.toString(symbolTable_);

            String find;

            if (all) {
                find = "Find all: ";
            }
            else {
                find = "Find: ";
            }

            logger_.finer("Reasoner: " + Util.makeIndent(proofLevel_) + find + str);

            if (s instanceof GroundFact == false)
                logger_.finer("Reasoner: " + Util.makeIndent(proofLevel_) + "{");

            proofLevel_++;
        }
    }
    
    protected void exitProof( Expression s, boolean success, Substitution workingSubst )
    {
        if (logger_.isLoggable(Level.FINER))
        {
            proofLevel_--;
            
            if (proofLevel_ < 0) {
                throw new RuntimeException("Proof level went below zero!!!");
            }
            
            String substString;
            if (!success) {
                substString = "";
            }
            else {
                substString = workingSubst.toString( symbolTable_ );
            }
            
            if ( s instanceof GroundFact == false ) {
                logger_.finer( "Reasoner: " + Util.makeIndent(proofLevel_) + "}");
            }
            logger_.finer( "Reasoner: " + Util.makeIndent( proofLevel_ ) + "Exit: "
                    + success + " " + substString );
        }
    }
    
    protected void reportRuleHead(Expression s, Substitution sigma)
    {
        if (logger_.isLoggable(Level.FINER))
        {
            s = s.applySubstitution(sigma);

            String str = s.toString(symbolTable_);

            logger_.finer("Reasoner: " + Util.makeIndent(proofLevel_)
                                 + "Rule head: " + str + ", mgu = " + sigma);
        }
    }
    
    protected void reportResult( Expression s, Substitution sub )
    {
        if (logger_.isLoggable(Level.FINER))
        {
            StringBuilder sb = new StringBuilder();
            
            sb.append( sub.toString() );
            
            logger_.finer( "Reasoner: " + Util.makeIndent( proofLevel_ )
                    + "Result for " + s + ": " + sb.toString() );
        }
    }
    
    protected void exitProof(Expression s)
    {
        if (logger_.isLoggable(Level.FINER))
        {
            proofLevel_--;
            
            if (proofLevel_ < 0) {
                throw new RuntimeException("Proof level went below zero!!!");
            }
            
            if ( s instanceof GroundFact == false ) {
                logger_.finer("Reasoner: " + Util.makeIndent(proofLevel_) + "}");
            }
            
            logger_.finer("Reasoner: " + Util.makeIndent(proofLevel_) + "Exit.");
        }
    }
    
    protected void exitProof( Expression s, boolean success, List<Substitution> workingSubsts )
    {
        if (logger_.isLoggable(Level.FINER))
        {
            proofLevel_--;
            
            if (proofLevel_ < 0) {
                throw new RuntimeException("Proof level went below zero!!!");
            }
            
            StringBuilder sb = new StringBuilder();
            
            if ( success )
            {
                for ( Substitution sigma : workingSubsts )
                {
                    sb.append( sigma.toString() );
                    sb.append( " " );
                }
            }
            
            if ( s instanceof GroundFact == false )
                logger_.finer( "Reasoner: " + Util.makeIndent(proofLevel_) + "}");
            
            logger_.finer( "Reasoner: " + Util.makeIndent( proofLevel_ ) + "Exit: "
                    + success + " " + sb.toString() );
        }
    }
    
    protected boolean isTrue(GroundFact fact)
    {
        if (volatileKb_ == null) {
            return false;
        }
        return volatileKb_.isTrue(fact);
    }
    
    protected List<Substitution> getUnifiableGrounds(VariableFact varFact)
    {
        if (volatileKb_ == null) {
            return null;
        }
        
        List<Substitution> grounds = volatileKb_.getUnifiable(varFact);
        
        return grounds;
    }
    
    
    
    
    /**
     * Create a dummy proof context with an empty volatile knowledge base.
     * @param p
     * @return
     */
    public static ProofContext makeDummy(Parser p)
    {
        ProofContext result = new ProofContext(null, p);
        
        return result;
    }
}
