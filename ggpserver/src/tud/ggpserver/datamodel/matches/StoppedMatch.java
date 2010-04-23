/*
    Copyright (C) 2010 Stephan Schiffel <stephan.schiffel@gmx.de> 

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
import java.util.List;
import java.util.Map;

import tud.gamecontroller.auxiliary.Pair;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.datamodel.dblists.ErrorMessageAccessor;
import tud.ggpserver.datamodel.dblists.JointMovesAccessor;
import tud.ggpserver.datamodel.dblists.StaticDBBackedList;
import tud.ggpserver.datamodel.dblists.StringStateAccessor;

/**
 * StoppedMatch is a ServerMatch which has been stopped (and thus must have been started before).
 * StoppedMatch contains common features of finished and aborted matches. 
 */
public abstract class StoppedMatch<TermType extends TermInterface, ReasonerStateInfoType>
		extends StartedMatch<TermType, ReasonerStateInfoType> {

	public StoppedMatch(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			Date startTime,
			boolean scrambled,
			String tournamentID,
			double weight,
			User owner, AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		super(matchID, game, startclock, playclock, rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, db);
	}

	@Override
	public final List<List<String>> getJointMovesStrings() {
		if (jointMovesStrings == null) {
			jointMovesStrings = new StaticDBBackedList<List<String>>(new JointMovesAccessor(getMatchID(), getDB()), true); 
		}
		return jointMovesStrings;
	}

	@Override
	public final List<Pair<Date,String>> getStringStates() {
		if (stringStates == null) {
			stringStates = new StaticDBBackedList<Pair<Date,String>>(new StringStateAccessor(getMatchID(), getDB(), getGame().getStylesheet()), false);
		}
		return stringStates;
	}

	@Override
	public final List<List<GameControllerErrorMessage>> getErrorMessages() {
		if (errorMessages == null) {
			errorMessages = new StaticDBBackedList<List<GameControllerErrorMessage>>(new ErrorMessageAccessor(getMatchID(), getDB()), true);
		}
		return errorMessages;
	}	
}
