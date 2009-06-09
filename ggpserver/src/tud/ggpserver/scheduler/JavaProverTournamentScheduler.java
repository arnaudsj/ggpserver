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

package tud.ggpserver.scheduler;

import java.util.HashMap;
import java.util.Map;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.Tournament;
import cs227b.teamIago.util.GameState;

public class JavaProverTournamentScheduler extends TournamentScheduler<Term, GameState> {
	private static final Map<Tournament<Term, GameState>, JavaProverTournamentScheduler> instances 
			= new HashMap<Tournament<Term, GameState>, JavaProverTournamentScheduler>();

	private JavaProverTournamentScheduler() {
	}

	@SuppressWarnings("unchecked")
	public static synchronized JavaProverTournamentScheduler getInstance(Tournament tournament) {
		JavaProverTournamentScheduler result = instances.get(tournament);
		if (result == null) {
			result = new JavaProverTournamentScheduler();
			instances.put(tournament, result);
		}

		return result;
	}
}
