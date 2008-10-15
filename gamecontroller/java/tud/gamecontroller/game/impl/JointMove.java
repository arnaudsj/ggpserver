package tud.gamecontroller.game.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.GameObjectInterface;
import tud.gamecontroller.term.TermInterface;

public class JointMove<TermType extends TermInterface>
	extends HashMap<RoleInterface<TermType>, MoveInterface<TermType>>
	implements JointMoveInterface<TermType> {
	
	private List<? extends RoleInterface<TermType>> orderedRoles;
	
	public JointMove(List<? extends RoleInterface<TermType>> orderedRoles){
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
	public List<MoveInterface<TermType>> getOrderedMoves(){
		List<MoveInterface<TermType>> moves=new LinkedList<MoveInterface<TermType>>();
		for(RoleInterface<TermType> role:orderedRoles){
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
		for(GameObjectInterface m:getOrderedMoves()){
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
