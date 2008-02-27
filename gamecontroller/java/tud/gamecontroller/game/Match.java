package tud.gamecontroller.game;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tud.gamecontroller.players.Player;

public class Match<
		RoleType,
		GameType extends GameInterface<? extends RoleType, ?>,
		PlayerType extends Player<?, ?, ?>
		> implements MatchInterface<RoleType, GameType, PlayerType> {
	private String matchID;
	private GameType game;
	private int startclock;
	private int playclock;
	private Map<? extends RoleType, ? extends PlayerType> players;
	private List<PlayerType> orderedPlayers=null;
	
	public Match(String matchID, GameType game, int startclock, int playclock, Map<? extends RoleType, ? extends PlayerType> players){
		this.matchID=matchID;
		this.game=game;
		this.startclock=startclock;
		this.playclock=playclock;
		this.players=players;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getMatchID()
	 */
	public String getMatchID() {
		return matchID;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getGame()
	 */
	public GameType getGame() {
		return game;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getStartclock()
	 */
	public int getStartclock() {
		return startclock;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getPlayclock()
	 */
	public int getPlayclock() {
		return playclock;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getPlayers()
	 */
	public Collection<? extends PlayerType> getPlayers() {
		return players.values();
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getOrderedPlayers()
	 */
	public List<? extends PlayerType> getOrderedPlayers() {
		if(orderedPlayers!=null){
			orderedPlayers=new LinkedList<PlayerType>();
			for(RoleType role:game.getOrderedRoles()){
				orderedPlayers.add(players.get(role));
			}
		}
		return orderedPlayers;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getPlayer(RoleType)
	 */
	public PlayerType getPlayer(RoleType role) {
		return players.get(role);
	}
}
