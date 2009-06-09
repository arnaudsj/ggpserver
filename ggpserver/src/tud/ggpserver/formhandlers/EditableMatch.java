package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
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
}
