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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import stanfordlogic.gdl.SymbolTable;


/**
 * An implication states that its <i>head</i> (consequent) is true when the
 * <i>antecedents</i> are true.
 */
public class Implication extends Expression
{
    private final Fact consequent_;
    private final Conjunction antecedents_;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.prover");
    
    final static private Conjunction EMPTY_ANTECEDENTS = new Conjunction();
    
    public Implication(Fact head, Expression ... conjuncts )
    {
        this(true, head, conjuncts);
    }
    
    public Implication(boolean clone, Fact head, Expression ... conjuncts)
    {
        consequent_ = head;
        
        if ( conjuncts == null )
            antecedents_ = EMPTY_ANTECEDENTS;
        else
            antecedents_ = new Conjunction(clone, conjuncts);
    }
    
    public Implication(Fact head, Conjunction conjuncts)
    {
        consequent_ = head;
        if ( conjuncts == null )
            antecedents_ = EMPTY_ANTECEDENTS;
        else
            antecedents_ = conjuncts;
    }
    
    /**
     * Can this rule apply to a fact? True if the rule's consequent has the same
     * fact name and the same arity. Note that a rule can apply to fact, without
     * being unifiable with it.
     * 
     * @param f The fact to check application for.
     * @return True if this rule applies to the fact.
     */
    public boolean canApplyTo(Fact f)
    {
        return consequent_.relationName_ == f.relationName_
            && consequent_.getArity() == f.getArity();
    }
    
    public Fact getConsequent()
    {
        return consequent_;
    }
    
    public Conjunction getAntecedents()
    {
        return antecedents_;
    }
    
    public int numAntecedents()
    {
        return antecedents_.numConjuncts();
    }
    
    public Implication uniquefy()
    {
        Map<TermVariable, TermVariable> varMap = new HashMap<TermVariable, TermVariable>();
        
        Fact newHead = (Fact) consequent_.uniquefy(varMap);
        Conjunction newConjuncts = (Conjunction) antecedents_.uniquefy(varMap);

        return new Implication(newHead, newConjuncts);
    }

    @Override
    public Expression applySubstitution(Substitution sigma)
    {
        Fact newHead = consequent_.applySubstitution(sigma);
        Conjunction newConjuncts = antecedents_.applySubstitution(sigma);
        
        return new Implication(false, newHead, newConjuncts);
    }

    @Override
    public boolean canMapVariables(Expression other)
    {
        if (other instanceof Implication == false) {
            return false;
        }
        
        Implication impl = (Implication) other;
        
        if (impl.consequent_.relationName_ != consequent_.relationName_) {
            return false;
        }
        if (impl.antecedents_.numConjuncts() != antecedents_.numConjuncts()) {
            return false;
        }
        
        Map<TermVariable, TermVariable> varMappings = new HashMap<TermVariable, TermVariable>();
        
        // First, check the heads' terms
        for (int i = 0; i < consequent_.getArity(); i++)
        {
            Term t1 = consequent_.getTerm(i);
            Term t2 = impl.consequent_.getTerm(i);
            
            if ( t1.canMapVariables(t2, varMappings) == false )
                return false;
        }
        
        // TODO: implement the rest of Implication.canMapVariables
        // (we don't actually need to use this, I think)
        
        logger_.severe("WARNING: Implication.canMapVariables not implemented");
        
        return false;
    }
    

    @Override
    public boolean hasTermFunction(int functionName)
    {
        if (consequent_.hasTermFunction(functionName)) {
            return true;
        }
        
        for (Expression exp: antecedents_.getConjuncts()) {
            if (exp.hasTermFunction(functionName)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean hasTermVariable(int varName)
    {
        if (consequent_.hasTermVariable(varName)) {
            return true;
        }
        
        for (Expression exp: antecedents_.getConjuncts()) {
            if (exp.hasTermVariable(varName)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void printToStream(PrintStream target, SymbolTable symtab)
    {
        target.print("(<= ");
        consequent_.printToStream(target, symtab);
        target.print(" ");
        antecedents_.printToStream(target, symtab);
        target.print(")");
    }

    @Override
    public Expression uniquefy(Map<TermVariable, TermVariable> varMap)
    {
        Fact newHead = consequent_.uniquefy(varMap);
        Conjunction newConjuncts = antecedents_.uniquefy(varMap);
        
        return new Implication(false, newHead, newConjuncts);
    }
    
    
    
}
