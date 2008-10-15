package tud.gamecontroller.game;

import java.util.Collection;
import java.util.List;

public interface ReasonerInterface<
	TermType,
	ReasonerStateInfoType> {

	List<? extends RoleInterface<TermType>> GetRoles();

	ReasonerStateInfoType getInitialState();

	boolean isTerminal(ReasonerStateInfoType state);

	ReasonerStateInfoType getSuccessorState(ReasonerStateInfoType state, JointMoveInterface<TermType> jointMove);

	boolean isLegal(ReasonerStateInfoType state, RoleInterface<TermType> role, MoveInterface<TermType> move);

	int GetGoalValue(ReasonerStateInfoType state, RoleInterface<TermType> role);

	Collection<? extends MoveInterface<TermType>> GetLegalMoves(ReasonerStateInfoType state, RoleInterface<TermType> role);

	Collection<? extends FluentInterface<TermType>> getFluents(ReasonerStateInfoType state);

	String getKIFGameDescription();

}