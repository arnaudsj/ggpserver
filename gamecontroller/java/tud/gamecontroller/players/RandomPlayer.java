package tud.gamecontroller.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.term.TermInterface;

public class RandomPlayer<
	TermType extends TermInterface
	> extends LocalPlayer<TermType>  {

	private Random random;
	
	public RandomPlayer(String name){
		super(name);
		random=new Random();
	}
	
	public MoveInterface<TermType> getNextMove() {
		List<MoveInterface<TermType>> legalmoves=new ArrayList<MoveInterface<TermType>>(currentState.getLegalMoves(role));
		int i=random.nextInt(legalmoves.size());
		return legalmoves.get(i);
	}
}
