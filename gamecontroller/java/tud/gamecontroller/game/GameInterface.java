package tud.gamecontroller.game;

public interface GameInterface<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> {

	public S getInitialState();

	public int getNumberOfRoles();

	/**
	 * 
	 * @param roleindex index of the role of the game (between 1 and getNumberOfRoles())
	 * @return the roleindex-th role
	 */
	public Role<T> getRole(int roleindex);

	/**
	 * @return the GDL game description in infix KIF format without comments and linebreaks
	 */
	public String getKIFGameDescription();
}