package tud.gamecontroller.game;

import tud.gamecontroller.term.GameObjectInterface;

public interface RoleInterface<TermType> extends GameObjectInterface{
	TermType getTerm();
}