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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.User;
import tud.gamecontroller.auxiliary.Pair;

public class NewMatch<TermType extends TermInterface, ReasonerStateInfoType>
		extends ServerMatch<TermType, ReasonerStateInfoType> {

	public NewMatch(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			Date startTime,
			boolean scrambled,
			String tournamentID,
			double weight,
			User owner,
			AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		super(matchID, game, startclock, playclock, rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, db);
	}

	/**
	 * Sets this matches status to "running", and returns the new running match.
	 * This object must not be used any more after calling this method.
	 */
	public RunningMatch<TermType, ReasonerStateInfoType> toRunning() throws SQLException {
		getDB().setMatchStatus(getMatchID(), ServerMatch.STATUS_RUNNING);
		return getDB().getRunningMatch(getMatchID());
	}
	
	public ScheduledMatch<TermType, ReasonerStateInfoType> toScheduled() throws SQLException {
		getDB().setMatchStatus(getMatchID(), ServerMatch.STATUS_SCHEDULED);
		return getDB().getScheduledMatch(getMatchID());
	}
	
	@Override
	public String getStatus() {
		return ServerMatch.STATUS_NEW;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<List<GameControllerErrorMessage>> getErrorMessages() {
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<List<String>> getJointMovesStrings() {
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pair<Timestamp,String>> getStringStates() {
		return Collections.EMPTY_LIST;
	}
}
