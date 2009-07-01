/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

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

import tud.auxiliary.NamedObject;
import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.GameObjectInterface;
import tud.gamecontroller.term.TermInterface;

public class XMLGameStateWriter
		implements GameControllerListener {

	private String outputDir, matchDir;
	private List<JointMoveInterface<? extends TermInterface>> moves;
	private int step;
	private RunnableMatchInterface<? extends TermInterface, ?> match;
	private String stylesheet;
		
	public XMLGameStateWriter(String outputDir, String stylesheet) {
		this.outputDir=outputDir;
		this.stylesheet=stylesheet;
		this.moves=new LinkedList<JointMoveInterface<? extends TermInterface>>();
		this.match=null;
	}

	public void gameStarted(RunnableMatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface, ?> currentState) {
		this.match=match;
		this.step=1;
		matchDir=outputDir+File.separator+match.getMatchID();
		(new File(matchDir)).mkdirs();
		writeState(currentState, null);
	}

	public void gameStep(JointMoveInterface<? extends TermInterface> jointMove, StateInterface<? extends TermInterface,?> currentState) {
		step++;
		moves.add(jointMove);
		writeState(currentState, null);
	}

	public void gameStopped(StateInterface<? extends TermInterface,?> currentState, Map<? extends RoleInterface<?>,Integer> goalValues) {
		writeState(currentState, goalValues);
		this.moves=new LinkedList<JointMoveInterface<? extends TermInterface>>();
	}
	
	private void writeState(StateInterface<? extends TermInterface,?> currentState, Map<?, Integer> goalValues) {
		ByteArrayOutputStream os = null; 
		FileOutputStream fileOutputStream = null;
		
		try {
			os = createXMLOutputStream(match, currentState, moves, goalValues, stylesheet);
			fileOutputStream = new FileOutputStream(new File(matchDir+File.separator+"step_"+step+".xml"));
			fileOutputStream.write(os.toByteArray());
			if(goalValues!=null){ // write the final state twice (once as step_X.xml and once as finalstate.xml)
				fileOutputStream.close();
				fileOutputStream = new FileOutputStream(new File(matchDir+File.separator+"finalstate.xml"));
				fileOutputStream.write(os.toByteArray());
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (IOException ex) {
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
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

	public static ByteArrayOutputStream createXMLOutputStream(
			RunnableMatchInterface<? extends TermInterface, ?> match,
			StateInterface<? extends TermInterface, ?> currentState,
			List<JointMoveInterface<? extends TermInterface>> moves,
			Map<?, Integer> goalValues,
			String stylesheet)
			throws TransformerFactoryConfigurationError,
			IllegalArgumentException {
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		try{
			Document xmldoc=createXML(match, currentState, moves, goalValues, stylesheet);
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
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (ParserConfigurationException ex) {
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (TransformerException ex) {
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
		}
		return os;
	}

	/**
	 * 
	 * @param <T> the term-type used for fluents and moves 
	 * @param <S> the state-type used for currentState
	 * @param <PlayerType>
	 * @param match the match the currentState is from
	 * @param currentState the state to be transformed to XML
	 * @param moves the list of joint moves executed so far
	 * @param goalValues a list of goal values for the players or null if this is not a terminal state
	 * @param stylesheet URL of a style sheet or null
	 * @return
	 * @throws ParserConfigurationException
	 */
	static public Document createXML(RunnableMatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface,?> currentState, List<JointMoveInterface<? extends TermInterface>> moves, Map<?, Integer> goalValues, String stylesheet) 
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
		 xmldoc.appendChild(impl.createDocumentType("match", null, "http://games.stanford.edu/gamemaster/xml/viewmatch.dtd"));
		 // Root element.
		 Element root = xmldoc.createElement("match");
		 xmldoc.appendChild(root);
		 e=xmldoc.createElement("match-id");
		 e.setTextContent(match.getMatchID());
		 root.appendChild(e);
		 for(GameObjectInterface role:match.getGame().getOrderedRoles()){
			 e=xmldoc.createElement("role");
			 e.setTextContent(role.getPrefixForm());
			 root.appendChild(e);
		 }
		 for(NamedObject p:match.getOrderedPlayers()){
			 e=xmldoc.createElement("player");
			 e.setTextContent(p.getName().toUpperCase());
			 root.appendChild(e);
		 }
		 e=xmldoc.createElement("timestamp");
		 e.setTextContent(Long.toString(System.currentTimeMillis()));
		 root.appendChild(e);
		 e=xmldoc.createElement("startclock");
		 e.setTextContent(Integer.toString(match.getStartclock()));
		 root.appendChild(e);
		 if(goalValues==null){ // don't write play clock if the game is over
			 e=xmldoc.createElement("playclock");
			 e.setTextContent(Integer.toString(match.getPlayclock()));
			 root.appendChild(e);
		 }
		 root.appendChild(createHistoryElement(xmldoc, moves));
		 if(goalValues!=null) root.appendChild(createScoresElement(xmldoc, match.getGame(), goalValues));
		 root.appendChild(createStateElement(xmldoc, currentState));
		 return xmldoc;
	}

	private static Node createStateElement(Document xmldoc, StateInterface<? extends TermInterface, ?> currentState) {
		Element state=xmldoc.createElement("state");
		Collection<? extends FluentInterface<? extends TermInterface>> fluents=currentState.getFluents();
		for(FluentInterface<? extends TermInterface> f:fluents){
			state.appendChild(createTermElement(xmldoc, "fact", f.getTerm()));
		}
		return state;
	}

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
			Logger.getLogger("tud.gamecontroller").warning("in XMLGameStateWriter.createStateElement: unsupported expression in state:"+term);
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

	private static Element createHistoryElement(Document xmldoc, List<JointMoveInterface<? extends TermInterface>> moves) {
		Element history=xmldoc.createElement("history");
		int s=1;
		for(JointMoveInterface<? extends TermInterface> jointMove:moves){
			Element step=xmldoc.createElement("step");
			Element e=xmldoc.createElement("step-number");
			e.setTextContent(""+s);
			step.appendChild(e);
			for(GameObjectInterface move:jointMove.getOrderedMoves()){
				e=xmldoc.createElement("move");
				e.setTextContent(move.getPrefixForm());
				step.appendChild(e);
			}
			history.appendChild(step);
			s++;
		}
		return history;
	}
}

