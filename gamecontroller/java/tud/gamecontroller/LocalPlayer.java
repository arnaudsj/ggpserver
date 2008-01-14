package tud.gamecontroller;

public abstract class LocalPlayer implements Player {
	
	protected State currentState;
	protected Role role;
	protected int startclock;
	protected int playclock;
	
	public LocalPlayer() {
	}

	public void gameStart(GameInterface game, Role role, int startclock, int playclock) {
		currentState=game.getInitialState();
		this.role=role;
		this.startclock=startclock;
		this.playclock=playclock;
	}

	public Move gamePlay(Move[] priormoves) {
		if(priormoves!=null){
			currentState=currentState.getSuccessor(priormoves);
		}
		return getNextMove();
	}

	protected abstract Move getNextMove();

	public void gameStop(Move[] priormoves) {
		// TODO Auto-generated method stub
		
	}

}
