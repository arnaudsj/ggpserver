package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.ArrayList;
import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.astvisitors.AbstractVisitor;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

/**
 * 
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 *
 */
public class Atom extends Term {
    
    /**
     *  content of this atom 
     */
	final String literal;
    final int    hashCode;
    
    /**
     * The very only constructor for an atom, since
     * no atoms should be possible without their
     * string parameter.
     * 
     * @param string Literal of newly created atom.
     */
	public Atom(String string) {
		literal  = string.toUpperCase();
        hashCode = literal.hashCode();
	}

    /**
     * @return returns literal propery of current atom.
     */
    @Override
    public final String toString() {
        return literal;
    }

    /**
     * @return Returns an empty list. Literals do not contain variables ;).
     */
    @Override
    public final List<Variable> getVariables() {
        return new ArrayList<Variable>();
    }

    /**
     * @param sigma Substitution to apply
     * @return Returns the literal because atoms are immune to
     *         any subsitutions.
     */
    @Override
    public final Expression apply(Substitution sigma) {
        return this;
    }

    /**
     * 
     * @param obj Object to compare to.
     * @return Returns if supplied object is an atom with
     *         equal literal.
     */
    @Override
    public final boolean equals(Object obj) {
        return (obj instanceof Atom) && (hashCode == obj.hashCode());
    }

    /**
     * 
     * 
     * @param sigma
     * @param scope
     * @param flag
     * @return
     * @throws InterruptedException
     */
    @Override
    protected FuzzyResolution fuzzyEvaluateBody( FuzzySubstitution sigma,
                                                 GameStateScope    scope, 
                                                 List<Expression>  guard,
                                                 TimerFlag         flag ) 
    throws InterruptedException {
                        
        FuzzyResolution sigmas = null;
        if (scope.isProven( this )){
            sigmas = new FuzzyResolution();
            sigmas.addAll( scope.getProvenForFuzzy( sigma, this ) );
            sigmas.setFuzzyValue( Expression.fuzzyOne );            
            return sigmas;
        } else if (scope.isFuzzylyEvaluated(this)){
            return scope.getFuzzyResolutionStage( sigma, this );
        } else if (guard.contains( this )){
            sigmas = new FuzzyResolution();
            sigmas.add( sigma );
            sigmas.setFuzzyValue( Expression.fuzzyZero );            
            return sigmas;            
        }

        int size = guard.size();
        guard.add( size, this);
        sigmas = fuzzyEvaluateSimilarExpressions( sigma, scope, guard, flag );
        guard.remove( size );
        scope.setFuzzyEvaluationStage( this, sigmas);
        return sigmas;
    }
    
    /**
     * 
     * @param sigma
     * @param sigmas
     * @param scope
     * @param flag
     * @return
     * @throws InterruptedException
     */
    private FuzzyResolution fuzzyEvaluateSimilarExpressions(
            FuzzySubstitution sigma, GameStateScope scope, 
            List<Expression> guard, TimerFlag flag) 
    throws InterruptedException {        
        /*
         * A trick about 'resolved' is that if
         * some predicate at some point is resolved,
         * we want to stop calculating fuzzy value for it.
         * However, we would like to keep collecting successful 
         * substitutions. Therefore 'resolved' acts as a flag,
         * indicating that no modifications to fuzzy value of 
         * current predicate should be done from now on.
         * 
         * For second issue consider following rules:
         * (<= (somepred ?x)
         *     (succ ?x ?y)
         *     (somepred ?y)
         * )
         * (somepred 1)
         * (succ 2 1)
         * (succ 3 2)
         * (succ 4 3)
         * The rules of 'somepred' kind should be treated as if they are 
         * combined with a T-Conorm (fuzzy conjunction),
         * whereas the rules of 'succ' kind should result only to fuzzy
         * truth or fuzzy false (no fuzzy conjunciton is assumed)
         * 
         * Moreover if some candidate (disreguarding which kind) evaluates to 
         * fuzzy true (higher than a threashold), fuzzy true should be
         * returned as the result. 
         */
        ExpressionList candidates = scope.getSimilarExpressions(this);
        FuzzyResolution sigmas = new FuzzyResolution();

        for (Expression e : candidates ) {
            FuzzyResolution fuzzy = null;        

            if ( e instanceof Implication ) {
                Implication impl = (Implication) e;
                ExpressionList prem = impl.getPremises();
                
                if ( flag.interrupted() ) throw Const.interrupt;

                fuzzy = prem.fuzzyEvaluate( sigma, scope, guard, flag );
                sigmas.addAlternativeResolution( fuzzy );
            }
        }
        scope.setFuzzyEvaluationStage(this, sigmas);
        return sigmas;
    } 
    
    /**
     * Basically there are only two successful cases:
     * <ul>
     *     <li>either supplied expression is the same atom 
     *        (in this case no modification are done to supplied
     *        substitution)</li>
     *     <li>or supplied expression is a variable 
     *         (in this case variable is asked to find MGU
     *         with current atom)</li>
     * </ul>
     * All other cases default to unsuccessful unification.
     * 
     * @param target Expression to unify with.
     * @param sigma Substitution to consider while unification.
     * @return Returns most general unifier substitution if 
     *         such exists.
     */
    @Override
    public final Substitution mgu (Expression target, Substitution sigma) {

        if ( target instanceof Atom && target.equals(this) ) 
            return sigma;
        
        return (target instanceof Variable) ? 
                                          ((Variable) target).mgu(this, sigma) : 
                                          null;
    }

    /**
     * 
     * @param var Variable to search for.
     * @return Defaults to false, since there are no 
     *         variables that could occure in a literal ;-).
     */
    @Override
    public final boolean isPresent(Variable var) {
        return false;
    }

    @Override
    public final int hashCode() {
        return hashCode;
    }

    @Override
    public final boolean isGround() {
        return true;
    }

    @Override
    public final Atom getKeyAtom() {
        return this;
    }

    @Override
    public final void processVisitor(AbstractVisitor visitor) {
        visitor.visitAtom(this);
    }

}
