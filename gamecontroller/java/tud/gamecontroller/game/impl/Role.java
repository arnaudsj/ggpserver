package tud.gamecontroller.game.impl;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.TermDelegator;
import tud.gamecontroller.term.TermInterface;

public class Role<T extends TermInterface> extends TermDelegator<T> implements RoleInterface<T> {

	public Role(T term) {
		super(term);
	}

}
