/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>

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

package tud.gamecontroller.traces;

import static tud.gamecontroller.traces.MatchTrace.FLUENT;
import static tud.gamecontroller.traces.MatchTrace.GAME;
import static tud.gamecontroller.traces.MatchTrace.GOAL_VALUE;
import static tud.gamecontroller.traces.MatchTrace.GOAL_VALUES;
import static tud.gamecontroller.traces.MatchTrace.LEGAL_MOVES;
import static tud.gamecontroller.traces.MatchTrace.MOVE;
import static tud.gamecontroller.traces.MatchTrace.MOVES;
import static tud.gamecontroller.traces.MatchTrace.ROLE;
import static tud.gamecontroller.traces.MatchTrace.STATE;
import static tud.gamecontroller.traces.MatchTrace.STEP;
import static tud.gamecontroller.traces.MatchTrace.STEPS;
import static tud.gamecontroller.traces.MatchTrace.STEP_NUMBER;
import static tud.gamecontroller.traces.MatchTrace.TERMINAL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MatchTraceReader {
	
	private static final Logger logger = Logger.getLogger(MatchTraceReader.class.getName());

//	static {
//		Handler handler = new ConsoleHandler();
//		handler.setLevel(Level.ALL);
//		logger.setLevel(Level.ALL);
//		logger.setUseParentHandlers(false);
//		logger.addHandler(handler);		
//	}

	/**
	 * @throws org.xml.sax.SAXException if the input file is not valid XML
	 * @throws java.io.IOException if the input file cannot be read
	 */
	public MatchTrace read(File xmlFile) throws SAXException, IOException {
		// Set up a DOM tree to query.
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		dfactory.setNamespaceAware(true);

		Document document;
		try {
			document = dfactory.newDocumentBuilder().parse(xmlFile);
			logger.fine("processing XML file: " + xmlFile);
			return parseGameTrace(document.getDocumentElement());
		} catch (ParserConfigurationException ex) {
			throw new InternalError("Could not configure XML parser: " + ex.getMessage());
		}
	}
	
	private MatchTrace parseGameTrace(Element gametrace) {
		String gameName = null;
		List<TracedStep> steps = null;
		
		NodeList nodes = gametrace.getChildNodes();
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals(GAME)) {
					if (gameName != null) {
						throw new IllegalArgumentException("Multiple <game> tags in XML file!");
					}
					gameName = parseGame(element);
				} else if (element.getTagName().equals(STEPS)) {
					if (steps != null) {
						throw new IllegalArgumentException("Multiple <steps> tags in XML file!");
					}
					steps = parseSteps(element);
				} else {
					logger.warning("ignoring element (expected <game> or <steps>): " + element);
				}
			}
		}
		
		if (gameName == null) {
			throw new IllegalArgumentException("Missing <game> tag in XML file!");
		}
		if (steps == null) {
			throw new IllegalArgumentException("Missing <steps> tag in XML file!");
		}
		return new MatchTrace(gameName, steps);
	}
	
	private String parseGame(Element game) {
		logger.finest("game: " + game.getTextContent());
		return game.getTextContent();
	}

	private List<TracedStep> parseSteps(Element stepsNode) {
		NodeList stepNodes = stepsNode.getChildNodes();
		List<TracedStep> tracedSteps = new ArrayList<TracedStep>(stepNodes.getLength());

		for (int i = 0; i < stepNodes.getLength(); i++) {
			Node node = stepNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals(STEP)) {
					TracedStep step = parseStep(element);
					if (step.getStepNumber() != tracedSteps.size() + 1) {
						throw new IllegalArgumentException("wrong step number, expected: " + (tracedSteps.size() + 1)
								+ ", found: " + step.getStepNumber());
					}					
					tracedSteps.add(step);
				} else {
					logger.warning("ignoring element: " + element);
				}
			}
		}
		
		return tracedSteps;
	}
		
	private TracedStep parseStep(Element stepElement) {
		int stepNumber;
		List<String> fluents = null;
		Map<String, List<String>> legalMoves = null;
		Map<String, String> moves = null;
		boolean terminal = false;
		Map<String, List<Integer>> goalValues = null;
		
		/* stepNumber */
		stepNumber = Integer.parseInt(stepElement.getAttribute(STEP_NUMBER));

		/* state, legal_moves, moves, terminal, goal_values */
		NodeList childNodes = stepElement.getChildNodes();

		// TODO: <error msg="blablabla">
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals(STATE)) {
					fluents = parseFluents(element);
				} else if (element.getTagName().equals(LEGAL_MOVES)) {
					legalMoves = parseLegalMoves(element);
				} else if (element.getTagName().equals(MOVES)) {
					moves = parseMoves(element);
				} else if (element.getTagName().equals(TERMINAL)) {
					terminal = true;
				} else if (element.getTagName().equals(GOAL_VALUES)) {
					goalValues = parseGoalValues(element);
				} else {
					logger.warning("ignoring element: " + element);
				}
			}
		}
		
		logger.finest("step number: " + stepNumber);
		logger.finest("state fluents: " + fluents);
		logger.finest("legal moves: " + legalMoves);
		logger.finest("moves: " + moves);
		logger.finest("terminal: " + terminal);
		logger.finest("goal values: " + goalValues);
		
		return new TracedStep(stepNumber, fluents, legalMoves, moves, terminal, goalValues);
	}		

	private List<String> parseFluents(Element state) {
		NodeList nodes = state.getChildNodes();
		List<String> fluents = new ArrayList<String>(nodes.getLength());

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals(FLUENT)) {
					fluents.add(element.getTextContent());
				} else {
					logger.warning("ignoring element: " + element);
				}
			}
		}
		return fluents;
	}
		
	private Map<String, List<String>> parseLegalMoves(Element legalMovesElement) {
		NodeList nodes = legalMovesElement.getChildNodes();
		Map<String, List<String>> legalMoves = new HashMap<String, List<String>>();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals(MOVE)) {
					String role = element.getAttribute(ROLE);
					List<String> legalMovesForRole = legalMoves.get(role);
					if (legalMovesForRole == null) {
						legalMovesForRole = new LinkedList<String>();
					}
					legalMovesForRole.add(element.getTextContent());
					legalMoves.put(role, legalMovesForRole);
				} else {
					logger.warning("ignoring element: " + element);
				}
			}
		}
		
		return legalMoves;
	}
	
	private Map<String, String> parseMoves(Element movesElement) {
		NodeList nodes = movesElement.getChildNodes();
		Map<String, String> moves = new HashMap<String, String>();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals(MOVE)) {
					String role = element.getAttribute(ROLE);
					String oldMove = moves.put(role, element.getTextContent());
					if (oldMove != null) {
						throw new IllegalArgumentException("multiple moves for same role inside <move>!");
					}
				} else {
					logger.warning("ignoring element: " + element);
				}
			}
		}
		
		return moves;
	}
		
	/**
	 * At first, it may seem odd that multiple different goal values are allowed 
	 * for the same role, since that isn't legal in a GDL game. However, it can
	 * happen if the reasoner is buggy, so we need to represent that case.
	 */
	private Map<String, List<Integer>> parseGoalValues(Element goalValuesElement) {
		NodeList nodes = goalValuesElement.getChildNodes();
		Map<String, List<Integer>> goalValues = new HashMap<String, List<Integer>>();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals(GOAL_VALUE)) {
					String role = element.getAttribute(ROLE);
					List<Integer> goalValuesForRole = goalValues.get(role);
					if (goalValuesForRole == null) {
						goalValuesForRole = new LinkedList<Integer>();
					}
					goalValuesForRole.add(Integer.parseInt(element.getTextContent()));
					goalValues.put(role, goalValuesForRole);
				} else {
					logger.warning("ignoring element: " + element);
				}
			}
		}
		
		return goalValues;
	}

}