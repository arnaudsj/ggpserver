package tud.gamecontroller.game.impl;

import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.term.TermDelegator;
import tud.gamecontroller.term.TermInterface;

public class SeesTerm<T extends TermInterface> extends TermDelegator<T> implements FluentInterface<T> {
	
	public SeesTerm(T term) {
		super(term);
	}
	
}
