package tud.gamecontroller;

import java.io.File;

import cs227b.teamIago.gameProver.GameSimulator;
import cs227b.teamIago.parser.Axioms;

public class Game implements GameInterface {

	private GameSimulator gameSim;
	private String gameDescription;
	private String name;
		
	private Game(String gameDescription, String name) {
		this.gameDescription=gameDescription;
		this.name=name;
		gameSim=new GameSimulator(false, false);
		gameSim.ParseDescIntoTheory(gameDescription);
	}

	public static Game readFromFile(String filename) {
		String gameDescription=Axioms.loadStringFromFile(filename);
		return new Game(gameDescription, (new File(filename)).getName());
	}

	public int getNumberOfRoles() {
		return gameSim.GetRoles().size();
	}

	public State getInitialState() {
		gameSim.SimulateStart();
		return new State(gameSim, gameSim.getTheory().getState());
	}

	public Role getRole(int roleindex) {
		return new Role(gameSim.GetRoles().get(roleindex-1));
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
