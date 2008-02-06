package tud.gamecontroller.game;

public interface TermFactoryInterface<T extends TermInterface> {

	public T getTermFromKIF(String kif) throws InvalidKIFException;
	
}
