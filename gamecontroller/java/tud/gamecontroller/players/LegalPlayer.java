package tud.gamecontroller.players;

public class LegalPlayer<
	RoleType,
	MoveType> extends LocalPlayer<RoleType, MoveType>  {

	public LegalPlayer(String name) {
		super(name);
	}

	public MoveType getNextMove() {
		return currentState.getLegalMove(role);
	}
}
