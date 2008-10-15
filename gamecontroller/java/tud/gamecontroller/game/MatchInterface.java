package tud.gamecontroller.game;

import java.util.Collection;
import java.util.List;

import tud.gamecontroller.players.Player;

public interface MatchInterface<TermType, StateType extends StateInterface<TermType, ? extends StateType>> {

	String getMatchID();

	GameInterface<TermType,StateType> getGame();

	int getStartclock();

	int getPlayclock();

	Collection<? extends Player<TermType>> getPlayers();

	List<? extends Player<TermType>> getOrderedPlayers();

	Player<TermType> getPlayer(RoleInterface<TermType> role);

}