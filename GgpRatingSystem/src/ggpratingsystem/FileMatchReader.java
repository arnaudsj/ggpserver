/*
    Copyright (C) 2008,2009 Martin Günther <mintar@gmx.de>

    This file is part of GgpRatingSystem.

    GgpRatingSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GgpRatingSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GgpRatingSystem.  If not, see <http://www.gnu.org/licenses/>.
*/

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
 * This class is responsible for for reading 
 * match data from a file.
 */

public class FileMatchReader {
	private static final Logger log = Logger.getLogger(FileMatchReader.class.getName());
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}

	private PlayerSet playerSet;
	
	public FileMatchReader(PlayerSet playerSet) {
		this.playerSet = playerSet;
	}
	
	public Match readMatch(MatchSet matchSet, File xmlFile) throws MatchParsingException {

		// Data available in the XML source
		String matchId;
		List<Player> players;
		List<Integer> scores;

		log.fine("processing XML file: " + xmlFile);
		
		try {
			/* parse matchId */
			XPathResult result = queryXPath(xmlFile, "/match/match-id");

			Node firstResult = result.iterateNext();
			if (firstResult != null) {
				// competition 2007 XML format: there is a node called "match-id"
				
				matchId = firstResult.getTextContent();
			} else {
				// competition 2008 XML format: "id" is an attribute of node "match"
				result = queryXPath(xmlFile, "/match");

				firstResult = result.iterateNext();
				
				if (firstResult == null) {
					throw new MatchParsingException("XPath query for match id returned no results!");				
				}
				matchId = firstResult.getAttributes().getNamedItem("id").getTextContent();
			}

			if (result.iterateNext() != null) {
				throw new MatchParsingException("XPath query for match id returned more than one result!");
			}
			
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
			
			players = new LinkedList<Player>();

			for (Node n = result.iterateNext(); n != null; n = result.iterateNext()) {
				// n = <player>FLUXPLAYER</player>
				Node firstChild = n.getFirstChild(); // firstChild = FLUXPLAYER
				
				log.finest("Player:    " + firstChild.getTextContent());
				players.add(playerSet.getPlayer(firstChild.getTextContent()));

				if (n.getChildNodes().getLength() > 1) {
					throw new MatchParsingException("XPath query for players returned a node with several children!");
				}
			}
			
			/* parse scores */
			scores = new LinkedList<Integer>();

			result = queryXPath(xmlFile, "/match/scores/reward");   // 2007 XML format
			
			List<Node> results = new LinkedList<Node>();
			
			for (Node n = result.iterateNext(); n != null; n = result.iterateNext()) {
				results.add(n);
			}
			
			if (results.size() == 0) {
				result = queryXPath(xmlFile, "/match/rewards/reward");   // 2008 XML format
				
				for (Node n = result.iterateNext(); n != null; n = result.iterateNext()) {
					results.add(n);
				}				
			}
			
			for (Node n : results) {
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
		
		return new Match(matchSet, matchId, players, scores);
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
	
}
