package tud.gamecontroller.game;

import java.util.Collection;

public interface StateInterface<
	TermType,
	StateType extends StateInterface<TermType, StateType>> {

	boolean isTerminal();

	StateType getSuccessor(JointMoveInterface<TermType> jointmove);

	boolean isLegal(RoleInterface<TermType> role, MoveInterface<TermType> move);

	MoveInterface<TermType> getLegalMove(RoleInterface<TermType> role);

	int getGoalValue(RoleInterface<TermType> role);

	Collection<? extends MoveInterface<TermType>> getLegalMoves(RoleInterface<TermType> role);

	Collection<? extends FluentInterface<TermType>> getFluents();
}