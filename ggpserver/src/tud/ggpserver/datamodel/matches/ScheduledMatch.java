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

package tud.ggpserver.datamodel.matches;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;

public class ScheduledMatch<TermType extends TermInterface, ReasonerStateInfoType> extends NewMatch<TermType, ReasonerStateInfoType> {

	public ScheduledMatch(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			Date startTime,
			boolean scrambled,
			String tournamentID,
			double weight,
			AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		super(matchID, game, startclock, playclock, rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, db);
	}

	@Override
	public String getStatus() {
		return ServerMatch.STATUS_SCHEDULED;
	}
	
	public NewMatch<TermType, ReasonerStateInfoType> toNew() throws SQLException {
		getDB().setMatchStatus(getMatchID(), ServerMatch.STATUS_NEW);
		return getDB().getNewMatch(getMatchID());
	}
	
}
