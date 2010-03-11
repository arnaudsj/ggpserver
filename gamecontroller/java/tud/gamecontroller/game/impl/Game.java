/*
    Copyright (C) 2008-2010 Stephan Schiffel <stephan.schiffel@gmx.de>, Nicolas JEAN <njean42@gmail.com>

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

package tud.gamecontroller.game.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.ReasonerFactory;
import tud.gamecontroller.XMLGameStateWriter;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.TermInterface;

public class Game<
	TermType extends TermInterface,
	ReasonerStateInfoType
	> implements GameInterface<
		TermType,
		State<TermType, ReasonerStateInfoType>> {

	private static final Logger logger = Logger.getLogger(Game.class.getName());
	
	private final ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory;

	private String name;
//	private List<RoleInterface<TermType>> orderedRoles=null;
	private String stylesheet = null;  // this can remain null (no stylesheet will be used)
	private String seesXMLRules = null;
	private String gameDescription = null;
	private final String kifGameDescription;
	private final List<? extends RoleInterface<TermType>> roles;

	public Game(File gameFile, ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) throws IOException {
		this(gameFile, reasonerFactory, null, null);
	}

	public Game(File gameFile, ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory, String stylesheet) throws IOException {
		this(gameFile, reasonerFactory, stylesheet, null);
	}
	
	public Game(File gameFile, ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory, String stylesheet, File sightFile) throws IOException {
		
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(gameFile));
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				sb.append(line + "\n"); // artificial EOLN marker
			}
			
			String fileName = gameFile.getName();
			
			int firstDot = fileName.indexOf(".");
			if (firstDot == -1) {
				// no "." in filename
				this.name = fileName;
			} else {
				this.name = fileName.substring(0, firstDot);
			}
			
			this.reasonerFactory=reasonerFactory;
			this.gameDescription=sb.toString();
			ReasonerInterface<TermType, ReasonerStateInfoType> reasoner = reasonerFactory
					.createReasoner(gameDescription, name);
			roles = reasoner.getRoles();
			kifGameDescription = reasoner.getKIFGameDescription();
			
		} finally {
			if (br != null) {
				br.close();
			}
		}
		
		
		// add sees_xml(..) rules
		
		if (sightFile != null) {
			
			sb = new StringBuffer();
			br = null;
			
			try {
				br = new BufferedReader(new FileReader(sightFile));
				String line;
	
				while ((line = br.readLine()) != null) {
					line = line.trim();
					sb.append(line + "\n"); // artificial EOLN marker
				}
				
				String fileName = gameFile.getName();
				
				int firstDot = fileName.indexOf(".");
				if (firstDot == -1) {
					// no "." in filename
					this.name = fileName;
				} else {
					this.name = fileName.substring(0, firstDot);
				}
				
				this.seesXMLRules = sb.toString();
				
			}
			catch (IOException ioe) {
				
			}
			finally {
				if (br != null) {
					br.close();
				}
			}
			
		}
		
		this.setDefaultXMLSeesRules();
		
	}

	public Game(String gameDescription, String name, ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) {
		this(gameDescription, name, reasonerFactory, null, null);
	}
	
	public Game(String gameDescription, String name, ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory, String stylesheet) {
		this(gameDescription, name, reasonerFactory, stylesheet, null);
	}
	
	public Game(String gameDescription, String name, ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory, String stylesheet, String seesXMLRules) {
		this.name=name;
		this.reasonerFactory=reasonerFactory;
		this.gameDescription=gameDescription;
		ReasonerInterface<TermType, ReasonerStateInfoType> reasoner = reasonerFactory
				.createReasoner(gameDescription, name);
		roles = reasoner.getRoles();
		kifGameDescription = reasoner.getKIFGameDescription();
		
		this.stylesheet = stylesheet;
		this.seesXMLRules = seesXMLRules;
		
		this.setDefaultXMLSeesRules();
		
	}
	
	
	private void setDefaultXMLSeesRules() {
		
		if (this.seesXMLRules == null || this.seesXMLRules == "") {
			/* MODIFIED (ADDED)
			 * We don't have a sightFile, or the file was not found
			 * → let's add the default sees_xml(..) rule for each player
			 */
			this.seesXMLRules = "";
			for (RoleInterface<TermType> role: this.getOrderedRoles())
				this.seesXMLRules += "(<= (sees_xml "+role+" ?t) (true ?t) )\n";
		}
		
	}
	
	
	public State<TermType, ReasonerStateInfoType> getInitialState() {
		ReasonerInterface<TermType, ReasonerStateInfoType> reasoner =
			reasonerFactory.createReasoner(gameDescription+"\n\n"+seesXMLRules, name);
		return new State<TermType,ReasonerStateInfoType>(reasoner , reasoner.getInitialState());
	}

	public RoleInterface<TermType> getRole(int roleindex) {
		return roles.get(roleindex);
	}

	public List<? extends RoleInterface<TermType>> getOrderedRoles(){
//		if(orderedRoles==null){
//			orderedRoles=new LinkedList<RoleInterface<TermType>>();
//			for(int i=0;i<getNumberOfRoles();i++){
//				orderedRoles.add(getRole(i));
//			}
//		}
//		return orderedRoles;
		return new LinkedList<RoleInterface<TermType>>(roles);
	}

	public int getNumberOfRoles() {
		return roles.size();
	}

	public String getName() {
		return name;
	}

	public String getKIFGameDescription() {
		return kifGameDescription;
	}

	/**
	 *
	 * @return the rules of the game including comments and whitespace (and sees_xml() rules)
	 */
	public String getGameDescription() {
		return gameDescription ;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Two games are considered equal iff their names match (i.e., name is a unique identifier).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Game<?, ?> other = (Game<?, ?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getStylesheet() {
		return stylesheet ;
	}
	
	public String getSeesXMLRules() {
		return seesXMLRules ;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Game:");
		buffer.append(" name: ");
		buffer.append(name);
		buffer.append(" reasonerFactory: ");
		buffer.append(reasonerFactory);
		buffer.append(" stylesheet: ");
		buffer.append(stylesheet);
//		buffer.append(" gameDescription: ");
//		buffer.append(gameDescription);
//		buffer.append(" kifGameDescription: ");
//		buffer.append(kifGameDescription);
		buffer.append(" roles: ");
		buffer.append(roles);
		buffer.append("]");
		return buffer.toString();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public String getXMLViewFor(
			Match<?, ?> match,
			String stringState,
			List<List<String>> stringMoves,
			RoleInterface<TermType> role,
			GDLVersion gdlVersion) {
		
		// let's turn the stringState into a known State
		ReasonerInterface<TermType, ReasonerStateInfoType> reasoner =
			this.reasonerFactory.createReasoner(this.gameDescription+"\n\n"+seesXMLRules, "temp_game");
		
		ReasonerStateInfoType reasonerState = reasoner.getStateFromString(stringState);
		State state = new State(reasoner, reasonerState);
		
		// compute goal values
		Map<RoleInterface<TermType>,Integer> goalValues = new HashMap<RoleInterface<TermType>,Integer>();
		
		if (state.isTerminal()) {
			Collection<? extends RoleInterface<TermType>> roles = this.getOrderedRoles();
			for (RoleInterface<TermType> oneRole: roles) {
				logger.info(oneRole+" has goal Value "+reasoner.getGoalValue(reasonerState, oneRole));
				goalValues.put(role, reasoner.getGoalValue(reasonerState, oneRole));
			}
		} else {
			goalValues = null;
		}
		
		XMLGameStateWriter.createXMLOutputStream(
				(MatchInterface<? extends TermInterface,?>) match,
				state,
				stringMoves, // moves...
				goalValues,
				this.stylesheet,
				role,
				gdlVersion);
		
		return null;
		
	}
	
}
