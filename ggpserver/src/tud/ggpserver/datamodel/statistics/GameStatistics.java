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

import java.util.Map;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.TermInterface;

public class GameStatistics<TermType extends TermInterface, ReasonerStateInfoType> {
	private GameRoleStatistics<TermType, ReasonerStateInfoType> gameRoleStatistics = null;
	private GamePlayerStatistics<TermType, ReasonerStateInfoType> gamePlayerStatistics = null;
	private Map<RoleInterface<TermType>, GamePlayerStatistics<TermType, ReasonerStateInfoType>> gamePlayerStatisticsPerRole = null;

	public GameStatistics(
			GameRoleStatistics<TermType, ReasonerStateInfoType> gameRoleStatistics,
			GamePlayerStatistics<TermType, ReasonerStateInfoType> gamePlayerStatistics,
			Map<RoleInterface<TermType>, GamePlayerStatistics<TermType, ReasonerStateInfoType>> gamePlayerStatisticsPerRole) {
		this.gameRoleStatistics = gameRoleStatistics;
		this.gamePlayerStatistics = gamePlayerStatistics;
		this.gamePlayerStatisticsPerRole = gamePlayerStatisticsPerRole;
	}

	public GameRoleStatistics<TermType, ReasonerStateInfoType> getGameRoleStatistics() {
		return gameRoleStatistics;
	}

	public GamePlayerStatistics<TermType, ReasonerStateInfoType> getGamePlayerStatistics() {
		return gamePlayerStatistics;
	}

	public Map<RoleInterface<TermType>, GamePlayerStatistics<TermType, ReasonerStateInfoType>> getGamePlayerStatisticsPerRole() {
		return gamePlayerStatisticsPerRole;
	}

}
