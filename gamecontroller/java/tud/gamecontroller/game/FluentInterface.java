package tud.gamecontroller.game;

import tud.gamecontroller.term.GameObjectInterface;

public interface FluentInterface<TermType> extends GameObjectInterface{
	TermType getTerm();
}
