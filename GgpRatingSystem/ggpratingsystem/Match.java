package ggpratingsystem;

/**
 * Match.java
 * 
 * This class represents a single played match.
 * It is responsible for storing the match data and for reading 
 * match data from various sources of information.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Match {
	private static final XPathFactory factory = XPathFactory.newInstance();
	private static final XPath xPath = factory.newXPath();		
	private static XPathExpression xPathMatchId;
	private static XPathExpression xPathRoles;
	private static XPathExpression xPathPlayers;
	private static XPathExpression xPathScores;

	private final MatchSet matchSet;
	
	// Data available in the XML source
	private final String matchId;
	// private final List<String> roles;	// TODO: This should be moved to Game; isn't needed anyway
	private final List<Player> players;
	private final List<Integer> scores;
	
	// The following data is available in the XML source, but not needed here
	// private final History history;	
	// private final State finalState;
	
	/**
	 * @param matchSet
	 * @param game
	 * @throws FileNotFoundException 
	 * @throws XPathExpressionException 
	 */
	public Match(MatchSet matchSet, File xmlDocument)
			throws FileNotFoundException {
		super();
		
		
		try {
			if (xPathMatchId == null)
				xPathMatchId = xPath.compile("/match/match-id");
			if (xPathRoles == null)
				xPathRoles = xPath.compile("/match/role");
			if (xPathPlayers == null)
				xPathPlayers = xPath.compile("/match/player");
			if (xPathScores == null)
				xPathScores = xPath.compile("/match/scores/reward");

			/* parse matchId */
			InputSource inputSource = new InputSource(new FileInputStream(xmlDocument));

			this.matchId = xPathMatchId.evaluate(inputSource);
			System.out.println("processing match: " + matchId);

			/* parse roles */

			// Stream was closed by the parser (xPath) after use, open a new one
			// (this is why we don't need to close them explicitly)
			inputSource = new InputSource(new FileInputStream(xmlDocument));

			NodeList nodeList = (NodeList) xPathRoles.evaluate(inputSource, XPathConstants.NODESET);

			List<String> roles = new LinkedList<String>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node element = nodeList.item(i); // element = <role>White</role>
				Node firstChild = element.getFirstChild(); // firstChild = White
				// System.out.println("Role: " + firstChild.getTextContent());
				roles.add(firstChild.getTextContent());
			}
			
			matchSet.getGame().setRoles(roles);	// FIXME What an ugly hack.

			/* parse players */
			inputSource = new InputSource(new FileInputStream(xmlDocument));
			nodeList = (NodeList) xPathPlayers.evaluate(inputSource, XPathConstants.NODESET);

			this.players = new LinkedList<Player>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node element = nodeList.item(i); // element = <player>FLUXPLAYER</player>
				Node firstChild = element.getFirstChild(); // firstChild = FLUXPLAYER
				// System.out.println("Player: " + firstChild.getTextContent());
				players.add(Player.getInstance(firstChild.getTextContent()));
			}

			/* parse scores */
			inputSource = new InputSource(new FileInputStream(xmlDocument));
			nodeList = (NodeList) xPathScores.evaluate(inputSource, XPathConstants.NODESET);

			this.scores = new LinkedList<Integer>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node element = nodeList.item(i); // element = <reward>100</reward>
				Node firstChild = element.getFirstChild(); // firstChild = 100
				// System.out.println("Score: " + firstChild.getTextContent());
				scores.add(new Integer(firstChild.getTextContent()));
			}

			// sanity check: all 3 lists must have the same number of elements, and at least one
			if (roles.size() != players.size() || players.size() != scores.size() || roles.size() == 0) {
//				throw new RuntimeException("Invalid XML file: " + xmlDocument.toString());
				System.out.println("Invalid XML file: " + xmlDocument.toString());
			}

			this.matchSet = matchSet;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException(
					"XPath expression could not be evaluated! This is an internal error and should not happen!",
					e);
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
}