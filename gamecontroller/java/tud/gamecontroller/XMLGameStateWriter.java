package tud.gamecontroller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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

import tud.gamecontroller.game.Fluent;
import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.players.Player;

public class XMLGameStateWriter<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> implements GameControllerListener<T,S> {

	private String outputDir, matchDir;
	private List<Player<T,S>> players;
	private List<List<Move<T>>> moves;
	private int step;
	private Match<T,S> match;
	private String stylesheet;
		
	public XMLGameStateWriter(String outputDir, String stylesheet) {
		this.outputDir=outputDir;
		this.stylesheet=stylesheet;
		this.moves=new LinkedList<List<Move<T>>>();
		this.match=null;
	}

	public void gameStarted(Match<T,S> match, List<Player<T,S>> players, S currentState) {
		this.match=match;
		this.players=players;
		this.step=1;
		matchDir=outputDir+File.separator+match.getMatchID();
		(new File(matchDir)).mkdirs();
		writeState(currentState, null);
	}

	public void gameStep(List<Move<T>> priormoves, S currentState) {
		step++;
		moves.add(priormoves);
		writeState(currentState, null);
	}

	public void gameStopped(S currentState, int[] goalValues) {
		writeState(currentState, goalValues);
		this.moves=new LinkedList<List<Move<T>>>();
	}

	private void writeState(S currentState, int[] goalValues) {
		 Document xmldoc = null;
		 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			 DOMImplementation impl = builder.getDOMImplementation();
			 Element e = null;
			 // Document.
			 xmldoc = impl.createDocument(null, null, null);
			 xmldoc.setXmlVersion("1.0");
			 Node xsl=xmldoc.createProcessingInstruction("xml-stylesheet","type=\"text/xsl\" href=\""+stylesheet+"\"");
			 xmldoc.appendChild(xsl);
			 xmldoc.appendChild(impl.createDocumentType("match", null, "http://games.stanford.edu/gamemaster/xml/viewmatch.dtd"));
			 // Root element.
			 Element root = xmldoc.createElement("match");
			 xmldoc.appendChild(root);
			 e=xmldoc.createElement("match-id");
			 e.setTextContent(match.getMatchID());
			 root.appendChild(e);
			 for(int i=1;i<=match.getGame().getNumberOfRoles();i++){
				 e=xmldoc.createElement("role");
				 e.setTextContent(match.getGame().getRole(i).toString());
				 root.appendChild(e);
			 }
			 for(Player<T,S> p:players){
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
			 if(goalValues==null){ // don't write playclock if the game is over
				 e=xmldoc.createElement("playclock");
				 e.setTextContent(Integer.toString(match.getPlayclock()));
				 root.appendChild(e);
			 }
			 root.appendChild(createHistoryElement(xmldoc));
			 if(goalValues!=null) root.appendChild(createScoresElement(xmldoc, goalValues));
			 root.appendChild(createStateElement(xmldoc, currentState));
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
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Node createStateElement(Document xmldoc, StateInterface<T,S> currentState) {
		Element state=xmldoc.createElement("state");
		Collection<Fluent<T>> fluents=currentState.getFluents();
		for(Fluent<T> f:fluents){
			Element fact=xmldoc.createElement("fact");
			Element e=xmldoc.createElement("prop-f");
			e.setTextContent(f.getName().toUpperCase());
			fact.appendChild(e);
			if(!f.isVariable()){
				for(TermInterface arg:f.getArgs()){
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

	private Node createScoresElement(Document xmldoc, int[] goalValues) {
		Element scores=xmldoc.createElement("scores");
		for(int i=0;i<goalValues.length;i++){
			Element e=xmldoc.createElement("reward");
			e.setTextContent(""+goalValues[i]);
			scores.appendChild(e);
		}
		return scores;
	}

	private Element createHistoryElement(Document xmldoc) {
		Element history=xmldoc.createElement("history");
		int s=1;
		for(List<Move<T>> jointmove:moves){
			Element step=xmldoc.createElement("step");
			Element e=xmldoc.createElement("step-number");
			e.setTextContent(""+s);
			step.appendChild(e);
			for(Move<T> move:jointmove){
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
