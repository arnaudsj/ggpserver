package tud.gamecontroller;

import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.term.TermInterface;

public interface ReasonerFactory<TermType extends TermInterface, ReasonerStateInfoType> {

	public abstract ReasonerInterface<TermType, ReasonerStateInfoType> createReasoner(
			String gameDescription, String gameName);

}