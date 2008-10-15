package tud.gamecontroller.term;

import tud.gamecontroller.aux.InvalidKIFException;

public interface TermFactoryInterface<TermType> {

	public TermType getTermFromKIF(String kif) throws InvalidKIFException;
	
}
