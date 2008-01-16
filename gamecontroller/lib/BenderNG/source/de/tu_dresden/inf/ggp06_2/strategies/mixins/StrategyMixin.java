package de.tu_dresden.inf.ggp06_2.strategies.mixins;

import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public interface StrategyMixin {

    void preStateEvaluation(GameState state, Game game, TimerFlag timerFlag)
    throws InterruptedException;
    
    
}
