package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * This class is a special HashMap implementation that contains the relation
 * between a variable and a term commonly known as a substitution.<br>
 * <br>
 * Definition: { v_1/t_1, v_2/t_2, ..., v_m/t_m },<br>
 *             t_i - Expression,<br>
 *             v_i - Variable<br>
 * <br>
 * In this HashMap the key is v_i and the value is t_i.<br>
 * <br>
 * Following method should be used:<br>
 * <ul>
 *   <li>
 *     containsKey() to check if substitution contains a variable mapping
 *   </li>
 *   <li>
 *     get() to get a expression for a variable
 *   </li>
 *   <li>
 *     isUnificator() to check if a substitution is a unificator
 *   </li>
 * </ul>
 *
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 *
 */
public class Substitution extends HashMap<Variable, Expression> {

    private static final long serialVersionUID = 8066780545882482804L;

    /* Stores the logger for this class */
    public static Logger logger = Logger.getLogger(Substitution.class);

    /**
     * Constructs a new empty substitution object.
     */
    public Substitution () {
        super();
    }

    /**
     * Constructs a new subtitution object based on the copy substitution.
     * @param copy Substitution
     */
    public Substitution ( Substitution copy ) {
        super();
        this.putAll( copy );
    }

    /**
     * This method adds a association if the variable has not already one.
     *
     * Does not add a already known association or a identity association.
     * @param var
     * @param replacement
     * @return Returns true if the association was added; otherwise false.
     */
    public void addAssociation(Variable var, Expression replacement) {

        // TODO: Check if we could remove this check
        if ( containsKey(var) || var.equals(replacement) )
            return;

        put( var, replacement );
    }

    public Substitution apply(Substitution substitution) {
        Substitution sigma = new Substitution();
        Substitution psi = substitution.factor();

        // iterate over every entry of this substitution
        for ( Map.Entry<Variable, Expression> entry : this.entrySet() )
            sigma.addAssociation( entry.getKey(), entry.getValue().apply(psi) );

        // iterate over every entry of the other substitution
        for ( Map.Entry<Variable, Expression> entry : substitution.entrySet() )
            sigma.addAssociation( entry.getKey(), entry.getValue().apply(psi) );

        return sigma;
    }

    /**
     * Apply the substitution to itself repeatedly until every mapping maps out
     * of the domain of the substitution.
     *
     * @return
     */
    public Substitution factor() {
        Substitution psi = new Substitution();

        // iterate over every entry
        for ( Map.Entry<Variable, Expression> entry : this.entrySet() ) {

            Variable   var    = entry.getKey();
            Expression mapNew = entry.getKey();
            Expression mapOld = entry.getKey();

            while (true) {
                mapNew = mapOld.apply(this);
                if ( mapNew == null ) {
                    mapNew = mapOld;
                    break;
                } else if ( mapNew.equals( mapOld ) ) {
                    break;
                } else
                    mapOld = mapNew;
            }
            psi.addAssociation( var, mapNew );
        }

        return psi;
    }

    public Substitution canonize(Substitution canon) {
        Substitution canonical = new Substitution();
        for(Map.Entry<Variable, Expression> association : this.entrySet()){
            Variable canonicalKey = (Variable) canon.get( association.getKey() );
            Expression value = association.getValue();
            Expression canonicalValue = value.apply( canon );

            canonical.put(canonicalKey, canonicalValue);
        }
        return canonical;
    }

    /**
     * This method returns a subset of the substitution that contains only the
     * variables occuring in the given expression.
     *
     * TODO: Check if the factor calling can be removed in favor of keeping the
     * results from factor within this object.
     * @param expression
     * @return
     */
    public Substitution restrict(Expression expression) {

        Substitution   sigma  = new Substitution();
        Substitution   psi    = this.factor();

        // iterate over all variables gotten from expression
        for ( Variable var : expression.getVariables() ){
            if (psi.containsKey( var )){
                Expression expression2 = psi.get(var);
                sigma.addAssociation( var, expression2 );
            }
        }

        return sigma;
    }


    /**
     * This method tests if the substitution object is a unificator for a list
     * of expressions.
     *
     * Definition: A substitution is a unificator if
     *             L_1 sigma = L_2 sigma = ... = L_m sigma is true, where
     *             L_n   - Expression,
     *             sigma - Substitution
     *
     * @param expressions
     * @return
     */
    public boolean isUnificator ( ExpressionList expressions ) {

        // only check if we have something to check
        if ( expressions == null || expressions.size() == 0 )
            return false;

        // every substitution is a unificator if we have only one expression
        if ( expressions.size() == 1 )
            return true;

        // initialising with first expression
        Expression tmpExp = expressions.get(0).apply(this);

        // if we have a difference in at least one expression it means that
        // there is no unificator
        for ( Expression exp: expressions )
            if ( !( tmpExp.equals( exp.apply(this) ) ) )
                return false;

        return true;
    }

    /**
     * Returns a string representation of the substitution.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("[ ");

        for ( Map.Entry<Variable, Expression> entry : this.entrySet() )
            sb.append("{").append( entry.getKey()).append( "->" ).append( entry.getValue() ).append( "}, ");

        sb.append( " ]" );
        return sb.toString();
    }

}
