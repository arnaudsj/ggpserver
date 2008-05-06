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

import java.util.Map;

import stanfordlogic.gdl.GdlExpression;

/**
 * A fact is something true in a knowledge base. A fact has a relation name,
 * followed by zero or more columns (parameters to the relation).
 */
public abstract class Fact extends Expression
{
    /** The name of the relation. */
    final protected int relationName_;
    
    /** The columns of the relation. */
    protected Term [] terms_;
    
    protected Fact(int relName)
    {
        relationName_ = relName;
    }
    
    public int getRelationName()
    {
        return relationName_;
    }
    
    protected Iterable<Implication> getRules(AbstractReasoner r, ProofContext context)
    {
        return r.getRules(this);
    }
    
    /**
     * Get the arity of this fact. That is, the number of columns of the relation.
     * 
     * @return The arity of this fact.
     */
    public int getArity()
    {
        return terms_.length;
    }
    
    public Term getTerm(int whichOne)
    {
        return terms_[whichOne];
    }
    
    public abstract boolean hasOnlyTermObjects();
    
    @Override
    public abstract Fact applySubstitution(Substitution sigma);
    
    @Override
    public abstract Fact uniquefy(Map<TermVariable, TermVariable> varMap);

    @Override
    public boolean hasTermFunction( int functionName )
    {
        for ( Term t : terms_ )
        {
            if ( t.hasTermFunction(functionName) )
                return true;
        }
        
        return false;
    }
    
    @Override
    public boolean hasTermVariable( int varName )
    {
        for ( Term t : terms_ )
        {
            if ( t.hasVariable(varName) )
                return true;
        }
        
        return false;
    }

    /**
     * Attempt to unify this fact with <tt>fact</tt>. If the unification
     * succeeds, return the substitution used -- but does not actually change
     * the facts. If the unification fails, return null.
     * 
     * <p>
     * Note that before attempting to unify with <tt>fact</tt>, you probably
     * want to make sure that the facts have unique variable names.
     * 
     * @param fact
     *            The fact to unify with.
     * @return The substitution used to unify these, or null.
     */
    public Substitution unify(Fact fact)
    {
        //fact = fact.uniquefy();
        
        Substitution sigma = Unifier.mgu(this, fact);
        
        return sigma;
    }
    
    /**
     * Construct a fact, ground or variable, from a GdlExpression. Returns a
     * GroundFact if there were no variables in the GdlExpression, and a
     * VariableFact otherwise.
     * 
     * @param exp The expression from which to construct the fact.
     * @return The fact constructed from GdlExpression <tt>exp</tt>.
     */
    static public Fact fromExpression(GdlExpression exp)
    {
        // When in doubt, it's probably a variable fact.
        // Besides, the variable fact factory takes care of turning
        // things into ground facts if there are no variables.
        
        return VariableFact.fromExpression(exp);
    }
}
