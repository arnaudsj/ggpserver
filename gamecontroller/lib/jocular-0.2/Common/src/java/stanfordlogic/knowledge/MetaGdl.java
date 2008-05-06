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
package stanfordlogic.knowledge;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.GdlVariable;
import stanfordlogic.gdl.Parser;
import stanfordlogic.prover.Expression;
import stanfordlogic.prover.Fact;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.Negation;
import stanfordlogic.prover.Disjunction;
import stanfordlogic.prover.Implication;
import stanfordlogic.prover.TermObject;
import stanfordlogic.game.GameManager;


/**
 * MetaGdl is knowledge about a game description. Given a game description,
 * MetaGdl will compute things such as:
 * 
 * <ul>
 * <li>which symbols are functions, constants, relations
 * <li>what the dependency graphs are
 * <li>which symbols are volatile, constant
 * </ul>
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class MetaGdl
{
    private Map<Integer, RelationInfo>              relations_;
    private Set<Integer>                            functionSymbols_;
    private Set<Integer>                            objectSymbols_;
    
    /** The parser that was used to create these symbols. */
    private Parser                                  parser_;
    
    /** Rules that were extracted during GDL examination. */
    private Map<Integer, List<Implication>>                rules_;
    
    /** Ground facts that were extracted during GDL examination. */
    private Map<Integer, List<GroundFact>>          groundFacts_;

    /** Ground facts corresponding to static relations */
    private Map<Integer, List<GroundFact>>          staticRelations_;
    
    /** All expressions in the game description. */
    private List<Expression> expressions_;
    
    /** 'Init' facts */
    private List<GroundFact>                        initFacts_;
    /** Ordered list of roles in the game. */
    private List<TermObject>                        roles_;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.knowledge");
    
    public MetaGdl(Parser p)
    {
        relations_ = new TreeMap<Integer, RelationInfo>();
        functionSymbols_ = new TreeSet<Integer>();
        objectSymbols_   = new TreeSet<Integer>();
        
        parser_ = p;
        
        rules_ = new TreeMap<Integer, List<Implication>>();
        groundFacts_ = new TreeMap<Integer, List<GroundFact>>();
        roles_ = new ArrayList<TermObject>();
        
        expressions_ = new ArrayList<Expression>();
        
        insertReservedKeywords();
    }
    
    private void insertReservedKeywords()
    {
        addRelationSymbol(parser_.TOK_ROLE, 1);
        addRelationSymbol(parser_.TOK_INIT, 1);
        addRelationSymbol(parser_.TOK_TRUE, 1);
        addRelationSymbol(parser_.TOK_DOES, 2);
        addRelationSymbol(parser_.TOK_NEXT, 1);
        addRelationSymbol(parser_.TOK_LEGAL, 2);
        addRelationSymbol(parser_.TOK_GOAL, 2);
        addRelationSymbol(parser_.TOK_TERMINAL, 0);
        addRelationSymbol(parser_.TOK_DISTINCT, 2);
        
        // nil isn't really a GDL token; it's only used during PLAY
        // messages to indicate that there was no previous move.
        //relationSymbols.add(parser_.TOK_NIL);
        
        
        // GDL operators
        // don't really need to add these, either.
        
        //public int TOK_IMPLIEDBY;
        //public int TOK_AND_OP;
        //public int TOK_OR_OP;
        //public int TOK_NOT_OP;
    }
    
    /**
     * Big monster master method that examines a game description and derives
     * properties from it. Should nonetheless strive to be a relatively rapid
     * process; no "game thinking" should occur here, only information about
     * straightforward "Datalog"ish facts should be examined.
     * 
     * @param gameDesc The game description to examine.
     * @return GameInformation The game information extracted from <tt>gameDesc</tt>.
     */
    public GameInformation examineGdl(GdlList gameDesc)
    {
        long startTime = System.nanoTime();
        
        for ( GdlExpression exp : gameDesc )
        {
            if ( exp instanceof GdlList )
                examineTopLevelList( (GdlList) exp);
            else if ( exp instanceof GdlAtom )
                examineTopLevelAtom( (GdlAtom) exp);
        }
        
        // Now that we have our list of relations and rules, we can do some
        // more analysis.
        
        // Sort static relations
        findStaticAndInitRelations();
        
        // TODO: do some more meta-analysis...
        
        GameInformation info = makeGameInformation();
        
        GameManager.addTime(GameManager.TIME_METAGDL, System.nanoTime() - startTime);
        
        return info;
    }
    
    private GameInformation makeGameInformation()
    {
        GameInformation info = new GameInformation();
        
        info.setFunctionSymbols(functionSymbols_);
        info.setGroundFacts(groundFacts_);
        info.setObjectSymbols(objectSymbols_);
        info.setRelations(relations_);
        info.setInitFacts(initFacts_);
        info.setStaticFacts(staticRelations_);
        info.setRoles(roles_);
        info.setRules(rules_);
        
        return info;
    }
    
    private boolean isRelationSymbol(int symbol)
    {
        return relations_.get( symbol ) != null;
    }
    private boolean isFunctionSymbol(int symbol)
    {
        return functionSymbols_.contains(symbol);
    }
    private boolean isObjectSymbol(int symbol)
    {
        return objectSymbols_.contains(symbol);
    }
    
    private void addRelationSymbol(int symbol, int arity)
    {
        boolean added = relations_.put( symbol, new RelationInfo(symbol, arity) ) == null;
        
        if (added)
        {
            groundFacts_.put(symbol, new ArrayList<GroundFact>());

            logger_.fine("MetaGDL: Adding relation symbol: " + symbol + " ("
                         + parser_.getSymbolTable().get(symbol) + ")");
        }
    }
    private void addFunctionSymbol(int symbol)
    {
        boolean added = functionSymbols_.add(symbol);

        if (added) {
            logger_.fine("MetaGDL: Adding function symbol: " + symbol + " ("
                         + parser_.getSymbolTable().get(symbol) + ")");
        }
    }
    private void addObjectSymbol(int symbol)
    {
        boolean added = objectSymbols_.add( symbol );
        
        if (added) {
            logger_.fine("MetaGDL: Adding object symbol: " + symbol + " ("
                         + parser_.getSymbolTable().get(symbol) + ")");
        }
    }
    
    private void addRule(int headRelation, Implication r)
    {
        List<Implication> rules = rules_.get(headRelation);
        
        if ( rules == null )
        {
            rules = new ArrayList<Implication>();
            rules_.put(headRelation, rules);
        }
        
        rules.add(r);
    }
    
    private void addGround(int relation, GroundFact ground)
    {
        List<GroundFact> list = groundFacts_.get(relation);
        
        /*if ( list == null )
        {
            list = new ArrayList<GroundFact>();
            groundFacts_.put(relation, list);
        }*/
        
        list.add(ground);
    }
    
    private void findStaticAndInitRelations()
    {
        staticRelations_ = new TreeMap<Integer, List<GroundFact>>();
        for(Integer i : groundFacts_.keySet())
        {
            if(i == parser_.TOK_IMPLIEDBY)
                continue;
            List<GroundFact> relation = groundFacts_.get(i); 
            if(i == parser_.TOK_INIT)
            {
                initFacts_ = relation;
                continue;
            }
            if(relation.size() > 0)
                staticRelations_.put(i, relation);
        }
    }
    
    private void examineTopLevelList(GdlList list)
    {
        // Is this a rule, or a relation?
        // Note that it is safe to assume that the head is in fact an atom.
        GdlAtom head = (GdlAtom) list.getElement(0);
        
        if ( head.getToken() == parser_.TOK_IMPLIEDBY )
        {
            Implication impl = examineRule(list);
            expressions_.add(impl);
            addRule(impl.getConsequent().getRelationName(), impl);
        }
        else
        {
            // It must be a ground fact at this point: it can't have variables,
            // since there is no rule to specify binding.
            GroundFact f = (GroundFact) examineListRelation(list, head.getToken() );
            addGround(head.getToken(), f);
            expressions_.add(f);
            
            // Is this a role?
            if ( head.getToken() == parser_.TOK_ROLE )
                roles_.add( (TermObject) f.getTerm(0));
        }
    }
    
    private void examineTopLevelAtom(GdlAtom atom)
    {
        int token = atom.getToken();
        
        // This probably never happens. . . so make a note of it
        System.err.println("WE GOT A TOP LEVEL ATOM!! " + parser_.getSymbolTable().get(token) );
        
        // Make sure this symbol isn't a function/object symbol already.
        if ( isFunctionSymbol( token ) || isObjectSymbol( token ) )
            throw new IllegalArgumentException( "Symbol '" + token + "' ("
                    + parser_.getSymbolTable().get( token )
                    + ") already exists, but not as a relation symbol!" );
        
        // It's a top level atom, so it has to be a relation symbol
        addRelationSymbol(token, 0);
    }
    
    private Implication examineRule(GdlList rule)
    {
        // First element is the IMPLIEDBY token; ignore it.

        // Second element is the head of the rule. It's a relation (fact).
        Fact head = (Fact) examineRelation(rule.getElement(1));
        
        // Everything thereafter are the antecedent relations.
        Expression [] conjuncts = new Expression[rule.getArity()-1];
        
        // Create the conjunct list
        for ( int i = 2; i < rule.getSize(); i++ )
            conjuncts[i-2] = examineRelation(rule.getElement(i));
        
        // Create a rule structure and add it to our list.
        Implication r = new Implication( false, head, conjuncts );
        
        r = r.uniquefy();
        
        return r;
    }
        
    private Expression examineRelation(GdlExpression relation)
    {
        if ( relation instanceof GdlAtom )
            return examineAtomRelation( (GdlAtom) relation );
        
        // else, must be a variable
        GdlList list = (GdlList) relation;
        return examineListRelation( (GdlList) relation, ((GdlAtom) list.getElement(0)).getToken() );
    }
    
    private Expression examineAtomRelation(GdlAtom atom)
    {
        int relName = atom.getToken();
        
        // Make sure this symbol isn't a function/object symbol already.
        if ( isFunctionSymbol( relName ) || isObjectSymbol( relName ) )
            throw new IllegalArgumentException( "Symbol '" + relName + "' ("
                    + parser_.getSymbolTable().get( relName )
                    + ") already exists, but not as a relation symbol!" );
        
        // Add to relation name to our list of relation symbols
        addRelationSymbol(relName, 0);
        
        return GroundFact.fromExpression(atom);
    }
    
    private Expression examineListRelation(GdlList relation, int relName)
    {
        // First: check to see if this is a logical operator, i.e. one of not/or/and
        
        if ( relName == parser_.TOK_NOT_OP )
        {
            // The next element must be a sentence
            return new Negation( examineRelation(relation.getElement(1) ) );
        }
            
        else if ( relName == parser_.TOK_OR_OP )
        {
            // all the rest of the elements are relations (sentences), not terms.
            Expression [] disjuncts = new Expression[relation.getArity()];
            
            for ( int i = 1; i <= relation.getArity(); i++ )
                disjuncts[i-1] = examineRelation(relation.getElement(i));
            
            return new Disjunction(false, disjuncts);
        }
        
        // Second case: normal relation.
        
        // Make sure this symbol isn't a function/object symbol already.
        if ( isFunctionSymbol( relName ) || isObjectSymbol( relName ) )
            throw new IllegalArgumentException( "Symbol '" + relName + "' ("
                    + parser_.getSymbolTable().get( relName )
                    + ") already exists, but not as a relation symbol!" );
        
        // Add the relation name to our list of relation symbols.
        addRelationSymbol(relName, relation.getArity());
        
        // Examine each term of the relation
        for ( int i = 1; i <= relation.getArity(); i++ )
            examineTerm(relation.getElement(i));
        
        // Convert the relation to a (ground or variable) fact.
        return Fact.fromExpression(relation);
    }
    
    private void examineTerm(GdlExpression exp)
    {
        if ( exp instanceof GdlAtom )
            examineAtomTerm( (GdlAtom) exp );
        
        else if ( exp instanceof GdlVariable )
            examineVariableTerm( (GdlVariable) exp );
        
        else if ( exp instanceof GdlList )
            examineListTerm( (GdlList) exp );
        
        else
        {
            throw new IllegalArgumentException(
                    "MetaGdl.examineTerm: can't handle expressions of type "
                            + exp.getClass().getName() );
        }
    }
    
    private void examineAtomTerm(GdlAtom atom)
    {
        // if it's a variable, do nothing with it.
        /*if ( atom instanceof GdlVariable )
            return;*/
        
        int token = atom.getToken();
        
        // This term must be an object constant. Make sure it is.
        if ( isFunctionSymbol( token ) || isRelationSymbol( token ) )
            throw new IllegalArgumentException( "Symbol '" + token + "' ("
                    + parser_.getSymbolTable().get(token)
                    + ") already exists, but not as an object symbol!" );
        
        // Add it to our list of object symbols
        addObjectSymbol(token);
    }
    
    private void examineVariableTerm(GdlVariable var)
    {
        // Do nothing.
    }
    
    private void examineListTerm(GdlList list)
    {
        // The first element here must be a function symbol.
        // Note that it is safe to assume that the head is in fact an atom.
        GdlAtom head = (GdlAtom) list.getElement(0);
        
        int token = head.getToken();
        
        // Make sure that 'token' is a function symbol.
        if ( isObjectSymbol( token ) || isRelationSymbol( token ) )
            throw new IllegalArgumentException( "Symbol '" + token + "' ("
                    + parser_.getSymbolTable().get(token)
                    + ") already exists, but not as a function symbol!" );
        
        // Add it to our list of function symbols
        addFunctionSymbol(token);
        
        // Now look at the rest of the elements in the list.
        for ( int i = 1; i < list.getSize(); i++ )
        {
            examineTerm( list.getElement(i) );
        }
    }
    
    
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static GameInformation examineGame(String filename, Parser p)
    {
        try
        {
            GdlList axioms = p.parse( new FileInputStream( filename ) );
            MetaGdl meta = new MetaGdl( p );
            GameInformation info = meta.examineGdl( axioms );
            return info;
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException("Error reading from file");
        }
    }
}
