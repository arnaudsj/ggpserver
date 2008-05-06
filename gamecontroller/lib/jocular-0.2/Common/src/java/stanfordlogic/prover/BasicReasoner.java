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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import stanfordlogic.prover.Implication;
import stanfordlogic.prover.Conjunction;
import stanfordlogic.prover.Substitution;

import stanfordlogic.gdl.Parser;
import stanfordlogic.knowledge.KnowledgeBase;
import stanfordlogic.game.GameManager;


/**
 *
 */
public class BasicReasoner extends AbstractReasoner
{
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.prover");
    
    public BasicReasoner(KnowledgeBase kb, Map<Integer, List<Implication>> rules, Parser p)
    {
        super(kb, rules, p);
    }
    
    public BasicReasoner(KnowledgeBase kb, List<Implication> rules, Parser p)
    {
        super(kb, rules, p);
    }
    
    @Override
    public List<GroundFact> getAllAnswers(Fact f, ProofContext context)
    {
        long startTime = System.nanoTime();
        
        List<GroundFact> answers = new ArrayList<GroundFact>();
        
        logger_.fine(" ---------- Beginning new proof request (multi) ----------");
        
        List<Substitution> subs = proveFact(f, context, true);
        
        if(subs != null)
        {        
            for(Substitution sub : subs)
            {
                GroundFact res = (GroundFact) f.applySubstitution(sub);
                
                if(!answers.contains(res))
                    answers.add(res);
            }
        }
        
        GameManager.addTime(GameManager.TIME_GET_ALL_ANSWERS, System.nanoTime() - startTime);
        
        return answers;
    }
    
    @Override
    public Iterable<GroundFact> getAllAnswersIterable( Fact question, ProofContext context )
    {
        return getAllAnswers(question, context);
    }

    
    @Override
    public GroundFact getAnAnswer(Fact f, ProofContext context)
    {
        long startTime = System.nanoTime();
        
        logger_.fine(" ---------- Beginning new proof request (single) ----------");
        
        Substitution s = proveOne(f, context);

        GameManager.addTime(GameManager.TIME_GET_AN_ANSWER, System.nanoTime() - startTime);
        
        // No proof?
        if (s == null)
            return null;
        
        // Proof: apply and return.
        return (GroundFact) f.applySubstitution(s);
    }

    /////////////////////////////////////
    // IMPLEMENTATION OF PROOF SYSTEM: //
    /////////////////////////////////////
    
    private List<Substitution> prove(Expression exp, ProofContext context, boolean proveAll)
    {
        if (exp instanceof Fact) {
            return proveFact((Fact) exp, context, proveAll);
        }
        else if (exp instanceof Conjunction) {
            return proveConjunction((Conjunction) exp, context, proveAll);
        }
        else if (exp instanceof Negation) {
            return proveNegation((Negation) exp, context, proveAll);
        }
        else if (exp instanceof Disjunction) {
            return proveDisjunction((Disjunction) exp, context, proveAll);
        }
        else {
            logger_.severe("Cannot prove expression of type " + exp.getClass().getName());
            return null;
        }
    }
    
    private Substitution proveOne(Expression exp, ProofContext context)
    {
        List<Substitution> proofs = prove(exp, context, false);
        
        if (proofs == null) {
            return null;
        }
        else
        {
            if (proofs.size() > 1) {
                logger_.warning("proveAll with getAll==false returned >1 results");
            }
            
            return proofs.get(0);
        }
    }
    
    private List<Substitution> proveFact(Fact f, ProofContext context, boolean proveAll)
    {
        context.enterProof(f, proveAll);
        
        // Special case for distinct.
        if (isDistinctFact(f))
        {
            if (checkDistinct((GroundFact) f))
            {
                // Success
                context.exitProof(f, true, EMPTY_SUB);
                return EMPTY_SUB_LIST;
            }
            else
            {
                // Failure
                context.exitProof(f, false, EMPTY_SUB);
                return null;
            }
        }
        
        List<Substitution> results = new ArrayList<Substitution>();
        
        // If this is a ground fact, just check if it's in our KB
        if (f instanceof GroundFact)
        {
            if (findGround((GroundFact) f, context))
            {
                context.exitProof(f, true, EMPTY_SUB);
                return EMPTY_SUB_LIST;
            }
        }
        
        // Otherwise, try to find all things unifiable with it
        else
        {
            Iterable<Substitution> similar = getUnifiableGrounds((VariableFact) f, context);
            
            for ( Substitution u: similar )
            {
                // Success!
                results.add(u);
                
                // Should we stop here?
                if (!proveAll) {
                    context.exitProof(f, true, u);
                    return results;
                }
            }
        }
        
        // OK, we also need to check for rules that can be applied.
        
        Iterable<Implication> rules = getRules(f);

        for ( Implication rule : rules )
        {
            rule = rule.uniquefy();

            Substitution unification = f.unify(rule.getConsequent());

            // Stop if unification fails.
            if ( unification == null )
                continue;

            context.reportRuleHead(rule.getConsequent(), unification);

            Conjunction conjuncts =
                    (Conjunction) rule.getAntecedents().applySubstitution(unification);

            List<Substitution> ruleResults = proveConjunction(conjuncts, context, proveAll);

            if ( ruleResults != null )
            {
                for (Substitution sub: ruleResults)
                {
                    Substitution s = unification.copy(sub);

                    results.add(s);
                    
                    if (!proveAll) {
                        // we can just stop here.
                        context.exitProof(f, true, s);
                        return results;
                    }
                }
            }
        }
        
        
        if ( results.size() == 0 )
        {
            // Failure.
            context.exitProof(f, false, results);
            return null;
        }
        else
        {
            // Success.
            context.exitProof(f, true, results);
            return results;
        }
    }
    
    private List<Substitution> proveConjunction(Conjunction conjuncts, ProofContext context, boolean getAll)
    {
        context.enterProof(conjuncts, true);
        
        List<Substitution> overallSubs = new ArrayList<Substitution>();
        overallSubs.add(EMPTY_SUB);
        
        List<Substitution> levelSubs = new ArrayList<Substitution>();
        List<Substitution> tempSubs;
        
        Expression [] sentences = conjuncts.getConjuncts();
        
        for ( int i = 0; i < sentences.length; i++ )
        {
            for(Substitution currentSub : overallSubs)
            {
                tempSubs = prove(sentences[i].applySubstitution(currentSub), context, true);
                
                if(tempSubs != null)
                {
                    for ( Substitution sub : tempSubs )
                        levelSubs.add( currentSub.copy(sub) );
                }
            }

            if(levelSubs.size() == 0)
            {
                overallSubs.clear();
                context.exitProof(conjuncts, false, overallSubs);
                return null;
            }
            
            overallSubs.clear();
            overallSubs.addAll(levelSubs);
            levelSubs.clear();
        }

        context.exitProof(conjuncts, true, overallSubs);
        return overallSubs;
    }
    
    private List<Substitution> proveNegation(Negation s, ProofContext context, boolean proveAll)
    {
        context.enterProof(s, proveAll);
        
        Substitution result = proveOne(s.getNegated(), context);
        
        // Make sure we *failed* the proof.
        if ( result == null ) {
            List<Substitution> res = new ArrayList<Substitution>(1);
            res.add(EMPTY_SUB);
            context.exitProof(s, false, EMPTY_SUB);
            return res;
        }
        else {
            return null;
        }
    }
    
    private List<Substitution> proveDisjunction(Disjunction s, ProofContext context, boolean proveAll)
    {
        context.enterProof(s, proveAll);
        
        List<Substitution> results;
        
        results = new ArrayList<Substitution>();
        
        Expression [] disjuncts = s.getDisjuncts();
        
        for ( Expression disjunct : disjuncts )
        {
            List<Substitution> result = prove(disjunct, context, proveAll);
            
            if ( result != null )
            {
                results.addAll(result);
                
                // should we stop here?
                if (!proveAll) {
                    context.exitProof(s, true, result);
                    return results;
                }
            }
        }
        
        if ( results.size() == 0 )
        {
            context.exitProof(s, false, results);
            return null;
        }
        else
        {
            context.exitProof(s, true, results);
            return results;
        }
    }
}
