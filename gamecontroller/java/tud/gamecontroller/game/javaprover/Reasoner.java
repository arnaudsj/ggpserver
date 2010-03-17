/*
    Copyright (C) 2008-2010 Stephan Schiffel <stephan.schiffel@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller.game.javaprover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;

import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Fluent;
import tud.gamecontroller.game.impl.Move;
import tud.gamecontroller.game.impl.Role;
import cs227b.teamIago.gameProver.JavaProversGameSimulatorEnhancer;
import cs227b.teamIago.parser.Parser;
import cs227b.teamIago.parser.PublicAxiomsWrapper;
import cs227b.teamIago.parser.Statement;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.resolver.Theory;
import cs227b.teamIago.util.GameState;

public class Reasoner implements ReasonerInterface<Term, GameState> {
	
	// MODIFIED: GDL-II-conscious GameSimulator
	private JavaProversGameSimulatorEnhancer gameSim;
	private String gameDescription;
	
	public Reasoner(String gameDescription) {
		//System.out.println("JavaProver.Reasoner("+gameDescription+")");
		this.gameDescription=gameDescription;
		gameSim=new JavaProversGameSimulatorEnhancer(false, true); // MODIFIED: GDL-II-conscious GameSimulator
		gameSim.ParseDescIntoTheory(gameDescription);
	}

	public boolean isTerminal(GameState state) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			return gameSim.IsTerminal();
		}
	}

	public List<? extends RoleInterface<Term>> getRoles() {
		ExpList expList;
		synchronized (gameSim) {
			expList=gameSim.GetRoles();
		}
		List<Role<Term>> roles=new ArrayList<Role<Term>>();
		for(int i=0;i<expList.size();i++){
			roles.add(new Role<Term>(new Term(expList.get(i))));
		}
		return roles;
	}

	public GameState getSuccessorState(GameState state, JointMoveInterface<Term> jointMove) {
		ExpList movesList=new ExpList();
		for(Entry<? extends RoleInterface<Term>, ? extends MoveInterface<Term>> entry:jointMove.entrySet()){
			ExpList doesArgs=new ExpList();
			doesArgs.add(entry.getKey().getTerm().getExpr());
			doesArgs.add(entry.getValue().getTerm().getExpr());
			movesList.add(new Predicate(new Atom("DOES"), doesArgs));
		}
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			gameSim.SimulateStep(movesList);
			return gameSim.GetGameState();
		}
	}

	public boolean isLegal(GameState state, RoleInterface<Term> role, MoveInterface<Term> move) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			ExpList expList=new ExpList();
			expList.add(role.getTerm().getExpr());
			expList.add(move.getTerm().getExpr());
			try {
				return gameSim.getTheory().findp(new Predicate(new Atom("LEGAL"),expList));
			} catch (InterruptedException e) {
				Logger logger=Logger.getLogger("tud.gamecontroller");
				logger.severe("reasoner was interrupted during findp("+new Predicate(new Atom("LEGAL"),expList)+"):");
				logger.severe(e.getMessage());
				return false;
			}
		}
	}

	public int getGoalValue(GameState state, RoleInterface<Term> role) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			return gameSim.GetGoalValue(role.getTerm().getExpr());
		}
	}

	public Collection<? extends MoveInterface<Term>> getLegalMoves(GameState state, RoleInterface<Term> role) {
		ExpList exprlist;
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			exprlist=gameSim.GetLegalMoves(role.getTerm().getExpr());
		}
		Collection<MoveInterface<Term>> moveslist=new LinkedList<MoveInterface<Term>>();
		if (exprlist == null) {
			System.out.println(role+" has no legal move!");
		}
		for(int i=0;i<exprlist.size();i++){
			moveslist.add(new Move<Term>(new Term(((Connective)exprlist.get(i)).getOperands().get(1))));
		}
		return moveslist;
	}

	public GameState getInitialState() {
		synchronized (gameSim) {
			gameSim.SimulateStart();
			return gameSim.getTheory().getState();
		}
	}

	public String getKIFGameDescription() {
		PublicAxiomsWrapper a=new PublicAxiomsWrapper();
		a.parseFromString(gameDescription);
		List<Statement> statements=a.getStatements();
		StringBuilder stringBuilder=new StringBuilder();
		for(Statement statement:statements){
			stringBuilder.append(statement.toString()).append(' ');
		}
		return stringBuilder.toString().toUpperCase();
	}

	@SuppressWarnings("unchecked")
	public Collection<? extends FluentInterface<Term>> getFluents(GameState state) {
		Collection<FluentInterface<Term>> fluents=new LinkedList<FluentInterface<Term>>();
		Iterator<ExpList> it=state.getMap().values().iterator();
		while(it.hasNext()){
			ExpList el=it.next();
			for(int i=0;i<el.size();i++){
				Predicate true_expr=(Predicate)el.get(i);
				 fluents.add(new Fluent<Term>(new Term(true_expr.getOperands().get(0))));
			}
		}
		return fluents;
	}
	
	
	// MODIFIED (ADDED)
	public Collection<? extends FluentInterface<Term>> getSeesFluents( GameState state, RoleInterface<Term> role, JointMoveInterface<Term> jointMove) {
		
		ExpList movesList=new ExpList();
		if (jointMove != null) {
			for(Entry<? extends RoleInterface<Term>, ? extends MoveInterface<Term>> entry:jointMove.entrySet()){
				ExpList doesArgs=new ExpList();
				doesArgs.add(entry.getKey().getTerm().getExpr());
				doesArgs.add(entry.getValue().getTerm().getExpr());
				movesList.add(new Predicate(new Atom("DOES"), doesArgs));
			}
		}
		ExpList el = new ExpList();
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			el = gameSim.getSeesFluents(role.getTerm().getExpr(), movesList);
		}
		
		Collection<FluentInterface<Term>> fluents = new Vector<FluentInterface<Term>>();
		if (el == null) return fluents;
		
		for(int i=0;i<el.size();i++) {
			Predicate true_expr=(Predicate)el.get(i);
			fluents.add(new Fluent<Term>(new Term(true_expr.getOperands().get(0))));
		}
		return fluents;
		
	}
	
	
	// MODIFIED (ADDED)
	public Collection<? extends FluentInterface<Term>> getSeesXMLFluents( GameState state, RoleInterface<Term> role) {
		
		/*ExpList movesList=new ExpList();
		if (jointMove != null) {
			for(Entry<? extends RoleInterface<Term>, ? extends MoveInterface<Term>> entry:jointMove.entrySet()){
				ExpList doesArgs=new ExpList();
				doesArgs.add(entry.getKey().getTerm().getExpr());
				doesArgs.add(entry.getValue().getTerm().getExpr());
				movesList.add(new Predicate(new Atom("DOES"), doesArgs));
			}
		}*/
		ExpList el = new ExpList();
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			Expression r = role.getTerm().getExpr();
			el = gameSim.getSeesXMLFluents(r);
		}
		
		Collection<FluentInterface<Term>> fluents = new Vector<FluentInterface<Term>>();
		if (el == null) return fluents;
		
		for(int i=0;i<el.size();i++) {
			Predicate true_expr=(Predicate)el.get(i);
			fluents.add(new Fluent<Term>(new Term(true_expr.getOperands().get(0))));
		}
		return fluents;
		
	}
	
	
	public void addRulesFromFile (String filename) {
		this.gameSim.ParseFileIntoTheory(filename);
	}

	public GameState getStateFromString(String state) {
		
		ExpList el = Parser.parseExpList(state);
		Expression[] exps = new Expression[el.size()];
		
		for (int i = 0; i < el.size(); i++) {
			Expression e = el.get(i);
			exps[i] = new Predicate("true", new ExpList( new Expression[] {e} ) );
		}
		
		Theory t = new Theory(true, false);
		t.setState(new ExpList(exps));
		GameState gs = t.getState();
		System.out.println("demarshalled state = "+gs);
		
		return gs;
		
	}
	

}
