package tud.gamecontroller;

import java.util.List;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.players.Player;

public interface GameControllerListener<
		T extends TermInterface,
		S extends StateInterface<T,S>
		>{

	void gameStarted(Match<T,S> match, List<Player<T,S>> players, S currentState);

	void gameStep(List<Move<T>> moves, S currentState);

	void gameStopped(S currentState, int[] goalValues);

}
