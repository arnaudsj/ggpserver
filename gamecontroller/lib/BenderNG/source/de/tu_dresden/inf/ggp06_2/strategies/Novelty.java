package de.tu_dresden.inf.ggp06_2.strategies;

import java.util.Map;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.simulator.Game;

/**
 * The Novelty class - inherits AbstractStrategy and implements
 * pickMove() method by selecting the move which leads to a game
 * state that differs the most form the current game state. The
 * difference is computed by the countTheChangedFluents() method
 * as the total number of fluents that are true in one state but
 * not in the other.
 * In the case of multiple moves with the same novelty ratio,
 * pickMove() chooses the first move.
 * The move is not considered if it leads to a game state that
 * has been encountered before.
 *
 * TODO: Reconsider if all fluents should be counted as equal,
 *      perhaps some weights on fluents might be introduced...
 *
 *      Reconsider if the repeated game states should be
 *      considered. If not, uncomment the line 69.
 *
 *      Should pickMove() return multiple moves in the case of the
 *      same novelty ratio?
 *
 * @author Novak Novakovic
 *
 */

public class Novelty extends AbstractStrategy {

    private static final Logger logger = Logger.getLogger(Novelty.class);

        public Novelty(Game game, String role){
        super(game,role);
    }

    @Override
    public Expression pickMove(GameNode node) {

        ExpressionList legalMoves;
        try {
            legalMoves = game.getLegalMoves(
                    this.role, node.getState(), timerFlag);
        }
        catch ( InterruptedException e1 ) {
            return new Predicate(Const.aDoes, role, Const.aNoop);
        }
        Expression pickedMove = null;
        if (!legalMoves.isEmpty()){
            if (logger.isTraceEnabled()){
                logger.trace( "available moves: "+legalMoves );
            }

            Expression bestMove = legalMoves.get( 0 );
            int noveltyRate = -1;


            for (Expression aMove: legalMoves){
                if (logger.isTraceEnabled()){
                    logger.trace( "processing move: "+aMove );
                }
                ExpressionList movesList = new ExpressionList(aMove);
                GameNode nextNode = null;
                try {
                    nextNode = game.produceNextNode( node, movesList, this.timerFlag );
                }
                catch ( InterruptedException e ) {
                    return bestMove;
                }
                if (logger.isTraceEnabled()){
                    logger.trace( "nextState: "+ nextNode.getState() );
                }

                /*if (this.stateTree.wasPlayed(nextState.hashCode())){
                    noveltyRate = 0;
                    continue;
                } */

                int tmp = countTheChangedFluents(node.getState(), nextNode.getState());

                if (-1 == noveltyRate ||
                        tmp > noveltyRate) {
                    bestMove = aMove;
                    noveltyRate = tmp;
                }
            }
            if (logger.isTraceEnabled()){
                logger.trace( " dicided for: "+bestMove );
            }
            pickedMove = bestMove;
        }
        if (null == pickedMove){
            return new Predicate(Const.aDoes, role, Const.aNoop);
        }
        return pickedMove;

    }
    private int countTheChangedFluents(GameState oldState, GameState newState){
        int counter = 0;
        Map<Atom, ExpressionList> oldFluents = oldState;
        Map<Atom, ExpressionList> newFluents = newState;

        for (Atom oldAtom : oldFluents.keySet()){
            for (ExpressionList oldExpressionList : oldFluents.values())
                for (Expression oldExpression : oldExpressionList)
                    if (!newFluents.get( oldAtom ).contains( oldExpression ))
                        counter++;
        }
        for (ExpressionList newAtom : newFluents.values()){
            for (ExpressionList newExpressionList : newFluents.values())
                for (Expression newExpression : newExpressionList)
                    if (!newAtom.contains( newExpression ))
                        counter++;
        }
        return counter;
    }
}
