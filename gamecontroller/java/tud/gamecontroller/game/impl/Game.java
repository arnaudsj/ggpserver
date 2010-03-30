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

package tud.gamecontroller.game.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.ReasonerFactoryInterface;
import tud.gamecontroller.auxiliary.InvalidKIFException;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.TermFactoryInterface;
import tud.gamecontroller.term.TermInterface;

public class Game<
	TermType extends TermInterface,
	ReasonerStateInfoType
	> implements GameInterface<
		TermType,
		State<TermType, ReasonerStateInfoType>> {

	private final ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactoryInterface;

	private String name;
//	private List<RoleInterface<TermType>> orderedRoles=null;
	private String stylesheet = null;  // this can remain null (no stylesheet will be used)
	private String seesXMLRules = null;
	private String seesRules = null;
	private String gameDescription = null;
	private final String kifGameDescription;
	private final List<? extends RoleInterface<TermType>> roles;
	private final GDLVersion gdlVersion;
	
	public static final String DEFAULT_SEES_XML_RULES = "(<= (sees_xml random ?t) (true ?t))\n(<= (sees_xml ?p ?t) (role ?p) (distinct ?p random) (true ?t))\n";
	public static final String DEFAULT_SEES_RULES = "(<= (sees ?p (did ?p2 ?m)) (role ?p) (does ?p2 ?m) )\n";

	public Game(File gameFile, ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactory, GDLVersion gdlVersion) throws IOException {
		this(gameFile, reasonerFactory, gdlVersion, null, null);
	}

	public Game(File gameFile, ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactory, GDLVersion gdlVersion, String stylesheet) throws IOException {
		this(gameFile, reasonerFactory, gdlVersion, stylesheet, null);
	}
	
	public Game(File gameFile, ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactory, GDLVersion gdlVersion, String stylesheet, File sightFile) throws IOException {
		
		this.gdlVersion = gdlVersion;
		
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(gameFile));
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				sb.append(line + "\n"); // artificial EOLN marker
			}
			
			// extract the game name from the file name
			String fileName = gameFile.getName();
			int firstDot = fileName.indexOf(".");
			if (firstDot == -1) { // no "." in filename
				this.name = fileName;
			} else {
				this.name = fileName.substring(0, firstDot);
			}
			
			this.reasonerFactoryInterface=reasonerFactory;
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
		String seesXMLRulesFromFile = null;
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
				seesXMLRulesFromFile = sb.toString();
			} finally {
				if (br != null) {
					br.close();
				}
			}
		}
		setSeesXMLRules(seesXMLRulesFromFile);
		setDefaultSeesRules();
	}

	public Game(String gameDescription, String name, ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactory, GDLVersion gdlVersion) {
		this(gameDescription, name, reasonerFactory, gdlVersion, null, null);
	}
	
	public Game(String gameDescription, String name, ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactory, GDLVersion gdlVersion, String stylesheet) {
		this(gameDescription, name, reasonerFactory, gdlVersion, stylesheet, null);
	}
	
	public Game(String gameDescription, String name, ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactory, GDLVersion gdlVersion, String stylesheet, String seesXMLRules) {
		this.gdlVersion = gdlVersion;
		this.name=name;
		this.reasonerFactoryInterface=reasonerFactory;
		this.gameDescription=gameDescription;
		ReasonerInterface<TermType, ReasonerStateInfoType> reasoner = reasonerFactory
				.createReasoner(gameDescription, name);
		roles = reasoner.getRoles();
		kifGameDescription = reasoner.getKIFGameDescription();
		this.stylesheet = stylesheet;
		setSeesXMLRules(seesXMLRules);
		setDefaultSeesRules();
	}

	private void setDefaultSeesRules() {
		if (gdlVersion == GDLVersion.v1) {
			// with this we transform GDL-I to GDL-II on the fly
			this.seesRules = DEFAULT_SEES_RULES;
			// TODO: check that there is no "sees" relation in the GDL yet
		} else {
			this.seesRules = "";
		}
	}
	
	private void setSeesXMLRules(String seesXMLRules) {
		if (seesXMLRules == null) {
			/* We don't have a sightFile, or the file was not found
			 * -> let's add the default sees_xml(..) rule for each player
			 */
			this.seesXMLRules = DEFAULT_SEES_XML_RULES;
		}else{
			this.seesXMLRules = seesXMLRules;
		}
	}
	
	public String getCompleteRules() {
		return gameDescription+"\n"+seesRules+"\n"+seesXMLRules;
	}
	
	
	public State<TermType, ReasonerStateInfoType> getInitialState() {
		ReasonerInterface<TermType, ReasonerStateInfoType> reasoner =
			reasonerFactoryInterface.createReasoner(getCompleteRules(), name);
		return new State<TermType,ReasonerStateInfoType>(reasoner , reasoner.getInitialState());
	}

	public RoleInterface<TermType> getRole(int roleindex) {
		return roles.get(roleindex);
	}

	public List<? extends RoleInterface<TermType>> getOrderedRoles(){
		return Collections.unmodifiableList(roles);
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
		return stylesheet;
	}
	
	public String getSeesXMLRules() {
		return seesXMLRules;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Game:");
		buffer.append(" name: ");
		buffer.append(name);
		buffer.append(" reasonerFactory: ");
		buffer.append(reasonerFactoryInterface);
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
	
	public State<TermType, ReasonerStateInfoType> getStateFromString(String stringState) throws InvalidKIFException {
		// let's turn the stringState into a known State
		ReasonerInterface<TermType, ReasonerStateInfoType> reasoner =
			reasonerFactoryInterface.createReasoner(getCompleteRules(), name);
		
		ReasonerStateInfoType reasonerState = reasoner.getStateFromString(stringState);
		State<TermType, ReasonerStateInfoType> state = new State<TermType, ReasonerStateInfoType>(reasoner, reasonerState);
		
		return state;
	}

	public GDLVersion getGdlVersion() {
		return gdlVersion;
	}
	
	public String getSeesRules() {
		return seesRules;
	}

	public RoleInterface<TermType> getNatureRole() {
		TermFactoryInterface<TermType> termFactory = reasonerFactoryInterface.getTermFactory();
		try {
			return new Role<TermType>(termFactory.getTermFromKIF(RoleInterface.NATURE_ROLE_NAME));
		} catch (InvalidKIFException e) {
			throw new RuntimeException(e);
		}
	}

	public RoleInterface<TermType> getRoleByName(String roleName) {
		TermFactoryInterface<TermType> termFactory = reasonerFactoryInterface.getTermFactory();
		Role<TermType> role = null;
		try {
			role = new Role<TermType>(termFactory.getTermFromKIF(roleName));
		} catch (InvalidKIFException e) {
			role = null;
		}
		if (role!=null && !getOrderedRoles().contains(role)) {
			role = null;
		}
		return role;
	}

	public TermType getTermFromString(String kifTermString) throws InvalidKIFException {
		TermFactoryInterface<TermType> termFactory = reasonerFactoryInterface.getTermFactory();
		return termFactory.getTermFromKIF(kifTermString);
	}
	
}
