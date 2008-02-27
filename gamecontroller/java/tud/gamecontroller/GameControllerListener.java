package tud.gamecontroller;

import java.util.Map;

import tud.gamecontroller.game.JointMoveInterface;

public interface GameControllerListener<
		RoleType,
		MoveType,
		MatchType,
		StateType
		>{

	void gameStarted(MatchType match, StateType currentState);

	void gameStep(JointMoveInterface<? extends RoleType, ? extends MoveType> jointmove, StateType currentState);

	void gameStopped(StateType currentState, Map<? extends RoleType, Integer> goalValues);

}
