package tud.gamecontroller.players;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.Role;

public interface Player {
	public void gameStart(Match match, Role role);
	public Move gamePlay(Move[] priormoves);
	public void gameStop(Move[] priormoves);
	public String getName();
}
