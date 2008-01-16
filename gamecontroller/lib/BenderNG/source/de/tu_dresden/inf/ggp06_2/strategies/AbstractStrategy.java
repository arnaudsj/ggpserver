package de.tu_dresden.inf.ggp06_2.strategies;

import java.util.Random;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameNode;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public abstract class AbstractStrategy {
    
    protected final Game   game;
    protected final Atom   role;
    protected final Atom[] otherRoles;    
    protected TimerFlag    timerFlag;
    protected Random       random = new Random();

    public AbstractStrategy (Game newGame, String currentRole) {
        game       = newGame;
        role       = new Atom( currentRole );
        otherRoles = new Atom[game.getRoleNames().size() - 1];
        int i = 0;
        for ( Atom otherRole : game.getRoleNames() )
            if ( !otherRole.equals(role) )
                otherRoles[i++] = otherRole;
    }
    
    public AbstractStrategy(Game newGame, String currentRole, TimerFlag flag) {
        this(newGame, currentRole);
        timerFlag  = flag;
    }

    abstract public Expression pickMove(GameNode state);

    public final Game getGame() {
        return game;
    }

    public final void setTimerFlag(TimerFlag flag) {
        timerFlag = flag;
    }

    public final TimerFlag getTimerFlag() {
        return timerFlag;
    }

}