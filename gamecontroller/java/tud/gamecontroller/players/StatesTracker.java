package tud.gamecontroller.players;

import java.util.Collection;
import java.util.Vector;

import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.impl.JointMove;
import tud.gamecontroller.term.TermInterface;


public class StatesTracker<TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>> {
	
	protected GameInterface<TermType, StateType> game;
	protected Vector<StateType> currentPossibleStates;
	protected RoleInterface<TermType> role;


	public StatesTracker (GameInterface<TermType, StateType> game, StateType initialState, RoleInterface<TermType> role) {
		
		this.game = game;
		
		this.currentPossibleStates = new Vector<StateType>();
		this.currentPossibleStates.add(initialState);
		
		this.role = role;
		
	}
	
	public Vector<StateType> statesUpdate (Collection<FluentInterface<TermType>> seesFluents) throws InterruptedException {
		
		Vector<StateType> currentPossibleStates2 = new Vector<StateType>();
		
		for (StateType state: this.currentPossibleStates) {
			
			Vector<JointMoveInterface<TermType>> legalJointMoves = this.computeJointMoves(state);
			
			for (JointMoveInterface<TermType> jointMove: legalJointMoves) {
				
				StateType newState = state.getSuccessor(jointMove);
				
				int i = currentPossibleStates2.indexOf(newState);
				if (i == -1) {
					if (this.isPossible(newState, jointMove, seesFluents)) {
						currentPossibleStates2.add(newState);
					}
				} else {
					//System.out.println("We already have state "+newState+" in currentPossibleStates2 at position "+i+" ("+currentPossibleStates2.get(i)+")");
				}
				
			}
			
		}
		
		this.currentPossibleStates = currentPossibleStates2;
		System.gc();
		
		System.out.println("oneStatesTracker.statesUpdate(...) for "+this.role+", "+
				this.currentPossibleStates.size()+" currentPossibleStates = "+this.currentPossibleStates);
		
		return this.currentPossibleStates; // TODO: make a clone?
		
	}
	
	
	public Vector<JointMoveInterface<TermType>> computeJointMoves ( StateType state ) {
		
		int nbJointMoves = 1;
		for(RoleInterface<TermType> role: this.game.getOrderedRoles()) {
			nbJointMoves *= state.getLegalMoves(role).size();
		}
		
		//System.out.println("\nThere are "+nbJointMoves+" possible JointMoves.");
		
		Vector<JointMoveInterface<TermType>> legalJointMoves = new Vector<JointMoveInterface<TermType>>(nbJointMoves);
		for (int i = 0; i < nbJointMoves; i++)
			legalJointMoves.add(new JointMove<TermType>( this.game.getOrderedRoles() ));
		
		for(RoleInterface<TermType> role: this.game.getOrderedRoles()) {
			
			//System.out.println("\nFilling JointMoves with move of "+role);
			
			Vector<MoveInterface<TermType>> legalMoves = new Vector<MoveInterface<TermType>>(state.getLegalMoves(role));
			int nbLm = legalMoves.size();
			for (int moveIndex=0; moveIndex < nbLm; moveIndex++) {
				
				MoveInterface<TermType> move = legalMoves.get(moveIndex);
				
				for (int i = moveIndex * nbJointMoves/nbLm; i < (moveIndex+1) * nbJointMoves/nbLm; i++) {
					//System.out.print(i+"·");
					JointMoveInterface<TermType> currentJointMove = legalJointMoves.get(i);
					currentJointMove.put(role, move);
				}
				
			}
			
		}
		
		//System.out.println("oneStatesTracker.legalJointMoves(...) = "+legalJointMoves);
		
		return legalJointMoves;
		
	}
	
	
	@SuppressWarnings("unchecked")
	private boolean isPossible (StateType state, JointMoveInterface<TermType> jointMove, Collection<FluentInterface<TermType>> seesFluents) {
		
		Collection<FluentInterface<TermType>> shouldSee =
			(Collection<FluentInterface<TermType>>) state.getSeesFluents(role, jointMove); 
		
		boolean b = shouldSee.equals(seesFluents);
		//System.out.println(shouldSee+".equals( "+seesFluents+" ) = "+b);
		
		return b;
		
	}
	
	
	/** 
	 * @return moves that are legal in all of the current possible states 
	 */
	@SuppressWarnings("unchecked")
	public Collection<MoveInterface<TermType>> computeLegalMoves () {
		
		Collection<MoveInterface<TermType>> legalMoves = null;
		
		for (StateType state: this.currentPossibleStates) {
			
			Collection<MoveInterface<TermType>> stateLegalMoves = (Collection<MoveInterface<TermType>>) state.getLegalMoves(this.role);
			//System.out.println( "stateLegalMoves = "+stateLegalMoves );
			
			if (legalMoves == null) {
				legalMoves = stateLegalMoves;
			} else {
				legalMoves.retainAll(stateLegalMoves);
			}
			
			//System.out.println( "Until now, our legalMoves are = "+legalMoves );
			
		}
		
		System.out.println( "oneStatesTracker.legalMoves() for "+this.role+"("+legalMoves.size()+") = "+legalMoves );
		
		return legalMoves;
		
	}
	
	
}