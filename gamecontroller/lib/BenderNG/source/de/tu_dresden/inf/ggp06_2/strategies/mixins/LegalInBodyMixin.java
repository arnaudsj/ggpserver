package de.tu_dresden.inf.ggp06_2.strategies.mixins;

import java.util.List;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class LegalInBodyMixin implements StrategyMixin {
    
    public void preStateEvaluation(GameState state, Game game, TimerFlag timerFlag)
            throws InterruptedException {
        List<Atom> availableRoles = game.getRoleNames();
        for (Atom aRole : availableRoles){
            game.getLegalMoves( aRole, state, timerFlag );
        }
    }
}
