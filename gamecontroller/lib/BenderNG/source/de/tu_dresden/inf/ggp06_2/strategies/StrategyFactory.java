package de.tu_dresden.inf.ggp06_2.strategies;

import de.tu_dresden.inf.ggp06_2.simulator.Game;

/**
 * The Strategy class is a singleton class that takes care of creating strategy
 * objects.
 * 
 * @author ingo
 *
 */
public class StrategyFactory {
    
    public static final int STR_RANDOM    = 0;
    public static final int STR_MOBILITY  = 1;
    public static final int STR_MOBILITY2 = 2;
    public static final int STR_NOVELTY   = 3;
    
    public static AbstractStrategy createStrategy(Game game, String role, int type) {
        switch (type) {
            case STR_RANDOM:
                return new RandomStrategy(game, role);
            
            case STR_MOBILITY:
                return new Mobility(game, role);
            
            case STR_NOVELTY:
                return new Novelty(game, role);
            
            case STR_MOBILITY2:
                return new Mobility2(game, role);

            default:
                return null;
        }
    }


}
