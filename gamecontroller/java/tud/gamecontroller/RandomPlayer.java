package tud.gamecontroller;

import java.util.Collection;
import java.util.Random;

public class RandomPlayer extends LocalPlayer {

	private Random random;
	
	public RandomPlayer(){
		super();
		random=new Random();
	}
	
	public Move getNextMove() {
		Collection<Move> l=currentState.getLegalMoves(role);
		int i=random.nextInt(l.size());
		return l.toArray(new Move[0])[i];
	}
	
	public String toString(){
		return "RandomPlayer";
	}

	public String getName() {
		return "Random";
	}

}
