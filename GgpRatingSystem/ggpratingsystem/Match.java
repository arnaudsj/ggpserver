package ggpratingsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xpath.domapi.XPathEvaluatorImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathNSResolver;
import org.w3c.dom.xpath.XPathResult;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Match.java
 * 
 * This class represents a single played match.
 * It is responsible for storing the match data and for reading 
 * match data from various sources of information.
 */

public class Match {
	private static final Logger log = Logger.getLogger(Match.class.getName());
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}
	
	private final MatchSet matchSet;
	
	// Data available in the XML source
	private final String matchId;
	private final List<Player> players;
	private final List<Integer> scores;
	
	/**
	 * @param matchSet
	 * @param game
	 */
	public Match(MatchSet matchSet, File xmlFile) throws MatchParsingException {
		super();

		this.matchSet = matchSet;

		log.fine("processing XML file: " + xmlFile);
		
		try {
			/* parse matchId */
			XPathResult result = queryXPath(xmlFile, "/match/match-id");

			Node firstResult = result.iterateNext();
			if (firstResult == null) {
				throw new MatchParsingException("XPath query for match id returned no results!");
			}
			
			if (result.iterateNext() != null) {
				throw new MatchParsingException("XPath query for match id returned more than one result!");
			}
			
			this.matchId = firstResult.getTextContent();

			log.finest("matchId: " + matchId);

			
			/* parse roles */
			result = queryXPath(xmlFile, "/match/role");
			
			List<String> roles = new LinkedList<String>();

			for (Node n = result.iterateNext(); n != null; n = result.iterateNext()) {
				
				// n = <role>White</role>
				Node firstChild = n.getFirstChild(); // firstChild = White
				
				log.finest("Role:    " + firstChild.getTextContent());
				roles.add(firstChild.getTextContent());

				if (n.getChildNodes().getLength() > 1) {
					throw new MatchParsingException("XPath query for roles returned a node with several children!");
				}
			}
			
			matchSet.getGame().setRoles(roles);	// TODO What an ugly hack. Fix this in the future when roles of a game are available directly and not only via the matches.  
			
			/* parse players */
			result = queryXPath(xmlFile, "/match/player");
			
			this.players = new LinkedList<Player>();

			for (Node n = result.iterateNext(); n != null; n = result.iterateNext()) {
				// n = <player>FLUXPLAYER</player>
				Node firstChild = n.getFirstChild(); // firstChild = FLUXPLAYER
				
				log.finest("Player:    " + firstChild.getTextContent());
				players.add(Player.getInstance(firstChild.getTextContent()));

				if (n.getChildNodes().getLength() > 1) {
					throw new MatchParsingException("XPath query for players returned a node with several children!");
				}
			}
			
			/* parse scores */
			result = queryXPath(xmlFile, "/match/scores/reward");
			
			this.scores = new LinkedList<Integer>();

			for (Node n = result.iterateNext(); n != null; n = result.iterateNext()) {
				// n = <reward>100</reward>
				Node firstChild = n.getFirstChild(); // firstChild = 100
				
				log.finest("Score:    " + firstChild.getTextContent());
				scores.add(Integer.valueOf(firstChild.getTextContent()));

				if (n.getChildNodes().getLength() > 1) {
					throw new MatchParsingException("XPath query for players returned a node with several children!");
				}
			}

			// sanity check 
			if (roles.size() != players.size() || players.size() != scores.size() || roles.isEmpty()) {
				throw new MatchParsingException("All 3 lists (roles, players, scores) must have the same number of elements and must not be empty!"); 
			}
		} catch (FileNotFoundException e) {
			MatchParsingException thrown = new MatchParsingException("XML file was not found: " + xmlFile, e);
			throw thrown;
		} catch (SAXException e) {
			MatchParsingException thrown = new MatchParsingException("SAXException while parsing XML file: " + xmlFile, e);
			throw thrown;
		} catch (IOException e) {
			MatchParsingException thrown = new MatchParsingException("IOException while parsing XML file: " + xmlFile, e);
			throw thrown;
		} catch (ParserConfigurationException e) {
			MatchParsingException thrown = new MatchParsingException("ParserConfigurationException while parsing XML file: " + xmlFile, e);
			throw thrown;
		}
	}

	public String getMatchId() {
		return matchId;
	}

	public MatchSet getMatchSet() {
		return matchSet;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Integer> getScores() {
		return scores;
	}

	@Override
	public String toString() {
		return getMatchId();
	}
	
	private static XPathResult queryXPath(File xmlFile, String xpath) throws SAXException, IOException, ParserConfigurationException, MatchParsingException  {
		if (xmlFile == null || xpath == null || xpath.length() == 0) {
			throw new MatchParsingException("Bad input arguments: " + xmlFile + ", " + xpath);
		}

		// Set up a DOM tree to query.
		InputSource in = new InputSource(new FileInputStream(xmlFile));
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		dfactory.setNamespaceAware(true);
		
		// Disable loading of external DTDs. This has to be done in case that
		// games.stanford.edu is down (again). Otherwise we'll get connection
		// timeouts.
		try {
			dfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		} catch (ParserConfigurationException e) {
			// Parser does not support this feature. We'll try to do without.
			log.warning("XML Parser does not support disabling of feature load-external-dtd!");
		}

		Document doc = dfactory.newDocumentBuilder().parse(in);

		// Create an XPath evaluator and pass in the document.
		XPathEvaluator evaluator = new XPathEvaluatorImpl(doc);
		XPathNSResolver resolver = evaluator.createNSResolver(doc);

		// Evaluate the xpath expression
		XPathResult result = (XPathResult) evaluator.evaluate(xpath, doc,
				resolver, XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
		return result;
	}
	
	/**
	 * simply print out some debug info
	 */
	public void print() {
		String result = toString() + ": " + players.get(0) + "(" + scores.get(0) + ") "; 
		
		for (int i = 1; i < players.size(); i++) {
			Player player = players.get(i);
			result += " vs. " + player + "(" + scores.get(i) + ") ";
		}
		System.out.println(result);
	}
}