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

package tud.gamecontroller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.GameObjectInterface;
import tud.gamecontroller.term.TermInterface;

public class XMLGameStateWriter
		implements GameControllerListener {
	
	//private static final Logger logger = Logger.getLogger(Game.class.getName());
	
	private String outputDir, matchDir;
	private List<JointMoveInterface<? extends TermInterface>> moves;
	private int step;
	protected RunnableMatchInterface<? extends TermInterface, ?> match;
	private String stylesheet;
	
	protected RoleInterface<? extends TermInterface> role;
	
		
	public XMLGameStateWriter(String outputDir, String stylesheet, RoleInterface<? extends TermInterface> role) {
		this.outputDir=outputDir;
		this.stylesheet=stylesheet;
		this.moves=new LinkedList<JointMoveInterface<? extends TermInterface>>();
		this.match=null;
		this.role = role;
	}
	
	public void gameStarted(RunnableMatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface, ?> currentState) {
		this.match = match;
		this.step=1;
		// MODIFIED
		if (role == null || this.match.getGame().getGdlVersion() == GDLVersion.v1) { // Regular GDL
			matchDir=outputDir+File.separator+match.getMatchID();
		} else { // GDL-II
			matchDir=outputDir+File.separator+match.getMatchID()+"-"+this.role;
		}
		(new File(matchDir)).mkdirs();
		writeState(currentState, null);
	}
	
	public void gameStep(JointMoveInterface<? extends TermInterface> jointMove, StateInterface<? extends TermInterface,?> currentState) {
		step++;
		moves.add(jointMove );
		writeState(currentState, null);
	}

	public void gameStopped(StateInterface<? extends TermInterface,?> currentState, Map<? extends RoleInterface<?>,Integer> goalValues) {
		writeState(currentState, goalValues);
		this.moves=new LinkedList<JointMoveInterface<? extends TermInterface>>();
	}
	
	private void writeState(StateInterface<? extends TermInterface,?> currentState, Map<?, Integer> goalValues) {
		ByteArrayOutputStream os = null; 
		FileOutputStream fileOutputStream = null;
		
		//System.out.println( ((Game)match.getGame()).getGameDescription() );
		
		try {
			os = createXMLOutputStream(match, currentState, XMLGameStateWriter.getStringMoves(moves), goalValues, stylesheet, role, null, false, false, null, null, null);
			fileOutputStream = new FileOutputStream(new File(matchDir+File.separator+"step_"+step+".xml"));
			fileOutputStream.write(os.toByteArray());
			if(goalValues!=null){ // write the final state twice (once as step_X.xml and once as finalstate.xml)
				fileOutputStream.close();
				fileOutputStream = new FileOutputStream(new File(matchDir+File.separator+"finalstate.xml"));
				fileOutputStream.write(os.toByteArray());
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(XMLGameStateWriter.class.getName()).warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (IOException ex) {
			Logger.getLogger(XMLGameStateWriter.class.getName()).warning("Exception occured while generation xml:"+ex.getMessage());
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @param match
	 * @param currentState
	 * @param stringMoves
	 * @param goalValues
	 * @param stylesheet
	 * @param role the role from which perspective the xml view should be generated
	 * @param date if null, the current time is set as timestamp for the state (for the standalone GameController). If not null, used as the timestamp (for calls from the server).
	 * @param legalMoves if null, no additional <legalmoves> tag is added (for the standalone GameController), otherwise, legalMoves are listed in this tag (for calls from the server).
	 * @return
	 * @throws TransformerFactoryConfigurationError
	 * @throws IllegalArgumentException
	 */
	public static ByteArrayOutputStream createXMLOutputStream(
			MatchInterface<? extends TermInterface, ?> match,
			StateInterface<? extends TermInterface, ?> currentState,
			List<List<String>> stringMoves,
			Map<?, Integer> goalValues,
			String stylesheet,
			RoleInterface<? extends TermInterface> role,
			Date date,
			boolean playing,
			boolean quickConfirm,
			List<TermInterface> legalMoves,
			TermInterface chosenMove,
			Boolean confirmed)
			throws TransformerFactoryConfigurationError,
			IllegalArgumentException {
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		try{
			Document xmldoc=createXML(match, currentState, stringMoves, goalValues, stylesheet, role, date, playing, quickConfirm, legalMoves, chosenMove, confirmed);
			// Serialization through Transform.
			DOMSource domSource = new DOMSource(xmldoc);
			
			StreamResult streamResult = new StreamResult(os);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer;
			serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
			serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"http://games.stanford.edu/gamemaster/xml/viewmatch.dtd");
			serializer.setOutputProperty(OutputKeys.INDENT,"yes");
			serializer.transform(domSource, streamResult);
		} catch (TransformerConfigurationException ex) {
			Logger.getLogger(XMLGameStateWriter.class.getName()).warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(XMLGameStateWriter.class.getName()).warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (TransformerException ex) {
			Logger.getLogger(XMLGameStateWriter.class.getName()).warning("Exception occured while generation xml:"+ex.getMessage());
		}
		return os;
	}

	/**
	 * 
	 * @param match the match the currentState is from
	 * @param currentState the state to be transformed to XML
	 * @param stringMoves the joint moves executed so far as strings
	 * @param goalValues a list of goal values for the players or null if this is not a terminal state
	 * @param stylesheet URL of a style sheet or null
	 * @param role the role from which perspective the xml view should be generated
	 * @param the time, when the currentState was generated
	 * @return an xml document
	 * @throws ParserConfigurationException
	 */
	@SuppressWarnings("unchecked")
	private static Document createXML(
			MatchInterface<? extends TermInterface, ?> match,
			StateInterface<? extends TermInterface,?> currentState,
			List<List<String>> stringMoves,
			Map<?, Integer> goalValues,
			String stylesheet,
			RoleInterface<? extends TermInterface> role,
			Date date,
			boolean playing,
			boolean quickConfirm,
			List<TermInterface> legalMoves,
			TermInterface chosenMove,
			Boolean confirmed)
	throws ParserConfigurationException {
		
		 Document xmldoc = null;
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder builder;

		 builder = factory.newDocumentBuilder();
		 DOMImplementation impl = builder.getDOMImplementation();
		 Element e = null;
		 // Document.
		 xmldoc = impl.createDocument(null, null, null);

		 xmldoc.setXmlVersion("1.0");
		 if(stylesheet!=null){
			 Node xsl=xmldoc.createProcessingInstruction("xml-stylesheet","type=\"text/xsl\" href=\""+stylesheet+"\"");
			 xmldoc.appendChild(xsl);
		 }
		 xmldoc.appendChild(impl.createDocumentType("match", null, "http://games.stanford.edu/gamemaster/xml/viewmatch.dtd")); // TODO: modify this DTD..?
		 // Root element.
		 Element root = xmldoc.createElement("match");
		 xmldoc.appendChild(root);
		 e=xmldoc.createElement("match-id");
		 e.setTextContent(match.getMatchID());
		 root.appendChild(e);
		 
		 // indicate for which player the xml file is meant
		 GameInterface<?, ?> game = match.getGame();
		 GDLVersion gdlVersion = game.getGdlVersion();
		 if (role == null) {
			 role = (RoleInterface<? extends TermInterface>) game.getNatureRole();
		 }
		 e=xmldoc.createElement("sight-of");
		 e.setTextContent(role.getKIFForm().toUpperCase());
		 root.appendChild(e);
		 
		 
		 for(GameObjectInterface oneRole: match.getGame().getOrderedRoles()){
			 e=xmldoc.createElement("role");
			 e.setTextContent(oneRole.getPrefixForm());
			 root.appendChild(e);
		 }
		 
		 for(String p:match.getOrderedPlayerNames()){
			 //logger.info("Adding player "+p.toUpperCase());
			 e=xmldoc.createElement("player");
			 e.setTextContent(p.toUpperCase());
			 root.appendChild(e);
		 }
		 e=xmldoc.createElement("timestamp");
		 if (date == null) { // used for XML files generation (from standalone GameController)
			 e.setTextContent(Long.toString(System.currentTimeMillis()));
		 } else { // used for storage in DB (from the ggpserver)
			 e.setTextContent(Long.toString(date.getTime()));
		 }
		 root.appendChild(e);
		 e=xmldoc.createElement("startclock");
		 e.setTextContent(Integer.toString(match.getStartclock()));
		 root.appendChild(e);
		 if(goalValues==null){ // don't write play clock if the game is over
			 e=xmldoc.createElement("playclock");
			 e.setTextContent(Integer.toString(match.getPlayclock()));
			 root.appendChild(e);
		 }
		 // role.getKIFForm().toUpperCase().equals("RANDOM")
		 root.appendChild(createHistoryElement(xmldoc, stringMoves, role, gdlVersion));
		 
		 //logger.info("goalValues = "+goalValues);
		 if(goalValues!=null) root.appendChild(createScoresElement(xmldoc, match.getGame(), goalValues));
		 
		 root.appendChild(createStateElement(xmldoc, currentState, role));
		 
		 if (quickConfirm)
			 root.appendChild(xmldoc.createElement("quickConfirm"));
		 
		 if (match instanceof RunnableMatchInterface) {
			 if (playing) {
				 root.appendChild(createLegalMoves(xmldoc, legalMoves));
				 root.appendChild(createChosenMove(xmldoc, chosenMove, confirmed));
			 }
		 }
		 
		 return xmldoc;
	}
	
	private static List<List<String>> getStringMoves(List<JointMoveInterface<? extends TermInterface>> moves) {
		List<List<String>> stringMoves = new LinkedList<List<String>>();
		for (JointMoveInterface<? extends TermInterface> jointMove: moves) {
			LinkedList<String> jM = new LinkedList<String>();
			for (MoveInterface<? extends TermInterface> move: jointMove.getOrderedMoves()) {
				jM.add(move.getKIFForm());
			}
			stringMoves.add(jM);
		}
		return stringMoves;
	}

	@SuppressWarnings("unchecked")
	private static Node createStateElement(Document xmldoc, StateInterface<? extends TermInterface, ?> currentState, RoleInterface<? extends TermInterface> role) {
		Element state=xmldoc.createElement("state");
		Collection<? extends TermInterface> terms = currentState.getSeesXMLTerms( (RoleInterface) role);
		for(TermInterface t:terms) {
			state.appendChild(createTermElement(xmldoc, "fact", t));
		}
		return state;
	}

//	// version for Stanford (GGP competition 2010) 
//	private static Node createTermElement(Document xmldoc, String elementName, TermInterface term) {
//		Element termElement=xmldoc.createElement(elementName);
//		Element e=xmldoc.createElement("relation");
//		e.setTextContent(term.getName().toLowerCase());
//		termElement.appendChild(e);
//		if(!term.isVariable()){
//			for(TermInterface arg:term.getArgs()){
//				if(arg.isConstant()){
//					e=xmldoc.createElement("argument");
//					e.setTextContent(arg.getName().toLowerCase());
//					termElement.appendChild(e);
//				}else{
//					termElement.appendChild(createTermElement(xmldoc, "argument", arg));
//				}
//			}
//		}else{
//			Logger.getLogger(XMLGameStateWriter.class.getName()).warning("in XMLGameStateWriter.createStateElement: unsupported expression in state:"+term);
//		}
//		return termElement;
//	}

	
	private static Node createTermElement(Document xmldoc, String elementName, TermInterface term) {
		Element termElement=xmldoc.createElement(elementName);
		Element e=xmldoc.createElement("prop-f");
		e.setTextContent(term.getName().toUpperCase());
		termElement.appendChild(e);
		if(!term.isVariable()){
			for(TermInterface arg:term.getArgs()){
				if(arg.isConstant()){
					e=xmldoc.createElement("arg");
					e.setTextContent(arg.getName().toUpperCase());
					termElement.appendChild(e);
				}else{
					termElement.appendChild(createTermElement(xmldoc, "arg", arg));
				}
			}
		}else{
			Logger.getLogger(XMLGameStateWriter.class.getName()).warning("in XMLGameStateWriter.createStateElement: unsupported expression in state:"+term);
		}
		return termElement;
	}

	
	private static Node createScoresElement(Document xmldoc, GameInterface<?, ?> game, Map<?, Integer> goalValues) {
		Element scores=xmldoc.createElement("scores");
		for(Object role:game.getOrderedRoles()){
			Element e=xmldoc.createElement("reward");
			e.setTextContent(goalValues.get(role).toString());
			scores.appendChild(e);
		}
		return scores;
	}

	private static Element createHistoryElement(
			Document xmldoc,
			List<List<String>> moves,
			RoleInterface<? extends TermInterface> role,
			GDLVersion gdlVersion) {
		Element history=xmldoc.createElement("history");
		int s=1;
		//logger.info("createHistoryElement, moves = "+moves);
		for(List<String> jointMove: moves) {
			Element step=xmldoc.createElement("step");
			Element e=xmldoc.createElement("step-number");
			e.setTextContent(""+s);
			step.appendChild(e);
			s++;
			if (gdlVersion == GDLVersion.v1 || role.isNature()) { // Regular GDL, or view of the random player (complete information)
				for(String move: jointMove){
					e=xmldoc.createElement("move");
					e.setTextContent(move);
					step.appendChild(e);
				}
			} else { // GDL-II, for players other than random
				// TODO: what do we want to have in the history, for GDL-II games, instead of the moves history?
			}
			history.appendChild(step);
		}
		return history;
	}
	
	private static Node createLegalMoves(Document xmldoc, List<TermInterface> legalMoves) {
		Element moves = xmldoc.createElement("legalmoves");
		int n=0;
		if (legalMoves != null) {
			for(TermInterface legalMove: legalMoves) {
				Element move=xmldoc.createElement("move");
				Element e=xmldoc.createElement("move-number");
				e.setTextContent(Integer.toString(n));
				move.appendChild(e);
				Node e2=createTermElement(xmldoc, "move-term", legalMove);
				move.appendChild(e2);
				moves.appendChild(move);
				n++;
			}
		}
		return moves;
	}
	
	private static Node createChosenMove(Document xmldoc, TermInterface chosenMove, Boolean confirmed) {
		Element move=xmldoc.createElement("chosenmove");
		if (chosenMove != null) {
			Node e=createTermElement(xmldoc, "move-term", chosenMove);
			move.appendChild(e);
		}
		if (confirmed != null && confirmed) {
			Element e2=xmldoc.createElement("confirmed");
			move.appendChild(e2);
		}
		return move;
	}
	
}

