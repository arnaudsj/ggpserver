package tud.gamecontroller.game;

public class Role<T extends TermInterface> extends TermDelegator<T> {

	public Role(T term) {
		super(term);
	}

}
