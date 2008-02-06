package tud.gamecontroller.game;

public class Move<T extends TermInterface> extends TermDelegator<T> {

	public Move(T term) {
		super(term);
	}

}
