package tud.gamecontroller.game;

import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.scrambling.IdentityGameScrambler;

public class Match<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> {
	private String matchID;
	private GameInterface<T, S> game;
	private int startclock;
	private int playclock;
	private GameScramblerInterface scrambler;
	
	public Match(String matchID, GameInterface<T, S> game, int startclock, int playclock){
		this(matchID, game, startclock, playclock, null);
	}
	public Match(String matchID, GameInterface<T, S> game, int startclock, int playclock, GameScramblerInterface scrambler){
		this.matchID=matchID;
		this.game=game;
		this.startclock=startclock;
		this.playclock=playclock;
		if(scrambler!=null){
			this.scrambler=scrambler;
		}else{
			this.scrambler=new IdentityGameScrambler();
		}
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

	public GameScramblerInterface getScrambler() {
		return scrambler;
	}
}
