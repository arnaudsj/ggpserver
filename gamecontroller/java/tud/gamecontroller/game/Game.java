package tud.gamecontroller.game;

import java.util.LinkedList;
import java.util.List;

public class Game<
	RoleType,
	MoveType,
	FluentType extends FluentInterface,
	ReasonerStateInfoType
	> implements GameInterface<
		RoleType,
		State<RoleType,MoveType,FluentType,ReasonerStateInfoType>> {

	private ReasonerInterface<RoleType, MoveType, FluentType, ReasonerStateInfoType> reasoner;
	private String name;
	private List<RoleType> orderedRoles=null;
		
	public Game(String gameDescription, String name, ReasonerInterface<RoleType, MoveType, FluentType, ReasonerStateInfoType> reasoner) {
		this.name=name;
		this.reasoner=reasoner;
	}

	public State<RoleType,MoveType,FluentType,ReasonerStateInfoType> getInitialState() {
		return new State<RoleType,MoveType,FluentType,ReasonerStateInfoType>(reasoner, reasoner.getInitialState());
	}

	public RoleType getRole(int roleindex) {
		return reasoner.GetRoles().get(roleindex);
	}

	public List<? extends RoleType> getOrderedRoles(){
		if(orderedRoles==null){
			orderedRoles=new LinkedList<RoleType>();
			for(int i=0;i<getNumberOfRoles();i++){
				orderedRoles.add(getRole(i));
			}
		}
		return orderedRoles;
	}

	public int getNumberOfRoles() {
		return reasoner.GetRoles().size();
	}

	public String getName() {
		return name;
	}

	public String getKIFGameDescription() {
		return reasoner.getKIFGameDescription();
	}


}
