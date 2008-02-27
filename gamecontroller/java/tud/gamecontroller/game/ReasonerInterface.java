package tud.gamecontroller.game;

import java.util.Collection;
import java.util.List;

public interface ReasonerInterface<
	RoleType,
	MoveType,
	FluentType,
	ReasonerStateInfoType> {

	List<? extends RoleType> GetRoles();

	ReasonerStateInfoType getInitialState();

	boolean isTerminal(ReasonerStateInfoType state);

	ReasonerStateInfoType getSuccessorState(ReasonerStateInfoType state, JointMoveInterface<? extends RoleType,? extends MoveType> jointMove);

	boolean isLegal(ReasonerStateInfoType state, RoleType role, MoveType move);

	int GetGoalValue(ReasonerStateInfoType state, RoleType role);

	Collection<? extends MoveType> GetLegalMoves(ReasonerStateInfoType state, RoleType role);

	Collection<? extends FluentType> getFluents(ReasonerStateInfoType state);

	String getKIFGameDescription();

}