package tud.gamecontroller.game;

import tud.gamecontroller.aux.InvalidKIFException;

public interface MoveFactoryInterface<MoveType> {

	public MoveType getMoveFromKIF(String kif) throws InvalidKIFException;
	
}
