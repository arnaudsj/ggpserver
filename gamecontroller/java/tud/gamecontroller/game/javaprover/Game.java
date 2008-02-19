package tud.gamecontroller.game.javaprover;

import java.io.File;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.Role;
import cs227b.teamIago.parser.Axioms;

public class Game implements GameInterface<Term, State> {

	private Reasoner reasoner;
	private String name;
		
	private Game(String gameDescription, String name) {
		this.name=name;
		reasoner=new Reasoner(gameDescription);
		
	}

	public static Game readFromFile(String filename) {
		String gameDescription=Axioms.loadStringFromFile(filename);
		return new Game(gameDescription, (new File(filename)).getName());
	}

	public int getNumberOfRoles() {
		return reasoner.GetRoles().size();
	}

	public State getInitialState() {
		return new State(reasoner, reasoner.getInitialState());
	}

	public Role<Term> getRole(int roleindex) {
		return new Role<Term>(new Term(reasoner.GetRoles().get(roleindex-1)));
	}

	public String getName() {
		return name;
	}

	public String getKIFGameDescription() {
		return reasoner.getKIFGameDescription();
	}


}
