package tud.gamecontroller.playerthreads;

import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.game.MoveInterface;

public interface MoveMemory<TermType> extends ConnectionEstablishedNotifier {
	
	public MoveInterface<TermType> getMove ();
	public void setMove (MoveInterface<TermType> move);
	
}
