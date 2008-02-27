package tud.gamecontroller.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPlayer<
	RoleType,
	MoveType> extends LocalPlayer<RoleType, MoveType>  {

	private Random random;
	
	public RandomPlayer(String name){
		super(name);
		random=new Random();
	}
	
	public MoveType getNextMove() {
		List<MoveType> legalmoves=new ArrayList<MoveType>(currentState.getLegalMoves(role));
		int i=random.nextInt(legalmoves.size());
		return legalmoves.get(i);
	}
}
