/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.datamodel.statistics;

import java.util.List;
import java.util.Map;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.Game;

public class GameRoleStatistics<TermType extends TermInterface, ReasonerStateInfoType> {
	
	private Game<TermType, ReasonerStateInfoType> game;
	private Map<? extends RoleInterface<TermType>, PerformanceInformation> informationPerRole;
	

	public GameRoleStatistics(Game<TermType, ReasonerStateInfoType> game,
			Map<? extends RoleInterface<TermType>, PerformanceInformation> informationPerRole) {
		this.game = game;
		this.informationPerRole = informationPerRole;
	}

	public Game<TermType, ReasonerStateInfoType> getGame() {
		return game;
	}

	public Map<? extends RoleInterface<TermType>, PerformanceInformation> getInformationPerRole() {
		return informationPerRole;
	}
	
	public List<? extends RoleInterface<TermType>> getOrderedRoles() {
		return game.getOrderedRoles();
	}

}
