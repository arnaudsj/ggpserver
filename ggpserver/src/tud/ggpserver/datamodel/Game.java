package tud.ggpserver.datamodel;

import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.term.TermInterface;

public class Game<
	TermType extends TermInterface,
	ReasonerStateInfoType> extends tud.gamecontroller.game.impl.Game<TermType, ReasonerStateInfoType> {

	private boolean enabled;
	
	public Game(String gameDescription, String name,
			ReasonerInterface<TermType, ReasonerStateInfoType> reasoner, String stylesheet, boolean enabled) {
		super(gameDescription, name, reasoner, stylesheet);
		this.enabled=enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
