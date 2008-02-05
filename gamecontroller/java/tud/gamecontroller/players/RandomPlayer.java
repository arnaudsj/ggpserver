package tud.gamecontroller.players;

import java.util.Collection;
import java.util.Random;

import tud.gamecontroller.game.Move;

public class RandomPlayer extends LocalPlayer {

	private Random random;
	
	public RandomPlayer(String name){
		super(name);
		random=new Random();
	}
	
	public Move getNextMove() {
		Collection<Move> l=currentState.getLegalMoves(role);
		int i=random.nextInt(l.size());
		return l.toArray(new Move[0])[i];
	}
}
