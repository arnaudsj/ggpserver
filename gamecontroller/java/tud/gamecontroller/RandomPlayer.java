package tud.gamecontroller;

import java.util.Collection;
import java.util.Random;

public class RandomPlayer extends LocalPlayer {

	public Move getNextMove() {
		Collection l=currentState.getLegalMoves(role);
		int i=(new Random()).nextInt(l.size());
		return (Move)l.toArray()[i];
	}
	
	public String toString(){
		return "RandomPlayer";
	}
}
