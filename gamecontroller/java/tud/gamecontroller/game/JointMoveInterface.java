package tud.gamecontroller.game;

import java.util.List;
import java.util.Map;

public interface JointMoveInterface<RoleType, MoveType> extends Map<RoleType,MoveType>{

	List<MoveType> getOrderedMoves();

	String getKIFForm();

}