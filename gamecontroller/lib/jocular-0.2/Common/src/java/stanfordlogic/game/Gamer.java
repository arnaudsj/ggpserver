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
package stanfordlogic.game;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import stanfordlogic.gdl.GdlAtom;
import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.Parser;
import stanfordlogic.knowledge.BasicKB;
import stanfordlogic.knowledge.GameInformation;
import stanfordlogic.knowledge.KnowledgeBase;
import stanfordlogic.knowledge.RelationNameProcessor;
import stanfordlogic.prover.AbstractReasoner;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.ProofContext;
import stanfordlogic.prover.Term;
import stanfordlogic.prover.TermObject;
import stanfordlogic.util.Stoppable;
import stanfordlogic.util.Triple;

/**
 * Contains all information pertaining to a current run of a game.
 * 
 * TODO: reorganize this as a running thread ("cognizing agent" model)
 */
public abstract class Gamer extends ReasoningEntity implements Stoppable
{
    /** Play clock: how much time I have to make a move */
    protected int                                         playClock_;
    
    /** RunnableMatch ID */
    protected String                                      gameId_;
    
    /** Ordered list of roles. */
    protected List<TermObject>                            roles_;
    
    /** The role assigned to "me" in the game. */
    protected TermObject myRole_;
    /** The role assigned to "me", in string form. */
    protected String     myRoleStr_;
    /** My role number (the index into the roles list). */
    protected int        myRoleIndex_;
    
    /** The current state of the game. That is, the collection of <i>true</i> facts. */
    protected KnowledgeBase currentState_;
    /** The proof context in which reasoning about this state is performed. */
    protected ProofContext currentContext_;
    
    /** The collected information about the game. */
    protected GameInformation gameInformation_;
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.game");
    private static final Logger stateLogger_ = Logger.getLogger("stanfordlogic.game.state");
    
    protected final RelationNameProcessor doesProcessor_;
    protected final RelationNameProcessor trueProcessor_;
    
    protected Gamer(String gameId, Parser p)
    {
        super(p);
        
        gameId_ = gameId;
        
        doesProcessor_ = new RelationNameProcessor("does", symbolTable_);
        trueProcessor_ = new RelationNameProcessor(parser_.TOK_TRUE);
    }
    
    public void initializeGame(TermObject role, int playClock,
                               GameInformation gameInformation,
                               AbstractReasoner reasoner)
    {
        myRole_ = role;
        myRoleStr_ = role.toString();
        playClock_ = playClock;
        gameInformation_ = gameInformation;
        roles_ = gameInformation_.getRoles();
        
        reasoner_ = reasoner;
        
        findRoleIndex();
        setupInitialState();
    }
    
    private void findRoleIndex()
    {
        for (int i = 0; i < roles_.size(); i++)
        {
            TermObject role = roles_.get(i);
            
            if (role.equals(myRole_)) {
                myRoleIndex_ = i;
                return;
            }
        }
        
        logger_.severe("Could not find my role index");
        myRoleIndex_ = 0;
    }
    
    protected void setupInitialState()
    {
        // First, create the game state
        currentState_ = new BasicKB();
        
        // Now, find all answers to the question: "init ?x"
        Iterable<GroundFact> inits = getAllAnswersIterable(ProofContext.makeDummy(parser_), "init", "?x");
        
        for (GroundFact init : inits) {
            currentState_.setTrue(trueProcessor_.processFact(init));
        }
        
        currentContext_ = new ProofContext(currentState_, parser_);
    }
    
    /**
     * @return The game ID used by this gamer.
     */
    public String gameId()
    {
        return gameId_;
    }

    public abstract void stopIt();

    /**
     * Think about the next move and return it.
     * 
     * @param prevMoves The moves just made, or nil if none (first move).
     * @return A triple containing: the move, an explanation (or null) and a taunt (or null).
     */
    public Triple<GdlExpression, String, String> play(GdlList prevMoves)
    {
        // Construct list of previous moves
        GroundFact [] previousMoves = parsePreviousMoves(prevMoves);
        
        if (previousMoves.length > 0 ) {
            updateCurrentState(previousMoves);
        }
        
        Triple<Term, String, String> move = moveThink();
        
        // Convert the Term to a GdlExpression
        
        GdlExpression moveGdl;
        
        if (move == null || move.first == null) {
            logger_.severe(gameId_ + ": move returned by moveThink was null");
            moveGdl = new GdlAtom(symbolTable_, parser_.TOK_NIL);
            move = new Triple<Term, String, String>(null, "", "");
        }
        else {
            // the top-level element returned is list of all elements in the parse;
            // in this case, we have just one, the move.
            moveGdl = parser_.parse(move.first.toString()).getElement(0);
        }
        
        Triple<GdlExpression, String, String> m =
                new Triple<GdlExpression, String, String>(moveGdl, move.second,
                                                          move.third);
                
        return m;
    }
    
    protected void updateCurrentState(GroundFact [] previousMoves)
    {
        for ( GroundFact prevMove : previousMoves )
            currentState_.setTrue(prevMove);
        
        if (stateLogger_.isLoggable(Level.FINE)) {
            stateLogger_.fine(gameId_ + ": Updating game state.");
            stateLogger_.fine(gameId_ + "   - Previous: " + currentState_.stateToGdl());
        }
        
        Iterable<GroundFact> newFacts = reasoner_.getAllAnswersIterable(QUERY_NEXT, currentContext_);
        
        // TODO: we should be creating the same kind of KB as currentState_ here
        KnowledgeBase newKb = new BasicKB();
        
        for(GroundFact newFact : newFacts)
            newKb.setTrue(trueProcessor_.processFact(newFact));
        
        currentState_ = newKb;
        
        if (stateLogger_.isLoggable(Level.FINE)) {
            stateLogger_.fine(gameId_ + "   -      New: " + currentState_.stateToGdl());
        }
        
        // Create a new proof context
        currentContext_ = new ProofContext(currentState_, parser_);
    }
    
    protected abstract Triple<Term, String, String> moveThink();
    
    protected GroundFact [] parsePreviousMoves(GdlList prevMoves)
    {
        if ( prevMoves == null )
            return new GroundFact[0];
        
        if (prevMoves.getSize() != roles_.size()) {
            logger_.severe(gameId_ + ": Previous move list is not the same size as number of roles!");
        }
        
        GroundFact [] previousMoves = new GroundFact[prevMoves.getSize()];
        
        for(int i=0; i<prevMoves.getSize(); i++)
        {
            if (i >= roles_.size())
                break;
            
            previousMoves[i] = new GroundFact(
                    parser_.TOK_DOES,
                    roles_.get(i),
                    Term.buildFromGdl(prevMoves.getElement(i))
                );
        }
        
        return previousMoves;
    }
    
    
    /**
     * Compute the payoffs of the game.
     * 
     * @param prevMoves The list of the last moves.
     * @return List of triplets with player's name, payoff, and a flag telling whether it's the played role.
     */
    public List<Triple<String, Integer, Boolean>> getPayoffs(GdlList prevMoves)
    {
        // (role name, payoff, my role?) list
        List<Triple<String, Integer, Boolean>> payoffs = new ArrayList<Triple<String, Integer, Boolean>>(roles_.size());
        
        // Construct list of previous moves
        GroundFact [] previousMoves = parsePreviousMoves(prevMoves);
        updateCurrentState(previousMoves);
        
        for(TermObject role : roles_)
        {
            int payoff = 0;
            try
            {
                GroundFact goal = getAnAnswer(currentContext_, "goal", role.toString(), "?x");
                TermObject score = (TermObject) goal.getTerm(1);
                payoff = Integer.parseInt(score.toString());
            }
            catch(Exception e)
            {
                payoff = -1;
            }
            payoffs.add(new Triple<String, Integer, Boolean>(role.toString(), payoff, role.equals(myRole_)));
        }

        return payoffs;
    }

    
}
