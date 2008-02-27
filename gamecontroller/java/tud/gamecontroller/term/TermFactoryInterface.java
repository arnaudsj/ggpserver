package tud.gamecontroller.term;

import tud.gamecontroller.aux.InvalidKIFException;

public interface TermFactoryInterface<T extends TermInterface> {

	public T getTermFromKIF(String kif) throws InvalidKIFException;
	
}
