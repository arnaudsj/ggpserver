/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>
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

import java.util.Date;
import java.util.List;

import tud.gamecontroller.auxiliary.Pair;
import tud.auxiliary.NamedObject;
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.game.impl.Match;

public interface GameInterface<
	TermType,
	StateType
	> extends NamedObject{
	
	public abstract StateType getInitialState();

	public abstract int getNumberOfRoles();

	/**
	 * 
	 * @param roleindex index of the role of the game (0&lt;=roleindex&lt;getNumberOfRoles())
	 * @return the roleindex-th role
	 */
	public abstract RoleInterface<TermType> getRole(int roleindex);
	
	public abstract List<? extends RoleInterface<TermType>> getOrderedRoles();

	/**
	 * @return the GDL game description in infix KIF format without comments and linebreaks
	 */
	public abstract String getKIFGameDescription();

	public abstract String getStylesheet();
	
	public abstract String getXMLViewFor(
			Match<?, ?> match,
			Pair<Date,String> stringState,
			List<List<String>> stringMoves,
			RoleInterface<TermType> role);
	
	public GDLVersion getGdlVersion();
	
}