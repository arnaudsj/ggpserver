package tud.gamecontroller;

public abstract class LocalPlayer implements Player {
	
	protected Match match;
	protected State currentState;
	protected Role role;
	protected int startclock;
	protected int playclock;
	
	public LocalPlayer() {
	}

	public void gameStart(Match match, Role role) {
		this.match=match;
		currentState=match.getGame().getInitialState();
		this.role=role;
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
