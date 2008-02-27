package tud.gamecontroller.game;

import tud.gamecontroller.term.TermDelegator;
import tud.gamecontroller.term.TermInterface;

public class Move<T extends TermInterface> extends TermDelegator<T> implements MoveInterface {

	public Move(T term) {
		super(term);
	}

}
