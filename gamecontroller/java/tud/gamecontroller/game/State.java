package tud.gamecontroller.game;

import java.util.Collection;

public class State<
	RoleType,
	MoveType,
	FluentType extends FluentInterface,
	ReasonerStateInfoType
	> implements StateInterface<
			RoleType,
			MoveType,
			FluentType,
			State<RoleType, MoveType, FluentType, ReasonerStateInfoType>> {

	protected ReasonerInterface<RoleType, MoveType,	FluentType,	ReasonerStateInfoType> reasoner;
	protected ReasonerStateInfoType stateInformation; 

	public State(ReasonerInterface<RoleType, MoveType,	FluentType,	ReasonerStateInfoType> reasoner, ReasonerStateInfoType stateInformation){
		this.reasoner=reasoner;
		this.stateInformation=stateInformation;
	}
	
	public boolean isTerminal() {
		return reasoner.isTerminal(stateInformation);
	}

	public State<RoleType, MoveType, FluentType, ReasonerStateInfoType> getSuccessor(JointMoveInterface<? extends RoleType,? extends MoveType> jointMove) {
		return new State<RoleType, MoveType, FluentType, ReasonerStateInfoType>(reasoner, reasoner.getSuccessorState(stateInformation, jointMove));
	}

	public boolean isLegal(RoleType role, MoveType move) {
		return reasoner.isLegal(stateInformation, role, move);
	}

	public int getGoalValue(RoleType role) {
		return reasoner.GetGoalValue(stateInformation, role);
	}

	public MoveType getLegalMove(RoleType role) {
		return getLegalMoves(role).iterator().next();
	}

	public Collection<? extends MoveType> getLegalMoves(RoleType role) {
		return reasoner.GetLegalMoves(stateInformation, role);
	}
	
	public Collection<? extends FluentType> getFluents() {
		return reasoner.getFluents(stateInformation);
	}

	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append('(');
		for(FluentInterface f:getFluents()){
			sb.append(f.getKIFForm());
			sb.append(" ");
		}
		sb.append(')');
		return sb.toString();
	}
}
