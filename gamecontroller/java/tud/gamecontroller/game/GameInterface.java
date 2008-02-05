package tud.gamecontroller.game;

public interface GameInterface {

	public int getNumberOfRoles();

	public State getInitialState();

	public Role getRole(int roleindex);

	public String getGameDescription();

}