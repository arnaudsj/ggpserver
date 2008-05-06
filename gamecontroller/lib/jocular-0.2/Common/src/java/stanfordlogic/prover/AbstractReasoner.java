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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import stanfordlogic.gdl.Parser;
import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.knowledge.KnowledgeBase;


/**
 *
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public abstract class AbstractReasoner
{
    final protected KnowledgeBase kb_;
    protected Map<Integer, List<Implication>> rules_;
    protected Implication [] rulesArray_;
    
    final protected Parser      parser_;
    final protected SymbolTable symbolTable_;
    final protected static Substitution EMPTY_SUB = new Substitution();
    final protected static List<Substitution> EMPTY_SUB_LIST =
            new ArrayList<Substitution>(Arrays.asList(new Substitution [] { EMPTY_SUB }));
    
    //////////////////////////////////
    //  Flags for optimization etc. //
    //////////////////////////////////
    
    // TODO think of some optimizations :-)
    
    
    /////////////////////////////////////////////////////////////////////////////
    
    /**
     * Create a reasoner using <tt>kb</tt> as static knowledge.
     * 
     * <p>This constructor takes caree of the common initialization. It is private,
     * and so only callable by constructors exposed to subclasses.
     * 
     * <p><i>Note</i>: we use the parser, and not just the symbol table, to
     * have quick access to the reserved tokens.
     * 
     * @param kb The static knowledge.
     * @param p  The parser whose symbol table to refer to.
     */
    private AbstractReasoner(KnowledgeBase kb, Parser p)
    {
        kb_ = kb;
        parser_ = p;
        symbolTable_ = p.getSymbolTable();
    }
    
    /**
     * Create a reasoner using <tt>kb</tt> for static knowledge and <tt>rules</tt> for
     * the collection of knowledge rules.
     * 
     * @param kb The static knowledge.
     * @param rules The rules indexed by the head's relation name.
     * @param p The parser whose symbol table to refer to.
     */
    protected AbstractReasoner(KnowledgeBase kb, Map<Integer, List<Implication>> rules, Parser p)
    {
        this(kb, p);
        rules_ = rules;
        
        List<Implication> allRules = new ArrayList<Implication>();
        for (List<Implication> r : rules.values())
            allRules.addAll(r);
        rulesArray_ = allRules.toArray( new Implication [] {} );
    }
    
    /**
     * Create a reasoner using <tt>kb</tt> for static knowledge and <tt>rules</tt> for
     * the collection of knowledge rules. Converts the rules list to an index mapping
     * rule head relation name to list of rules with that name as the head. 
     * 
     * @param kb The static knowledge.
     * @param rules The list of rules.
     * @param p The parser whose symbol table to refer to.
     */
    protected AbstractReasoner(KnowledgeBase kb, List<Implication> rules, Parser p)
    {
        this(kb, p);
        
        rulesArray_ = rules.toArray( new Implication [] {} );
        
        rules_ = new TreeMap<Integer, List<Implication>>();
        
        // Construct the indexed rule table
        for ( Implication r : rules )
        {
            int relName = r.getConsequent().getRelationName();
            
            List<Implication> sublist = rules_.get(relName);
            
            if ( sublist == null )
            {
                sublist = new ArrayList<Implication>();
                rules_.put(relName, sublist);
            }
            
            sublist.add(r);
        }
    }
    
    final public SymbolTable getSymbolTable()
    {
        return symbolTable_;
    }
    
    public GroundFact getAnAnswer(Fact question)
    {
        ProofContext context = ProofContext.makeDummy(parser_);
        return getAnAnswer(question, context);
    }
    
    public List<GroundFact> getAllAnswers(Fact question)
    {
        ProofContext context = ProofContext.makeDummy(parser_);
        return getAllAnswers(question, context);
    }
    
    /**
     * Try to prove a single fact. If provable, returns what was proved;
     * if not provable, returns <tt>null</tt>.
     * 
     * <p>For example, if you pass in a variable fact, such as (legal xplayer ?x),
     * you will get back either <tt>null</tt> or (e.g.) (legal xplayer noop).
     * 
     * @param question The question ('fact') to prove.
     * @param context
     * @return
     */
    abstract public GroundFact getAnAnswer(Fact question, ProofContext context);
    abstract public List<GroundFact> getAllAnswers(Fact question, ProofContext context);
    
    abstract public Iterable<GroundFact> getAllAnswersIterable(Fact question, ProofContext context);
    
    
    /**
     * Return true if <tt>ground</tt> is true in the current proof context.
     * 
     * @param ground
     *            The ground to check for.
     * @param context
     *            The proof context in which to find the ground. (Provides
     *            additional knowledge.)
     * @return True if the reasoner can find a proof (i.e. existence) for
     *         <tt>ground</tt>.
     */
    protected boolean findGround( GroundFact ground, ProofContext context )
    {
        // Special case for distinct
        if (isDistinctFact(ground)) {
            return checkDistinct(ground);
        }
        
        return kb_.isTrue(ground) || context.isTrue(ground);
    }
    
    protected List<Substitution> getUnifiableGrounds(VariableFact varFact, ProofContext context)
    {
        List<Substitution> grounds = kb_.getUnifiable(varFact);
        
        List<Substitution> moreUnifiable = context.getUnifiableGrounds(varFact);
        
        if (moreUnifiable != null) {
            grounds.addAll(moreUnifiable);
        }
        
        return grounds;
    }
    
    /**
     * Check if two terms are distinct. (The terms checked are columns 0 and 1
     * of the argument.)
     * 
     * @param ground The distinct relation.
     * 
     * @return True if the two terms are distinct.
     */
    protected boolean checkDistinct(GroundFact ground)
    {
        if ( ground.getTerm(0).equals(ground.getTerm(1)) )
            return false;
        else
            return true;
    }
    
    
    final private static List<Implication> EMPTY_RULE_LIST = new ArrayList<Implication>(0);
    
    /**
     * Get all rules that have fact <tt>f</tt> at their head.
     * 
     * @param f Fact for which to find rules.
     * @return An array of rules with fact <tt>f</tt> at their head.
     */
    public List<Implication> getRules(Fact f)
    {
        List<Implication> rules;
        
        rules = rules_.get(f.getRelationName());

        if ( rules == null )
            return EMPTY_RULE_LIST;
        else
            return rules;
    }
    
    public boolean isDistinctFact(Fact f)
    {
        return f.getRelationName() == parser_.TOK_DISTINCT;
    }
    
    public boolean isDoesFact(Fact f)
    {
        return f.getRelationName() == parser_.TOK_DOES;
    }

}
