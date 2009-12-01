/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>
                  2009 Stephan Schiffel <stephan.schiffel@gmx.de>

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

package tud.ggpserver.datamodel;

import java.sql.SQLException;
import java.util.List;

import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.matches.ServerMatch;

public class Tournament<TermType extends TermInterface, ReasonerStateInfoType> {
	public static final int DEFAULT_STARTCLOCK = 600;
	public static final int DEFAULT_PLAYCLOCK = 30;
	
	private final String tournamentID;
	private final User owner;

	private final AbstractDBConnector<TermType, ReasonerStateInfoType> db;
	private int nbOfMatches = -1;
	
	public static final String ROUND_ROBIN_TOURNAMENT_ID = "round_robin_tournament";
	public static final String MANUAL_TOURNAMENT_ID = "manual_matches";
	
	public Tournament(final String tournamentID, final User owner, AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		this.tournamentID = tournamentID;
		this.owner = owner;
		this.db = db;
	}

	public List<ServerMatch<TermType,ReasonerStateInfoType>> getMatches() throws SQLException {
		return db.getMatches(0, Integer.MAX_VALUE, null, null, tournamentID, null, false);
	}

	public int getNumberOfMatches() throws SQLException {
		if(nbOfMatches == -1){
			nbOfMatches = db.getRowCountMatches(null, null, tournamentID, null, false);
		}
		return nbOfMatches;
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public User getOwner() {
		return owner;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((tournamentID == null) ? 0 : tournamentID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tournament<?, ?> other = (Tournament<?, ?>) obj;
		if (tournamentID == null) {
			if (other.tournamentID != null)
				return false;
		} else if (!tournamentID.equals(other.tournamentID))
			return false;
		return true;
	}

	public boolean isDeletable() {
		if (ROUND_ROBIN_TOURNAMENT_ID.equals(tournamentID))
			return false;

		if (MANUAL_TOURNAMENT_ID.equals(tournamentID))
			return false;

		return true;
	}
	
}
