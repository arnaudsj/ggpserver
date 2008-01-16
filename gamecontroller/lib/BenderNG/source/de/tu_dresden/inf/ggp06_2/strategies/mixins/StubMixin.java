package de.tu_dresden.inf.ggp06_2.strategies.mixins;

import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

/**
 * This class serves as a placeholder for various StrategyMixin's 
 * No method if this particular class should ever be implemented
 * (that's the desteny of any stub ;) )
 * @author Arsen Kostenko
 *
 */
public class StubMixin implements StrategyMixin {
    

    /**
     * Empty stub method.
     */
    public final void preStateEvaluation(GameState state, Game game, TimerFlag timerFlag)
            throws InterruptedException {
    }
}
