package tud.gamecontroller.game;

import tud.gamecontroller.term.TermDelegator;
import tud.gamecontroller.term.TermInterface;

public class Fluent<T extends TermInterface> extends TermDelegator<T> implements FluentInterface {

	public Fluent(T term) {
		super(term);
	}

}
