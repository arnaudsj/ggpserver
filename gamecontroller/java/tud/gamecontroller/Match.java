package tud.gamecontroller;

public class Match {
	private String matchID;
	private GameInterface game;
	private int startclock;
	private int playclock;
	
	public Match(String matchID, GameInterface game, int startclock, int playclock){
		this.matchID=matchID;
		this.game=game;
		this.startclock=startclock;
		this.playclock=playclock;
	}

	public String getMatchID() {
		return matchID;
	}

	public GameInterface getGame() {
		return game;
	}

	public int getStartclock() {
		return startclock;
	}

	public int getPlayclock() {
		return playclock;
	}
}
