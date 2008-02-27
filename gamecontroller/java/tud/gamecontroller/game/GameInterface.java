package tud.gamecontroller.game;

import java.util.List;

import tud.aux.NamedObject;

public interface GameInterface<
		RoleType,
		StateType> extends NamedObject{

	public abstract StateType getInitialState();

	public abstract int getNumberOfRoles();

	/**
	 * 
	 * @param roleindex index of the role of the game (0&lt;=roleindex&lt;getNumberOfRoles())
	 * @return the roleindex-th role
	 */
	public abstract RoleType getRole(int roleindex);
	
	public abstract List<? extends RoleType> getOrderedRoles();

	/**
	 * @return the GDL game description in infix KIF format without comments and linebreaks
	 */
	public abstract String getKIFGameDescription();
}