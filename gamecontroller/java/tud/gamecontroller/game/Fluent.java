package tud.gamecontroller.game;

public class Fluent<T extends TermInterface> extends TermDelegator<T> {

	public Fluent(T term) {
		super(term);
	}

}
