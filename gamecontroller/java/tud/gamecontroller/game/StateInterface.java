package tud.gamecontroller.game;

import java.util.Collection;
import java.util.List;

public interface StateInterface<T extends TermInterface, S extends StateInterface<T,S>> {

	boolean isTerminal();

	S getSuccessor(List<Move<T>> moves);

	boolean isLegal(Role<T> role, Move<T> move);

	Move<T> getLegalMove(Role<T> role);

	int getGoalValue(Role<T> role);

	Collection<Move<T>> getLegalMoves(Role<T> role);

	Collection<Fluent<T>> getFluents();
}