package tud.gamecontroller;

public interface GameControllerListener {

	void gameStarted(Match match, Player[] players, State currentState);

	void gameStep(Move[] priormoves, State currentState);

	void gameStopped(State currentState, int[] goalValues);

}
