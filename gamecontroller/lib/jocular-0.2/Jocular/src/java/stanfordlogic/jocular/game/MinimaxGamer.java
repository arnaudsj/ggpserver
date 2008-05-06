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
package stanfordlogic.jocular.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import stanfordlogic.game.Gamer;
import stanfordlogic.gdl.Parser;
import stanfordlogic.knowledge.BasicKB;
import stanfordlogic.knowledge.KnowledgeBase;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.ProofContext;
import stanfordlogic.prover.Term;
import stanfordlogic.prover.TermObject;
import stanfordlogic.util.FactCombinationIterator;
import stanfordlogic.util.Pair;
import stanfordlogic.util.TimedTaskMonitor;
import stanfordlogic.util.Triple;
import stanfordlogic.util.Util;

/**
 *
 */
public class MinimaxGamer extends Gamer
{
    
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.game");
    private static final Logger searchLogger_ = Logger.getLogger("stanfordlogic.game.search");
    
    private final Timer searchTimer_;
    
    private boolean stopSearch_;
    
    /** The best move so far at the root search node. */
    private Term bestMoveSoFar_;
    /** The best score so far at the root search node. */
    private int  bestScoreSoFar_;
    
    private class TimeoutException extends Exception
    {
        public TimeoutException()
        {
            super();
        }
        public TimeoutException(String msg)
        {
            super(msg);
        }
    }
    
    public MinimaxGamer(String gameId, Parser p)
    {
        super(gameId, p);
        
        random_ = new Random();
        
        searchTimer_ = new Timer();
    }

    @Override
    protected Triple<Term, String, String> moveThink()
    {
        searchTimer_.purge();
        stopSearch_ = false;
        bestMoveSoFar_ = null;
        bestScoreSoFar_ = Integer.MIN_VALUE;
        
        // Set up the play clock:
        TimedTaskMonitor searchTask = new TimedTaskMonitor(this);
        long time = (playClock_-2) * 1000; // convert to ms, giving ourselves a 2 second margin
        searchTimer_.schedule(searchTask, time);
        
        // We now have playClock-2 seconds to finish our search.
        
        Pair<Term, Integer> searchResult;
        
        // Construct the root search state
        KnowledgeBase rootState = new BasicKB();
        for (GroundFact ground : currentState_.getIterable()) {
            rootState.setTrue(ground);
        }
        // ... and the proof contex to go with it
        ProofContext rootContext = new ProofContext(rootState, parser_);
        
        // Run the minimax search.
        
        try {
            searchResult = minimaxSearch(rootState, rootContext, 0);
        }
        catch (TimeoutException e) {
            // We timed out -- default to whatever was registered as the best move so far
            logger_.fine(gameId_ + ": Search clock expired, using fallback move.");
            searchResult = new Pair<Term, Integer>(bestMoveSoFar_, -1);
        }
        
        if (searchResult == null || searchResult.first == null) {
            // This is really really bad.
            logger_.severe(gameId_ + ": No move returned by minimax search");
            return null;
        }
        
        Term action = searchResult.first;
        
        String explanation = getExplanation(searchResult);
        
        String taunt = getTaunt(searchResult.second);

        return new Triple<Term, String, String>(action, explanation, taunt);
    }
    
    private String getExplanation(Pair<Term, Integer> searchResult)
    {
        if (searchResult.second >= 0)
            return "Minimax score is " + searchResult.second;
        else
            return "Timed out; using fallback move.";
    }
    
    private String getTaunt(int score)
    {
        if (score == 100) {
            return "HAHA! I win!";
        }
        else if (score >= 75) {
            return "I'm in pretty good shape...";
        }
        else if (score >= 50) {
            return "Well, could be worse.";
        }
        else if (score > 0) {
            return "Not so good for me.";
        }
        else if (score == 0) {
            return "Well, darn.";
        }
        else {
            return "I have no idea, really.";
        }
    }
    
    private Pair<Term, Integer> minimaxSearch(KnowledgeBase state, ProofContext context,
                                              int currentDepth) throws TimeoutException
    {
        newLevel(currentDepth, state);
        
        // is this a terminal node?
        GroundFact isTerminal = reasoner_.getAnAnswer(QUERY_TERMINAL, context);
        
        if (isTerminal != null) {
            return calculateTerminal(context, currentDepth);
        }
        
        // Not terminal, so do the minimax search.
        
        // Build a list of everybody's moves.
        
        List<List<GroundFact>> otherMoves = new ArrayList<List<GroundFact>>();
        List<GroundFact> myMoves = null;
        
        for (int i = 0; i < roles_.size(); i++)
        {
            TermObject role = roles_.get(i);
            List<GroundFact> roleMoves = getAllAnswers(context, "legal", role.toString(), "?x");
            
            if (roleMoves.size() == 0) {
                logger_.severe(gameId_ + ": role " + role.toString() + " had no legal moves!");
            }
            
            if (i == myRoleIndex_) {
                myMoves = roleMoves;
                
                if (currentDepth == 0 && bestMoveSoFar_ == null) {
                    // pick a random first move if we don't have one yet
                    bestMoveSoFar_ = myMoves.get(random_.nextInt(myMoves.size()))
                                            .getTerm(1);
                }
            }
            else {
                otherMoves.add(roleMoves);
            }
        }
        
        // Pick my move that maximizes my score, assuming all other players
        // are trying to minimize it.
        Pair<Term, Integer> move =
                findMaximalMove(state, context, myMoves, otherMoves, currentDepth);
        
        return move;
    }
    
    private Pair<Term, Integer> calculateTerminal(ProofContext context, int currentDepth)
    {
        // figure out my score in this outcome
        GroundFact myGoal = getAnAnswer(context, "goal", myRoleStr_, "?x");
        
        int myScore = Integer.MIN_VALUE;
        
        if (myGoal == null) {
            logger_.severe(gameId_ + ": No goal for me (" + myRoleStr_ + "); using Integer.MIN_VALUE");
        }
        else {
            try {
                myScore = Integer.parseInt(myGoal.getTerm(1).toString());
            }
            catch (NumberFormatException e) {
                logger_.severe(gameId_ + ": My goal (" + myRoleStr_ + ") was not a number; was: " + myGoal.getTerm(1));
            }
        }
        
        reportTerminal(myScore, currentDepth);
        
        return new Pair<Term,Integer>(null, myScore);
    }
    
    private Pair<Term, Integer> findMaximalMove(KnowledgeBase state,
                                                ProofContext context,
                                                List<GroundFact> myMoves,
                                                List<List<GroundFact>> otherMoves,
                                                int currentDepth) throws TimeoutException
    {
        Term bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        for (GroundFact myMove: myMoves)
        {
            FactCombinationIterator it = new FactCombinationIterator(myMove, otherMoves, doesProcessor_);
            
            int minScore = Integer.MAX_VALUE;
            
            for (GroundFact [] moveSet : it)
            {
                // Find the combination that *minimizes* my score.
                int score = getScore(state, context, moveSet, currentDepth);
                
                if (score < minScore) {
                    minScore = score;
                }
            }
            
            if (minScore > bestScore)
            {
                bestScore = minScore;
                bestMove = myMove.getTerm(1);
                
                if (currentDepth == 0 && bestScore > bestScoreSoFar_)
                {
                    bestMoveSoFar_ = bestMove;
                    bestScoreSoFar_ = bestScore;
                }
            }
            
            if (bestScore == 100) {
                // might as well stop now!
                break;
            }
            
            // is it time to stop the search?
            if (stopSearch_) {
                throw new TimeoutException();
            }
        }
        
        if (bestMove == null || bestScore == Integer.MIN_VALUE) {
            searchLogger_.severe(gameId_ + ": Failed to find best move or best score!!");
        }
        
        return new Pair<Term, Integer>(bestMove, bestScore);
    }
    
    private int getScore(KnowledgeBase state, ProofContext context, GroundFact [] moves,
                         int currentDepth) throws TimeoutException
    {
        // Create a new state, based on state and context

        // First, add the moves
        for (GroundFact move: moves) {
            state.setTrue(move);
        }
        
        // Figure out what is true in the new state
        Iterable<GroundFact> nexts = reasoner_.getAllAnswersIterable(QUERY_NEXT, context);
        
        // FIXME: this should create a knowledge base of the same type as the current one
        KnowledgeBase newState = new BasicKB();
        
        for (GroundFact next: nexts) {
            newState.setTrue(trueProcessor_.processFact(next));
        }
        
        ProofContext newContext = new ProofContext(newState, parser_);
        
        // Run the recursive search
        Pair<Term, Integer> result = minimaxSearch(newState, newContext, currentDepth+1);
        
        // Remove the moves
        for (GroundFact move: moves) {
            state.setFalse(move);
        }
        
        return result.second;
    }
    
    
    private void newLevel(int currentDepth, KnowledgeBase state)
    {
        if (searchLogger_.isLoggable(Level.FINER))
        {
            String indent = Util.makeIndent(currentDepth);
            searchLogger_.finer(indent + gameId_ + ": Entering level " + currentDepth);
            
            String stateStr = state.stateToGdl();
            searchLogger_.finer(indent + gameId_ + ": State: " + stateStr);
        }
    }
    private void reportTerminal(int myScore, int currentDepth)
    {
        if (searchLogger_.isLoggable(Level.FINER))
        {
            String indent = Util.makeIndent(currentDepth);
            searchLogger_.finer(indent + gameId_ + ": Terminal. My score: " + myScore);
        }
    }

    @Override
    public void stopIt()
    {
        stopSearch_ = true;
    }

}
