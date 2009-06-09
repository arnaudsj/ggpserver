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

package tud.ggpserver.tests;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.junit.Test;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.scheduler.JavaProverTournamentScheduler;
import tud.ggpserver.scheduler.TournamentScheduler;

public class TournamentSchedulerTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testAbort() throws SQLException, InterruptedException, RemoteException, NamingException {
		RoundRobinSchedulerTest.setupJNDI();
		
		AbstractDBConnector db = DBConnectorFactory.getDBConnector();
		Tournament tournament = db.getTournament("t1");
		Game game = db.getGame("othello");
		List<RoleInterface> orderedRoles = game.getOrderedRoles();
		Map<RoleInterface, PlayerInfo> rolesToPlayerInfos = new HashMap<RoleInterface, PlayerInfo>();		
		
		int roleIndex = 0;
		for (RoleInterface role : orderedRoles) {
			rolesToPlayerInfos.put(role, db.getPlayerInfo("test" + (roleIndex + 1)));
			roleIndex++;
		}
		
		NewMatch match = db.createMatch(game, 5, 5, rolesToPlayerInfos, tournament);
		TournamentScheduler scheduler = JavaProverTournamentScheduler.getInstance(tournament);
		
		scheduler.start(match);
		Thread.sleep(500);
		RunningMatch runningMatch = db.getRunningMatch(match.getMatchID());
		scheduler.abort(runningMatch);
	}

}
