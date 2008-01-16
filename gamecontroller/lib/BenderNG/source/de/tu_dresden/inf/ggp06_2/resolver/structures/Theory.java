package de.tu_dresden.inf.ggp06_2.resolver.structures;

import gnu.trove.THashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.Implication;
import de.tu_dresden.inf.ggp06_2.resolver.NotOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;
import de.tu_dresden.inf.ggp06_2.resolver.helper.ResolverUtils;
import de.tu_dresden.inf.ggp06_2.resolver.structures.functions.Function;
import de.tu_dresden.inf.ggp06_2.resolver.structures.relations.Relation;

/**
 * The Theory class is basically the storage for all information that we have of
 * our environment. The main and original purpose is the mapping between a key
 * (function or relation) symbol and the correspondent expressions.
 *
 * Besides this mapping theory contains extra information.
 * <ul>
 *   <li>Map<Atom, Integer> fSymbols - function symbol to arity mapping</li>
 *   <li>Map<Atom, Integer> rSymbols - relation symbol to arity mapping</li>
 * </ul>
 *
 * Since the class should be as small and as fast as possible the extra
 * information has to be put in separately that means the other information is
 * not necessarily present and generate from the outside.
 *
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 * @author Novak Novakovic - General Game Playing course student at TUD
 *
 */
public final class Theory extends THashMap<Atom, ExpressionList> {

    private static final Logger logger = Logger.getLogger(Expression.class);

    /**
     * This attribute contains the expression list that is provided by the
     * parser.
     */
    private ExpressionList ast;

    private Map<Atom, Function> fSymbols = new THashMap<Atom, Function>();
    private Map<Atom, Relation> rSymbols = new THashMap<Atom, Relation>();

    /**
     * fDomains contains a mapping Atom -> Set where the a set contains object
     * constants of a function symbol.
     *
     * Some examples:
     * (coordinate 1) (coordinate 2) (coordinate 3)
     * Map[coordinate] = { 1, 2, 3 }
     *
     * (succ 1 2) (succ 2 3) (succ 3 4)
     * Map[succ] = { 1, 2, 3, 4 }
     */
    private Map<Atom, Set<String>> fDomains = new HashMap<Atom, Set<String>>();
    final Set<Atom>            counterNames = new HashSet<Atom>();

    public Theory() {
        ast = null;
    }

    public Theory(ExpressionList gameDescription) {
        ast = gameDescription;
        Atom key;
        for ( Expression expression : gameDescription ) {

            key = expression.getKeyAtom();
            if ( !containsKey(key) )
                put( key, new ExpressionList() );

            get(key).add(expression);
        }

    }

    /**
     * This method returns all elements that are available in theory and match
     * the key.
     *
     * @param key
     */
    public Theory flattenTheory() {
        Theory newTheory = new Theory( getAll() );
        for ( Atom key : newTheory.keySet() )
            newTheory.put( key, ResolverUtils.flattenExpressionList(newTheory.get(key)) );

        return newTheory;
    }

    /**
     * This method returns all expressions of theory in a single ExpressionList.
     * @return
     */
    public ExpressionList getAll() {
        ExpressionList gameDescription = new ExpressionList();
        for ( ExpressionList expList : values() )
            gameDescription.addAll( expList );

        return gameDescription;
    }

    /**
     * This method takes a theory and checks if it is a valid game description
     * with respect to the variable occurens.
     * @return
     */
    public boolean isValid() {

        // get flat theory
        Theory flat = flattenTheory();

        // find the rule atoms
        List<Atom> ruleAtom = new ArrayList<Atom>();
        for (Atom key : flat.keySet())
            if ( flat.get(key).get(0) instanceof Implication )
                ruleAtom.add(key);

        // check the rules
        for (Atom key : ruleAtom)
            for (Expression exp : flat.get(key) ) {
                if ( !validRule( (Implication) exp ) )
                    return false;

                // if rule is sound - we want to know more
//                setRuleInformation( (Implication) exp );
            }

        // run through everything of the domain an gather the position
        // to list<atom> information
//        ExpressionList expList = new ExpressionList();
//        for ( Atom key: flat.keySet() )
//            expList.addAll( flat.get( key ) );
//
//        set = new HashSet<Position2>();
//        for (Expression exp: expList)
//            getPositions(exp, set);
//
//        findProperDomains(flat);
        return true;
    }

    /**
     * This method checks wether a single rule is valid or not with respect to
     * the occurens of the variables.
     * @param rule
     * @return
     */
    private boolean validRule(Implication rule) {

        List<Variable> positiv = new ArrayList<Variable>();
        List<Variable> negativ = new ArrayList<Variable>();

        // head
        positiv.addAll( rule.getConsequence().getVariables() );
        for (Expression exp : rule.getPremises())

            // gather the negativ variables
            if (exp instanceof NotOperator || exp instanceof DistinctOperator)
                negativ.addAll( exp.getVariables() );

            // gather the positive variables
            else
                positiv.addAll( exp.getVariables() );

        // check occurens
        for (Variable var : negativ)
            if ( !positiv.contains(var) ) {

                logger.error(var + " does not appear in a positiv Literal of " +
                             rule);
                return false;
            }
        return true;
    }






    @Override
    public ExpressionList get(Object key) {
        return containsKey(key) ? super.get(key) : new ExpressionList();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( "Theory Mappings:" );
        for ( Atom key : keySet() )
            sb.append(key).append(" \t= [").append(get(key)).append("]\n");
        return sb.toString();
    }

    /**
     * This method returns the description string which shows the content of
     * the function and relation mappings known.
     * @return
     */
    public String symbolsToString() {

        StringBuilder sb = new StringBuilder( "Theory Symbols:\n" );

        // relations
        sb.append( "---Relations---\n" );
        if (rSymbols != null)
            for ( Atom key : rSymbols.keySet() )
                sb.append(key).append("." + rSymbols.get(key)).append("\n");
        else
            sb.append( "No information available." );

        // functions
        sb.append( "---Functions---\n" );
        if (fSymbols != null)
            for ( Atom key : fSymbols.keySet() )
                sb.append(key).append("." + fSymbols.get(key)).append("\n");
        else
            sb.append( "No information available." );

        return sb.toString();
    }

    /**
     * @return the fSymbols
     */
    public final Map<Atom, Function> getFSymbols() {
        return fSymbols;
    }

    /**
     * @return the rSymbols
     */
    public final Map<Atom, Relation> getRSymbols() {
        return rSymbols;
    }

    /**
     * @param symbols the fSymbols to set
     */
    public final void setFSymbols(Map<Atom, Function> symbols) {
        fSymbols = symbols;
    }

    /**
     * @param symbols the rSymbols to set
     */
    public final void setRSymbols(Map<Atom, Relation> symbols) {
        rSymbols = symbols;
    }

    /**
     * @return the fSets
     */
    public final Map<Atom, Set<String>> getFDomains() {
        return fDomains;
    }

    /**
     * This method returns an expression list that represents the abstract
     * syntax tree of the theory object.
     *
     * @return abstract syntax tree of theory
     */
    public final ExpressionList getAst() {
        return ast;
    }


    /**
     * This method returns all legal rules of the game.
     * @return
     */
    public final List<Implication> getLegalRules() {
        List<Implication> rules = new ArrayList<Implication>();

        // find legal rules
        for (Expression exp : ast)
            if ( exp instanceof Implication &&
                 Const.aLegal.equals( exp.getKeyAtom() ) )
                rules.add( (Implication) exp );

        return rules;
    }


    /**
     * This method returns all next rules of the game.
     * @return
     */
    public final List<Implication> getNextRules() {
        List<Implication> rules = new ArrayList<Implication>();

        // find next rules
        for (Expression exp : ast)
            if ( exp instanceof Implication &&
                 Const.aNext.equals( exp.getKeyAtom() ) )
                rules.add( (Implication) exp );

        return rules;
    }

    public void setAst(ExpressionList ast) {
        this.ast = ast;
    }
}
