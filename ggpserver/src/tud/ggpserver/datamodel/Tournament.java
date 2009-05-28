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

package tud.ggpserver.datamodel;

import java.util.LinkedList;
import java.util.List;

import tud.gamecontroller.term.TermInterface;

public class Tournament<TermType extends TermInterface, ReasonerStateInfoType> {
	private final String tournamentID;
	private final User owner;
	
	private List<Match<TermInterface, ReasonerStateInfoType>> matches = new LinkedList<Match<TermInterface,ReasonerStateInfoType>>();

	public Tournament(final String tournamentID, final User owner) {
		this.tournamentID = tournamentID;
		this.owner = owner;
	}

	public List<Match<TermInterface, ReasonerStateInfoType>> getMatches() {
		// TODO
		return matches;
	}

	public void addMatch(Match<TermInterface, ReasonerStateInfoType> match) {
		// TODO
		this.matches.add(match);
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public User getOwner() {
		return owner;
	}
}
