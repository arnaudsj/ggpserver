package tud.gamecontroller.game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class JointMove<
	RoleType,
	MoveType extends MoveInterface
	> extends HashMap<RoleType, MoveType> implements JointMoveInterface<RoleType, MoveType> {
	
	private List<? extends RoleType> orderedRoles;
	
	public JointMove(List<? extends RoleType> orderedRoles){
		super();
		this.orderedRoles=orderedRoles;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7570393446222872482L;
	
	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.JointMoveInterface#getOrderedMoves()
	 */
	public List<MoveType> getOrderedMoves(){
		List<MoveType> moves=new LinkedList<MoveType>();
		for(RoleType role:orderedRoles){
			moves.add(get(role));
		}
		return moves;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.JointMoveInterface#getKIFForm()
	 */
	public String getKIFForm() {
		StringBuilder sb=new StringBuilder();
		sb.append('(');
		boolean first=true;
		for(MoveType m:getOrderedMoves()){
			if(first){
				first=false;
			}else{
				sb.append(' ');
			}
			sb.append(m.getKIFForm());
		}
		sb.append(')');
		return sb.toString();
	}
}
