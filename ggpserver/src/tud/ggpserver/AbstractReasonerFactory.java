package tud.ggpserver;

import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractReasonerFactory<TermType extends TermInterface, ReasonerStateInfoType> {
	public abstract MoveFactoryInterface<? extends MoveInterface<TermType>> getMoveFactory();

	public abstract ReasonerInterface<TermType, ReasonerStateInfoType> getReasoner(String gameDescription, String name);
}
