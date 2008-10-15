package tud.gamecontroller.game;

import java.util.List;
import java.util.Map;

public interface JointMoveInterface<TermType>
	extends Map<RoleInterface<TermType>,MoveInterface<TermType>>{

	List<MoveInterface<TermType>> getOrderedMoves();

	String getKIFForm();

}