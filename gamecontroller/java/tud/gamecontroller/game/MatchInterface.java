package tud.gamecontroller.game;

import java.util.Collection;
import java.util.List;

import tud.gamecontroller.players.Player;

public interface MatchInterface<RoleType, GameType, PlayerType extends Player<?, ?, ?>> {

	String getMatchID();

	GameType getGame();

	int getStartclock();

	int getPlayclock();

	Collection<? extends PlayerType> getPlayers();

	List<? extends PlayerType> getOrderedPlayers();

	PlayerType getPlayer(RoleType role);

}