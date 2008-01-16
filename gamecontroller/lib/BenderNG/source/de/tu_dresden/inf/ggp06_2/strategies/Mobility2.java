package de.tu_dresden.inf.ggp06_2.strategies;

import java.util.ArrayList;
import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.simulator.Game;

public class Mobility2 extends AbstractStrategy {

    public Mobility2(Game newGame, String currentRole) {
        super(newGame, currentRole);
    }

    @Override
    public Expression pickMove(GameNode node) {

        // get all legal moves
        List<ExpressionList> movesList = new ArrayList<ExpressionList>();
        List<Atom>           roleNames = game.getRoleNames();
        for ( Atom aRole : roleNames ) {
            try {
                movesList.add( game.getLegalMoves( aRole, node.getState(), timerFlag ) );
            }
            catch ( InterruptedException e1 ) {
                return new Predicate(Const.aDoes, role, Const.aNoop);
            }
        }
                
        // flatten moves for multiplayer games
        if ( !game.isSinglePlayer() ) {
            return new Predicate(Const.aDoes, role, Const.aNoop);
        }
            
        // calculate all next game states and the legal moves
        // check for the gamestate with the highest number of legal moves
        // we assume that at least one following state exist
        GameNode       tmpNode = null;
        int            current;
        int            max  = -1;
        ExpressionList best = movesList.get(0);

        for ( ExpressionList move : movesList ) {
            
            try {
                tmpNode = game.produceNextNode(node, move, timerFlag);
            }
            catch ( InterruptedException e ) {
                return best.get(0);
            }
            
            if ( tmpNode.getState().wasPlayed() )
                continue;
            
            try {
                current = game.getLegalMoves( role, tmpNode.getState(), timerFlag ).size();
            }
            catch ( InterruptedException e ) {
                return best.get(0);
            }

            if ( current > max ) {                
                max  = current;
                best = move;
                System.err.println("Move " + best.get(0) + " branching " + current);
            } 
            
        }
        
        // return the move        
        return best.get(0);
    }
}
