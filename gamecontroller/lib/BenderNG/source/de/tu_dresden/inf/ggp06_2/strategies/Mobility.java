package de.tu_dresden.inf.ggp06_2.strategies;

import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class Mobility extends AbstractStrategy {

    private static final Logger logger = Logger.getLogger(Mobility.class);

    private final Atom opponent;

    public Mobility(Game game, String role){
        super(game,role);
        this.opponent = otherRoles[0];
    }

    public Mobility(Game game, String string, TimerFlag flag) {
        this(game, string);
        this.timerFlag = flag;
    }

    @Override
    public Expression pickMove(GameNode gameNode) {

        Expression pickedMove = null;
        ExpressionList legalMoves;
        try {
            legalMoves = game.getLegalMoves( 
                    role, gameNode.getState(), timerFlag );
        }
        catch ( InterruptedException e2 ) {
            return new Predicate(Const.aDoes, role, Const.aNoop);
        }
        if (!legalMoves.isEmpty()){
            if (logger.isTraceEnabled()){
                logger.trace( "first turn moves: "+legalMoves );
            }

            ExpressionList aMovesList;
            try {
                aMovesList = game.getLegalMoves(
                        this.opponent, gameNode.getState(), timerFlag );
            }
            catch ( InterruptedException e1 ) {
                return new Predicate(Const.aDoes, role, Const.aNoop);
            }
            Expression opponentMove = aMovesList.get( 0 );

            Expression bestMove = legalMoves.get( 0 );
            int mobilityRate = -1;


            for (Expression aMove: legalMoves){
                if (logger.isTraceEnabled()){
                    logger.trace( "processing move: "+aMove );
                }

                ExpressionList turnMoves = new ExpressionList();
                turnMoves.add( aMove );
                turnMoves.add( opponentMove );

                GameNode nextNode = null;
                try {
                    nextNode = game.produceNextNode( 
                            gameNode, turnMoves, this.timerFlag );
                }
                catch ( InterruptedException e ) {
                    return bestMove;
                }
                if (logger.isTraceEnabled()){
                    logger.trace( "nextState: " + nextNode.getState() );
                }

                ExpressionList nextStateMovesList = null;
                try {
                    nextStateMovesList = game.getLegalMoves( 
                            this.opponent, nextNode.getState(), timerFlag );
                }
                catch ( InterruptedException e ) {
                    return bestMove;
                }

                int tmp = nextStateMovesList.size();
                
                if (-1 == mobilityRate ||
                        tmp < mobilityRate) {
                    bestMove = aMove;
                    mobilityRate = tmp;
                }
            }
            if (logger.isTraceEnabled()){
                logger.trace( " dicided for: "+bestMove );
            }
            pickedMove = bestMove;
        }
        if (null == pickedMove) 
            return new Predicate(Const.aDoes, role, Const.aNoop);
        
        return pickedMove ;
    }

}
