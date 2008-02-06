package tud.gamecontroller.game;

public interface GameInterface<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> {

	public int getNumberOfRoles();

	public S getInitialState();

	public Role<T> getRole(int roleindex);

	public String getGameDescription();

}