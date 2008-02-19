package tud.gamecontroller.game;

import java.util.List;

public class Match<
		T extends TermInterface,
		S extends StateInterface<T,S>,
		PlayerType
		> {
	private String matchID;
	private GameInterface<T, S> game;
	private int startclock;
	private int playclock;
	private List<PlayerType> players;
	
	public Match(String matchID, GameInterface<T, S> game, int startclock, int playclock, List<PlayerType> players){
		this.matchID=matchID;
		this.game=game;
		this.startclock=startclock;
		this.playclock=playclock;
		this.players=players;
	}

	public String getMatchID() {
		return matchID;
	}

	public GameInterface<T, S> getGame() {
		return game;
	}

	public int getStartclock() {
		return startclock;
	}

	public int getPlayclock() {
		return playclock;
	}

	public List<PlayerType> getPlayers() {
		return players;
	}

	public void setPlayers(List<PlayerType> players) {
		this.players = players;
	}
}
