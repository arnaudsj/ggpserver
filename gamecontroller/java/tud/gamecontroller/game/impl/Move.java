package tud.gamecontroller.game.impl;

import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.term.TermDelegator;
import tud.gamecontroller.term.TermInterface;

public class Move<T extends TermInterface> extends TermDelegator<T> implements MoveInterface<T> {

	public Move(T term) {
		super(term);
	}

}
