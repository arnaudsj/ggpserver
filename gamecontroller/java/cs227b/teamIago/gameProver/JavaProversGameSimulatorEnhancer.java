/*
    Copyright (C) 2010 Nicolas JEAN <njean42@gmail.com>

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

package cs227b.teamIago.gameProver;

import cs227b.teamIago.gameProver.GameSimulator;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;


/* MODIFIED (ADDED)
 * This class is here to extend the capabilities of the JavaProver's GameSimulator, without touching its code.
 * This is due to a lack of license on its code, making it difficult to reuse.
 * This class focuses on enabling derivation of so-called "sees terms", and thereby giving the possibility
 * to reason about GDL-II games.
 */
public class JavaProversGameSimulatorEnhancer extends GameSimulator {

	protected static final Atom aSees = new Atom("sees");
	protected static final Atom aSeesXML = new Atom("sees_xml");
	
	public JavaProversGameSimulatorEnhancer(boolean wantDebugPrintouts, boolean useOpt) {
		super(wantDebugPrintouts, useOpt);
	}
	
	/**
	 * this calculates the sees terms to send to "player", given that the previous moves are "moves"
	 * "moves" is useful because does(Player,Action) may appear in the "sees" relation's body
	 * 		(like the next relation)
	 */
	public ExpList getSeesTerms(Expression player, ExpList moves) {
		ExpList el = null;
		theoryObj.add(moves);
		ExpList seesArgs = new ExpList();
		seesArgs.add(player);
		seesArgs.add(vX);
		try {
			el = theoryObj.finds(vX, new Predicate(aSees,seesArgs));
		} catch (InterruptedException e) {
			wasInterrupted = true;
		}
		return el;
	}
	
	/**
	 * this calculates the sees terms to put in the XML file for the visualization
	 */
	public ExpList getSeesXMLTerms(Expression player) {
		ExpList el = null;
		ExpList seesArgs = new ExpList();
		seesArgs.add(player);
		seesArgs.add(vX);
		try {
			el = theoryObj.finds(vX, new Predicate(aSeesXML,seesArgs));
		} catch (InterruptedException e) {
			wasInterrupted = true;
		}
		return el;
	}
	
}
