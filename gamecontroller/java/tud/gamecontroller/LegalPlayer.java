package tud.gamecontroller;

public class LegalPlayer extends LocalPlayer {

	public Move getNextMove() {
		return currentState.getLegalMove(role);
	}
	
	public String toString(){
		return "LegalPlayer";
	}

}
