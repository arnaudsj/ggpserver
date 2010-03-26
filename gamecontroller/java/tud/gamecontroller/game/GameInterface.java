/*
    Copyright (C) 2008,2010 Stephan Schiffel <stephan.schiffel@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller.game;

import java.util.List;

import tud.auxiliary.NamedObject;
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.auxiliary.InvalidKIFException;

public interface GameInterface<
	TermType,
	StateType
	> extends NamedObject{
	
	public StateType getInitialState();

	public int getNumberOfRoles();

	/**
	 * 
	 * @param roleindex index of the role of the game (0&lt;=roleindex&lt;getNumberOfRoles())
	 * @return the roleindex-th role
	 */
	public RoleInterface<TermType> getRole(int roleindex);
	
	public List<? extends RoleInterface<TermType>> getOrderedRoles();

	/**
	 * @return the GDL game description in infix KIF format without comments and linebreaks
	 */
	public String getKIFGameDescription();

	public String getStylesheet();
	
	public GDLVersion getGdlVersion();
	
	/**
	 * This method is more or less the inverse of StateInterface.toString() it parses the given String and returns a state of the game.
	 * @param stringState a string containing a list of fluents in infix KIF notation e.g., "((step 1) (cell 1 1 b) ... (control xplayer))"
	 * @return the state object
	 */
	public StateType getStateFromString(String stringState) throws InvalidKIFException;

	/**
	 * returns the role that plays the nature (i.e., the role named "random")
	 * 
	 * This role may not actually be a role of the game (e.g., if there is no random role). 
	 */
	public RoleInterface<TermType> getNatureRole();

	public RoleInterface<TermType> getRoleByName(String roleName);

	public TermType getTermFromString(String kifTermString) throws InvalidKIFException;
}