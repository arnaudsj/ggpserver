/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

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

package tud.ggpserver.datamodel.matches;

import java.util.Date;
import java.util.Map;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;

public class FinishedMatch<TermType extends TermInterface, ReasonerStateInfoType>
		extends ServerMatch<TermType, ReasonerStateInfoType> {

	
	private final Map<? extends RoleInterface<?>, Integer> goalValues;
	
	public FinishedMatch(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			Date startTime,
			AbstractDBConnector<TermType, ReasonerStateInfoType> db,
			Map<? extends RoleInterface<?>, Integer> goalValues) {
		super(matchID, game, startclock, playclock, rolesToPlayerInfos, startTime, db);
		this.goalValues = goalValues;
	}

	@Override
	public String getStatus() {
		return ServerMatch.STATUS_FINISHED;
	}

	@Override
	public Map<? extends RoleInterface<?>, Integer> getGoalValues() {
		return goalValues;
	}
}