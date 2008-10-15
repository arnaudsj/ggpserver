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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tud.aux.NamedObject;
import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.GameObjectInterface;
import tud.gamecontroller.term.TermInterface;

public class XMLGameStateWriter
		implements GameControllerListener {

	private String outputDir, matchDir;
	private List<JointMoveInterface<? extends TermInterface>> moves;
	private int step;
	private MatchInterface<? extends TermInterface, ?> match;
	private String stylesheet;
		
	public XMLGameStateWriter(String outputDir, String stylesheet) {
		this.outputDir=outputDir;
		this.stylesheet=stylesheet;
		this.moves=new LinkedList<JointMoveInterface<? extends TermInterface>>();
		this.match=null;
	}

	public void gameStarted(MatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface, ?> currentState) {
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
		try{
			Document xmldoc=createXML(match, currentState, moves, goalValues, stylesheet);
			// Serialization through Transform.
			DOMSource domSource = new DOMSource(xmldoc);
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			StreamResult streamResult = new StreamResult(os);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer;
			serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
			serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"http://games.stanford.edu/gamemaster/xml/viewmatch.dtd");
			serializer.setOutputProperty(OutputKeys.INDENT,"yes");
			serializer.transform(domSource, streamResult);
			(new FileOutputStream(new File(matchDir+File.separator+"step_"+step+".xml"))).write(os.toByteArray());
			if(goalValues!=null){ // write the final state twice (once as step_X.xml and once as finalstate.xml)
				(new FileOutputStream(new File(matchDir+File.separator+"finalstate.xml"))).write(os.toByteArray());
			}
		} catch (TransformerConfigurationException ex) {
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (ParserConfigurationException ex) {
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (TransformerException ex) {
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (FileNotFoundException ex) {
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
		} catch (IOException ex) {
			Logger.getLogger("tud.gamecontroller").warning("Exception occured while generation xml:"+ex.getMessage());
		}
		
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
	static public Document createXML(MatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface,?> currentState, List<JointMoveInterface<? extends TermInterface>> moves, Map<?, Integer> goalValues, String stylesheet) 
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
		 for(NamedObject p:match.getPlayers()){
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
			Element fact=xmldoc.createElement("fact");
			Element e=xmldoc.createElement("prop-f");
			e.setTextContent(f.getTerm().getName().toUpperCase());
			fact.appendChild(e);
			if(!f.getTerm().isVariable()){
				for(TermInterface arg:f.getTerm().getArgs()){
					e=xmldoc.createElement("arg");
					if(arg.isConstant()){
						e.setTextContent(arg.getName().toUpperCase());
					}else{
						e.setTextContent("?");
						Logger.getLogger("tud.gamecontroller").warning("in XMLGameStateWriter.createStateElement: unsupported expression as argument of a fluent:"+arg);
					}
					fact.appendChild(e);
				}
			}else{
				Logger.getLogger("tud.gamecontroller").warning("in XMLGameStateWriter.createStateElement: unsupported fluent expression in state:"+f);
			}
			state.appendChild(fact);
		}
		return state;
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

