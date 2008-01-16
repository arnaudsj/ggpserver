package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is meant to be a container for all constants of this framework.
 * 
 * @author ingo
 *
 */
public final class Const {

    // Constant values from GameMaster
    public static final int MAX_GOAL = 100;
    public static final int MIN_GOAL = 0;

    // Variable, list containing only that var
    public static final Variable       vX       = new Variable      ( "?X" );
    public static final ExpressionList varListX = new ExpressionList( vX   );
    
    // Atoms for unary predicates
    public static final Atom aRole  = new Atom( "ROLE"     );
    public static final Atom aInit  = new Atom( "INIT"     );
    public static final Atom aTrue  = new Atom( "TRUE"     );
    public static final Atom aNext  = new Atom( "NEXT"     );
    public static final Atom aNoop  = new Atom( "NOOP"     );
    public static final Atom aLegal = new Atom( "LEGAL"    );
    public static final Atom aDoes  = new Atom( "DOES"     );
    public static final Atom aGoal  = new Atom( "GOAL"     );
    public static final Atom aTerm  = new Atom( "TERMINAL" );
    
    // Unary predicates (not dependent on Player) get fixed reps
    public static final Predicate pRoles = new Predicate( aRole, varListX );
    public static final Predicate pTrue  = new Predicate( aTrue, varListX );
    public static final Predicate pNext  = new Predicate( aNext, varListX );
    public static final Predicate pInit  = new Predicate( aInit, varListX );
    public static final Predicate pGoal  = new Predicate( aGoal, varListX );
    public static final Predicate pRole  = new Predicate( aRole, vX       );
    
    // common operators
    public static final Atom     aVarAtom    = new Atom( "*"        );
    public static final Atom     aImpOp      = new Atom( "<="       );
    public static final Atom     aNotOp      = new Atom( "NOT"      );
    public static final Atom     aAndOp      = new Atom( "AND"      );
    public static final Atom     aOrOp       = new Atom( "OR"       );
    public static final Atom     aDistinctOp = new Atom( "DISTINCT" );
    
    public static final List<Substitution> emptySubstitutionList = 
                                                  new ArrayList<Substitution>();
    public static final Atom aDummy = new Atom ("found");
    
    public static final InterruptedException interrupt = new InterruptedException();
    
    public static final List<Atom> gdlRelations = new ArrayList<Atom>();
    
    static {
        gdlRelations.add(aRole);
        gdlRelations.add(aTrue);
        gdlRelations.add(aNext);
        gdlRelations.add(aInit);
        gdlRelations.add(aGoal);
        gdlRelations.add(aTerm);
        gdlRelations.add(aLegal);
        gdlRelations.add(aDoes);
    }
}