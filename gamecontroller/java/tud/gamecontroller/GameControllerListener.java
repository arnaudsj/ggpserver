package tud.gamecontroller;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.State;
import tud.gamecontroller.players.Player;

public interface GameControllerListener {

	void gameStarted(Match match, Player[] players, State currentState);

	void gameStep(Move[] priormoves, State currentState);

	void gameStopped(State currentState, int[] goalValues);

}
