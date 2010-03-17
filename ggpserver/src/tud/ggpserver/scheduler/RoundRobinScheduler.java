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

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import cs227b.teamIago.util.GameState;

public class RoundRobinScheduler extends AbstractRoundRobinScheduler<Term, GameState> {
	
	private static RoundRobinScheduler instance = null;
	
	@SuppressWarnings("unchecked")
	private RoundRobinScheduler(AbstractDBConnector dbConnector) {
		super(dbConnector);
	}

	public static synchronized AbstractRoundRobinScheduler<?,?> getInstance(  ) {
		
		if (instance == null) {
			instance = new RoundRobinScheduler(DBConnectorFactory.getDBConnector());
		}
		
		return instance;
		
	}

	@Override
	protected MatchRunner<Term, GameState> getMatchRunner() {
		return MatchRunner.getInstance();
	}

}
