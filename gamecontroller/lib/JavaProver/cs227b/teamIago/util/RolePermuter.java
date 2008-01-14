/*
 * Created on May 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.util;

import java.util.ArrayList;

import cs227b.teamIago.gameProver.GameSimulator;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;

/**
 * @author Nick
 *
 * Essentially manages the loop counting variable
 * of a set of nested "for" loops of arbitrary nesting depth.
 * Manages the complete move generation for all or all but one players.
 * 
 */

public class RolePermuter extends ForNest{ 
	protected GameSimulator ggp;
	protected ExpList roles;
	protected ExpList [] moves;
	
/*	public RolePermuter(Atom role, GGP ggp)
	{
		super();
		this.ggp = ggp;
		roles = ggp.GetOtherRoles(role);
		if (roles == null) nestDepth = 0;
		else nestDepth = roles.size();
		moves = new ExpList[nestDepth];
		int[] roleSize = new int[nestDepth];
		for (int i = 0; i < nestDepth; i++)
		{
			ExpList mvs = ggp.GetLegalMoves(roles.get(i));
			moves[i] = mvs;
			roleSize[i] = mvs.size();
		}
		setLimits(roleSize);
	}
*/	
	public RolePermuter(ExpList roles, GameSimulator ggp)
	{
		super();
		this.ggp = ggp;
		this.roles = roles;
		nestDepth = roles.size();
		moves = new ExpList[nestDepth];
		int[] roleSize = new int[nestDepth];
		for (int i = 0; i < nestDepth; i++)
		{
			ExpList mvs = ggp.GetLegalMoves(roles.get(i));
			moves[i] = mvs;
			roleSize[i] = mvs.size();
		}
		setLimits(roleSize);		
	}
	
	public RolePermuter(ExpList roles, ArrayList moveSets, GameSimulator ggp)
	{
		super();
		this.ggp = ggp;
		nestDepth = roles.size();
		moves = new ExpList[moveSets.size()];
		int[] roleSize = new int[nestDepth];
		for (int i = 0; i < nestDepth; ++i) {
			moves[i] = (ExpList) moveSets.get(i);
			roleSize[i] = moves[i].size();
		}
		setLimits(roleSize);
	}
	
	public ExpList getFullMove(Expression myMove) {
		ExpList retMove = new ExpList();
		retMove.add(myMove);
		for (int i = 0; i < nestDepth; ++i)
			retMove.add(moves[i].get(count[i]));
		return retMove;
	}

	public ExpList getFullMove() {
		ExpList retMove = new ExpList();
		for (int i = 0; i < nestDepth; ++i)
			retMove.add(moves[i].get(count[i]));
		return retMove;
	}
	
	public ExpList getFullMove(ExpList partialMove) {
		ExpList retMove = new ExpList(partialMove);
		for (int i = 0; i < nestDepth; ++i) {
			retMove.add(moves[i].get(count[i]));
		}
		return retMove;
	}
	
	public ExpList getMove()
	{
		ExpList retMove = new ExpList();
		for (int i = 0; i < nestDepth; i++)
			retMove.add(moves[i].get(count[i]));
		return retMove;	
	}
	public boolean hasRoles() {
		return (roles != null) && (roles.size() > 0);
	}
}
