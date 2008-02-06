package tud.gamecontroller.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;

public class RandomPlayer<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends LocalPlayer<T,S> {

	private Random random;
	
	public RandomPlayer(String name){
		super(name);
		random=new Random();
	}
	
	public Move<T> getNextMove() {
		List<Move<T>> legalmoves=new ArrayList<Move<T>>(currentState.getLegalMoves(role));
		int i=random.nextInt(legalmoves.size());
		return legalmoves.get(i);
	}
}
