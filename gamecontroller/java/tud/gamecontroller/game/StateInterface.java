package tud.gamecontroller.game;

import java.util.Collection;

public interface StateInterface<
	RoleType,
	MoveType,
	FluentType,
	StateType extends StateInterface<RoleType, MoveType, FluentType, StateType>> {

	boolean isTerminal();

	StateType getSuccessor(JointMoveInterface<? extends RoleType,? extends MoveType> jointmove);

	boolean isLegal(RoleType role, MoveType move);

	MoveType getLegalMove(RoleType role);

	int getGoalValue(RoleType role);

	Collection<? extends MoveType> getLegalMoves(RoleType role);

	Collection<? extends FluentType> getFluents();
}