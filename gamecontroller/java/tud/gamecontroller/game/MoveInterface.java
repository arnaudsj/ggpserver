package tud.gamecontroller.game;

import tud.gamecontroller.term.GameObjectInterface;

public interface MoveInterface<TermType> extends GameObjectInterface{
	TermType getTerm();
}