package tud.gamecontroller;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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

import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;

public class XMLGameStateWriter implements GameControllerListener {
	private String outputDir, matchDir;
	private Player[] players;
	private List<Move[]> moves;
	private int step;
	private Match match;
	private String stylesheet;
		
	public XMLGameStateWriter(String outputDir, String stylesheet) {
		this.outputDir=outputDir;
		this.stylesheet=stylesheet;
		this.moves=new LinkedList<Move[]>();
		this.match=null;
	}

	public void gameStarted(Match match, Player[] players, State currentState) {
		this.match=match;
		this.players=players;
		this.step=1;
		matchDir=outputDir+File.separator+match.getMatchID();
		(new File(matchDir)).mkdirs();
		writeState(currentState, null);
	}

	public void gameStep(Move[] priormoves, State currentState) {
		step++;
		moves.add(priormoves);
		writeState(currentState, null);
	}

	public void gameStopped(State currentState, int[] goalValues) {
		writeState(currentState, goalValues);
		this.moves=new LinkedList<Move[]>();
	}

	private void writeState(State currentState, int[] goalValues) {
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
			 for(int i=0;i<players.length;i++){
				 e=xmldoc.createElement("player");
				 e.setTextContent(players[i].getName().toUpperCase());
				 root.appendChild(e);
			 }
			 root.appendChild(createHistoryElement(xmldoc));
			 if(goalValues!=null) root.appendChild(createScoresElement(xmldoc, goalValues));
			 root.appendChild(createStateElement(xmldoc, currentState));
			 // Serialization through Transform.
			 DOMSource domSource = new DOMSource(xmldoc);
			 StreamResult streamResult = new StreamResult(new File(matchDir+File.separator+"step_"+step+".xml"));
			 TransformerFactory tf = TransformerFactory.newInstance();
			 Transformer serializer;
			 serializer = tf.newTransformer();
			 serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
			 serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"http://games.stanford.edu/gamemaster/xml/viewmatch.dtd");
			 serializer.setOutputProperty(OutputKeys.INDENT,"yes");
			 serializer.transform(domSource, streamResult); 
			 			 
		} catch (TransformerConfigurationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (ParserConfigurationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (TransformerException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	private Node createStateElement(Document xmldoc, State currentState) {
		Element state=xmldoc.createElement("state");
		Collection<Fluent> fluents=currentState.getFluents();
		for(Fluent f:fluents){
			Element fact=xmldoc.createElement("fact");
			Element e=xmldoc.createElement("prop-f");
			e.setTextContent(f.expr.firstOp().toString().toUpperCase());
			fact.appendChild(e);
			if(f.expr instanceof Predicate){
				ExpList el=((Predicate)f.expr).getOperands();
				for(int i=0;i<el.size();i++){
					Expression exp=el.get(i);
					e=xmldoc.createElement("arg");
					if(exp instanceof Atom){
						e.setTextContent(exp.toString().toUpperCase());
					}else{
						e.setTextContent("?");
						System.err.println("XMLGameStateWriter: unsupported expression as argument of a fluent:"+exp);
					}
					fact.appendChild(e);
				}
			}else if(!(f.expr instanceof Atom)){
				System.err.println("XMLGameStateWriter: unsupported fluent expression in state:"+f.expr);
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
		for(Move[] move:moves){
			Element step=xmldoc.createElement("step");
			Element e=xmldoc.createElement("step-number");
			e.setTextContent(""+s);
			step.appendChild(e);
			for(int i=0;i<move.length;i++){
				e=xmldoc.createElement("move");
				e.setTextContent(ExpressionFormatter.prefixForm(move[i].expr));
				step.appendChild(e);
			}
			history.appendChild(step);
			s++;
		}
		return history;
	}

}
