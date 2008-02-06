package tud.gamecontroller.game;


public class Match<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> {
	private String matchID;
	private GameInterface<T, S> game;
	private int startclock;
	private int playclock;
	
	public Match(String matchID, GameInterface<T, S> game, int startclock, int playclock){
		this.matchID=matchID;
		this.game=game;
		this.startclock=startclock;
		this.playclock=playclock;
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
}
