package tud.gamecontroller.game.impl;

import java.util.LinkedList;
import java.util.List;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.TermInterface;

public class Game<
	TermType extends TermInterface,
	ReasonerStateInfoType
	> implements GameInterface<
		TermType,
		State<TermType, ReasonerStateInfoType>> {

	private ReasonerInterface<TermType, ReasonerStateInfoType> reasoner;
	private String name;
	private List<RoleInterface<TermType>> orderedRoles=null;
		
	public Game(String gameDescription, String name, ReasonerInterface<TermType, ReasonerStateInfoType> reasoner) {
		this.name=name;
		this.reasoner=reasoner;
	}

	public State<TermType, ReasonerStateInfoType> getInitialState() {
		return new State<TermType,ReasonerStateInfoType>(reasoner, reasoner.getInitialState());
	}

	public RoleInterface<TermType> getRole(int roleindex) {
		return reasoner.GetRoles().get(roleindex);
	}

	public List<? extends RoleInterface<TermType>> getOrderedRoles(){
		if(orderedRoles==null){
			orderedRoles=new LinkedList<RoleInterface<TermType>>();
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
