package de.tu_dresden.inf.ggp06_2.strategies;

import java.util.Random;
import de.tu_dresden.inf.ggp06_2.resolver.Connective;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.simulator.Game;

public final class RandomStrategy extends AbstractStrategy {

    private final Random random = new Random();
    
    public RandomStrategy(Game game, String role) {
        super(game, role);
    }

    @Override
    public Expression pickMove(GameNode gameNode) {
        ExpressionList moves;
        try {
            moves = game.getLegalMoves( 
                    role, gameNode.getState(), timerFlag );
        }
        catch ( InterruptedException e ) {
            return Const.aNoop;
        }
        if ( moves.isEmpty() )
            return Const.aNoop;
        
        int randomInt = random.nextInt( moves.size() );
        Connective randomMove = (Connective) moves.get(randomInt);
        return  randomMove.getOperands().get(1);
    }
}
