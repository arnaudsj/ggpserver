package tud.gamecontroller;

public interface Player {
	public void gameStart(GameInterface game, Role role, int startclock, int playclock);
	public Move gamePlay(Move[] priormoves);
	public void gameStop(Move[] priormoves);
}
