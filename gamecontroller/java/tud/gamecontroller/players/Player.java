package tud.gamecontroller.players;

import java.util.List;

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.Role;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;

public interface Player<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> {
	public void gameStart(Match<T,S> match, Role<T> role, MessageSentNotifier notifier);
	public Move<T> gamePlay(List<Move<T>> priormoves, MessageSentNotifier notifier);
	public void gameStop(List<Move<T>> priormoves, MessageSentNotifier notifier);
	public String getName();
}
