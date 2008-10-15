package tud.gamecontroller;

import java.util.Map;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

public interface GameControllerListener{

	void gameStarted(MatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface, ?> currentState);

	void gameStep(JointMoveInterface<? extends TermInterface> jointmove, StateInterface<? extends TermInterface, ?> currentState);

	void gameStopped(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues);

}
