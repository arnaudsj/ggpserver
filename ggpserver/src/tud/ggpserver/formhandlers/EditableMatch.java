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

package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.matches.NewMatch;


public class EditableMatch {
	private final AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	private final NewMatch shadowedMatch;
	private GameInterface game;
	private int startclock;
	private int playclock;
	private List<PlayerInfo> playerInfos;
	

	@SuppressWarnings("unchecked")
	public EditableMatch(String matchID) throws SQLException {
		shadowedMatch = db.getNewMatch(matchID);
		game = shadowedMatch.getGame();
		startclock = shadowedMatch.getStartclock();
		playclock = shadowedMatch.getPlayclock();
		playerInfos = shadowedMatch.getOrderedPlayerInfos();
	}


	public void setGame(String gameName) throws SQLException {
		game = db.getGame(gameName);
	}
	
	public void setStartclock(int startclock) {
		if (startclock <= 0) {
			throw new IllegalArgumentException("Startclock cannot be 0!");
		}
		this.startclock = startclock;
	}

	public void setPlayclock(int playclock) {
		if (playclock <= 0) {
			throw new IllegalArgumentException("Playclock cannot be 0!");
		}
		this.playclock = playclock;
	}

	public void setPlayerInfo(int roleIndex, String playerName) throws SQLException {
		playerInfos.set(roleIndex, db.getPlayerInfo(playerName));
	}

	@SuppressWarnings("unchecked")
	public void commit() throws SQLException {
		if (shadowedMatch.getStartclock() != startclock) {
			db.setMatchStartclock(shadowedMatch.getMatchID(), startclock);
		}
		
		if (shadowedMatch.getPlayclock() != playclock) {
			db.setMatchPlayclock(shadowedMatch.getMatchID(), playclock);
		}
		
		RemotePlayerInfo duplicatePlayer = duplicateRemotePlayer(playerInfos);
		if (duplicatePlayer != null) {
			throw new IllegalArgumentException("A RemotePlayer cannot play two roles in the same game! MatchID: " + shadowedMatch.getMatchID() + ", offending player: " + duplicatePlayer.getName());
		}
		
		List<PlayerInfo> shadowedPlayerInfos = shadowedMatch.getOrderedPlayerInfos();
		for (int i = 0; i < playerInfos.size(); i++) {
			PlayerInfo shadowedPlayerInfo = shadowedPlayerInfos.get(i);
			PlayerInfo playerInfo = playerInfos.get(i);
			
			if (!playerInfo.equals(shadowedPlayerInfo)) {
				db.setMatchPlayerInfo(shadowedMatch.getMatchID(), i, playerInfo);
			}
		}
		
		// set the game last, because this will change the match id
		if (!shadowedMatch.getGame().equals(game)) {
			db.setMatchGame(shadowedMatch, game);
		}
	}


	private RemotePlayerInfo duplicateRemotePlayer(List<PlayerInfo> shadowedPlayerInfos) {
		List<RemotePlayerInfo> seenPlayers = new ArrayList<RemotePlayerInfo>();
		
		for (PlayerInfo playerInfo : shadowedPlayerInfos) {
			if (playerInfo instanceof RemotePlayerInfo) {
				if (seenPlayers.contains(playerInfo)) {
					return (RemotePlayerInfo) playerInfo;
				}
				seenPlayers.add((RemotePlayerInfo) playerInfo);
			}
		}
		
		return null;
	}
}