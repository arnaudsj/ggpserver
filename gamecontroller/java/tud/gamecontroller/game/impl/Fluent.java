package tud.gamecontroller.game.impl;

import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.term.TermDelegator;
import tud.gamecontroller.term.TermInterface;

public class Fluent<T extends TermInterface> extends TermDelegator<T> implements FluentInterface<T> {

	public Fluent(T term) {
		super(term);
	}

}
