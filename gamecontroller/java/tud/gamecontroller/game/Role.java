package tud.gamecontroller.game;

import tud.gamecontroller.term.TermDelegator;
import tud.gamecontroller.term.TermInterface;

public class Role<T extends TermInterface> extends TermDelegator<T> implements RoleInterface {

	public Role(T term) {
		super(term);
	}

}
