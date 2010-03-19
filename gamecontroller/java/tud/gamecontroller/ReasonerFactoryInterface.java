package tud.gamecontroller;

import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.term.TermFactoryInterface;
import tud.gamecontroller.term.TermInterface;

public interface ReasonerFactoryInterface<TermType extends TermInterface, ReasonerStateInfoType> {

	public abstract ReasonerInterface<TermType, ReasonerStateInfoType> createReasoner(
			String gameDescription, String gameName);

	public abstract TermFactoryInterface<TermType> getTermFactory();

}