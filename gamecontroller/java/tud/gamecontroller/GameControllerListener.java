package tud.gamecontroller;

import java.util.List;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;

public interface GameControllerListener<
		T extends TermInterface,
		S extends StateInterface<T,S>,
		PlayerType
		>{

	void gameStarted(Match<T, S, PlayerType> match, S currentState);

	void gameStep(List<Move<T>> moves, S currentState);

	void gameStopped(S currentState, List<Integer> goalValues);

}
