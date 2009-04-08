package tud.ggpserver.datamodel;

import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.util.HashCodeUtil;

public class Game<TermType extends TermInterface, ReasonerStateInfoType> extends tud.gamecontroller.game.impl.Game<TermType, ReasonerStateInfoType> {
	/**
	 * Use DBConnector.getGame() instead
	 */
	protected Game(String gameDescription, String name,
			ReasonerInterface<TermType, ReasonerStateInfoType> reasoner) {
		super(gameDescription, name, reasoner);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Game) {
			Game other = (Game) obj;
			return other.getName().equals(getName());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.SEED, getName());
	}
}
