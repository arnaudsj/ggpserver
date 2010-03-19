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

package tud.gamecontroller.game.jocular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.GdlList;
import stanfordlogic.gdl.Parser;
import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.knowledge.BasicKB;
import stanfordlogic.knowledge.GameInformation;
import stanfordlogic.knowledge.KnowledgeBase;
import stanfordlogic.knowledge.MetaGdl;
import stanfordlogic.knowledge.RelationNameProcessor;
import stanfordlogic.prover.AbstractReasoner;
import stanfordlogic.prover.BasicReasoner;
import stanfordlogic.prover.Fact;
import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.ProofContext;
import stanfordlogic.prover.TermVariable;
import stanfordlogic.prover.VariableFact;
import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Fluent;
import tud.gamecontroller.game.impl.Move;
import tud.gamecontroller.game.impl.Role;

public class Reasoner implements ReasonerInterface<Term, ProofContext> {
	private Parser parser;
	private AbstractReasoner stanfordlogicReasoner;

	private Fact queryTerminal;
	private Fact queryNext;
	private Fact queryTrue;
	private Fact queryInit;
	private RelationNameProcessor trueProcessor;
	private GdlList rules;

	public Reasoner(String gameDescription, Parser parser){
		this.parser=parser;
		rules=parser.parse(gameDescription);
        GameInformation gameInfo = new MetaGdl(parser).examineGdl(rules);
        KnowledgeBase staticKb = new BasicKB();
        staticKb.loadWithFacts(gameInfo.getAllGrounds());
        this.stanfordlogicReasoner = new BasicReasoner(staticKb, gameInfo.getIndexedRules(), parser);
		queryTerminal=makeQuery("terminal");
		queryNext=makeQuery("next", "?f");
		queryTrue=makeQuery("true", "?f");
		queryInit=makeQuery("init", "?f");
		trueProcessor=new RelationNameProcessor(parser.TOK_TRUE);
	}
	
    protected Fact makeQuery(String ... args)
    {
        GdlList list = GdlList.buildFromWords(parser.getSymbolTable(), args);
        return VariableFact.fromList(list);
    }

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.jocular.ReasonerInterface#GetRoles()
	 */
	public synchronized List<? extends RoleInterface<Term>> getRoles() {
		List<RoleInterface<Term>> roles=new ArrayList<RoleInterface<Term>>();
		Iterable<GroundFact> roleFacts = stanfordlogicReasoner.getAllAnswers(makeQuery("role", "?r"));
		for(GroundFact roleFact:roleFacts){
			roles.add(new Role<Term>(new Term(parser.getSymbolTable(), roleFact.getTerm(0))));
		}
		return roles;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.jocular.ReasonerInterface#getInitialState()
	 */
	public synchronized ProofContext getInitialState() {
        KnowledgeBase kb = new BasicKB();
        Iterable<GroundFact> inits = stanfordlogicReasoner.getAllAnswers(queryInit);
        for (GroundFact init : inits) {
        	kb.setTrue(trueProcessor.processFact(init));
        }
        return new ProofContext(kb, parser);
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.jocular.ReasonerInterface#isTerminal(tud.gamecontroller.game.jocular.State)
	 */
	public synchronized boolean isTerminal(ProofContext state) {
		return stanfordlogicReasoner.getAnAnswer(queryTerminal, state)!=null;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.jocular.ReasonerInterface#getSuccessorState(tud.gamecontroller.game.jocular.State, tud.gamecontroller.game.JointMove)
	 */
	public synchronized ProofContext getSuccessorState(ProofContext state, JointMoveInterface<Term> jointMove) {
		for(Entry<? extends RoleInterface<Term>, ? extends MoveInterface<Term>> entry:jointMove.entrySet()){
			GroundFact moveFact=new GroundFact(parser.TOK_DOES, entry.getKey().getTerm().getExpr(), entry.getValue().getTerm().getExpr());
        	state.getVolatileKb().setTrue(moveFact);
        }
		Iterable<GroundFact> nextFacts=stanfordlogicReasoner.getAllAnswersIterable(queryNext, state);
        KnowledgeBase kb = new BasicKB();
        for (GroundFact fact : nextFacts) {
        	kb.setTrue(trueProcessor.processFact(fact));
        }
		return new ProofContext(kb, parser);
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.jocular.ReasonerInterface#isLegal(tud.gamecontroller.game.jocular.State, tud.gamecontroller.game.Role, tud.gamecontroller.game.Move)
	 */
	public synchronized boolean isLegal(ProofContext state, RoleInterface<Term> role, MoveInterface<Term> move) {
		return stanfordlogicReasoner.getAnAnswer(new VariableFact(parser.TOK_LEGAL, role.getTerm().getExpr(), move.getTerm().getExpr()), state)!=null;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.jocular.ReasonerInterface#GetGoalValue(tud.gamecontroller.game.jocular.State, tud.gamecontroller.game.Role)
	 */
	public synchronized int getGoalValue(ProofContext state, RoleInterface<Term> role) {
		GroundFact f=stanfordlogicReasoner.getAnAnswer(new VariableFact(parser.TOK_GOAL, role.getTerm().getExpr(), TermVariable.makeTermVariable()), state);
		return Integer.parseInt(f.getTerm(1).toString(parser.getSymbolTable()));
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.jocular.ReasonerInterface#GetLegalMoves(tud.gamecontroller.game.jocular.State, tud.gamecontroller.game.Role)
	 */
	public synchronized Collection<? extends MoveInterface<Term>> getLegalMoves(ProofContext state, RoleInterface<Term> role) {
		Iterable<GroundFact> legalFacts=stanfordlogicReasoner.getAllAnswersIterable(new VariableFact(parser.TOK_LEGAL, role.getTerm().getExpr(), TermVariable.makeTermVariable()), state);
		Collection<MoveInterface<Term>> moveslist=new LinkedList<MoveInterface<Term>>();
        for (GroundFact fact : legalFacts) {
        	moveslist.add(new Move<Term>(new Term(parser.getSymbolTable(), fact.getTerm(1))));
        }
		return moveslist;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.jocular.ReasonerInterface#getFluents(tud.gamecontroller.game.jocular.State)
	 */
	public synchronized Collection<? extends FluentInterface<Term>> getFluents(ProofContext state) {
		
		Collection<FluentInterface<Term>> fluents=new LinkedList<FluentInterface<Term>>();
        Iterable<GroundFact> trues = stanfordlogicReasoner.getAllAnswers(queryTrue,state);
        for (GroundFact init : trues) {
        	fluents.add(new Fluent<Term>(new Term(parser.getSymbolTable(), init.getTerm(0))));
        }
		return fluents;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.jocular.ReasonerInterface#getKIFGameDescription()
	 */
	public String getKIFGameDescription() {
		StringBuilder sb = new StringBuilder();
		boolean first=true;
		for (GdlExpression exp:rules){
			if(!first){
				sb.append(" ");
			}else{
				first=false;
			}
			sb.append(exp.toString());
		}
		return sb.toString();
	}

	public SymbolTable getSymbolTable() {
		return parser.getSymbolTable();
	}

	public synchronized Collection<Term> getSeesTerms(
			ProofContext state, RoleInterface<Term> role,
			JointMoveInterface<Term> jointMove) {
		throw new UnsupportedOperationException();
	}

	public synchronized Collection<Term> getSeesXMLTerms(
			ProofContext state, RoleInterface<Term> role) {
		throw new UnsupportedOperationException();
	}

	public ProofContext getStateFromString(String state) {
		throw new UnsupportedOperationException();
	}

}
