package tud.gamecontroller.game.impl;

import tud.gamecontroller.aux.InvalidKIFException;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.term.TermFactoryInterface;
import tud.gamecontroller.term.TermInterface;

public class MoveFactory<T extends TermInterface> implements MoveFactoryInterface<Move<T>> {
	
	private TermFactoryInterface<T> termFactory; 
	
	public MoveFactory(TermFactoryInterface<T> termFactory){
		this.termFactory=termFactory;
	}
	

	public Move<T> getMoveFromKIF(String kif) throws InvalidKIFException {
		Move<T> move=null;
		T t=termFactory.getTermFromKIF(kif);
		if(t!=null && !t.isGround()){
			throw new InvalidKIFException("\""+kif+"\" is not a ground term.");
		}else if(t!=null){
			move=new Move<T>(t);
		}
		return move;
	}
	
}
