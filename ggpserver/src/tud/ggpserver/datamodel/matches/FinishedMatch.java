/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 
                  2010 Stephan Schiffel <stephan.schiffel@gmx.de>
                  
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.User;

public class FinishedMatch<TermType extends TermInterface, ReasonerStateInfoType>
		extends StoppedMatch<TermType, ReasonerStateInfoType> {

	
	private final Map<? extends RoleInterface<TermType>, Integer> goalValues;
	
	public FinishedMatch(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			Date startTime,
			boolean scrambled,
			String tournamentID,
			double weight,
			User owner, AbstractDBConnector<TermType, ReasonerStateInfoType> db,
			Map<? extends RoleInterface<TermType>, Integer> goalValues) {
		super(matchID, game, startclock, playclock, rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, db);
		this.goalValues = goalValues;
	}

	@Override
	public String getStatus() {
		return ServerMatch.STATUS_FINISHED;
	}

	@Override
	public Map<RoleInterface<TermType>, Integer> getGoalValues() {
		// defensive copy needed here, otherwise EditableMatch will fail
		return new HashMap<RoleInterface<TermType>, Integer>(goalValues);
	}
}
