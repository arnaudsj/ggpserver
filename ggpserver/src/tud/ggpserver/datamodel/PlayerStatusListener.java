package tud.ggpserver.datamodel;

import tud.gamecontroller.term.TermInterface;

public interface PlayerStatusListener<TermType extends TermInterface, ReasonerStateInfoType> {

	public abstract void notifyStatusChange(RemotePlayerInfo player);

}