package tud.gamecontroller;

public interface Player {
	public void gameStart(Match match, Role role);
	public Move gamePlay(Move[] priormoves);
	public void gameStop(Move[] priormoves);
	public String getName();
}
