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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static tud.gamecontroller.traces.MatchTrace.*;

public class MatchTraceWriter {
	/**
	 * @throws java.io.IOException if the output file cannot be written to
	 */
	public void write(MatchTrace trace, File outputFile) throws IOException {
		ByteArrayOutputStream outputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			Document xmldoc = createXML(trace);
			outputStream = createXMLOutputStream(xmldoc);

			fileOutputStream = (new FileOutputStream(outputFile));
			fileOutputStream.write(outputStream.toByteArray());
		} finally {
			try {
				if (outputStream != null) outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fileOutputStream != null) fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ByteArrayOutputStream createXMLOutputStream(Document xmldoc) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			// Serialization through Transform.
			DOMSource domSource = new DOMSource(xmldoc);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer serializer = transformerFactory.newTransformer();
			
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

			serializer.transform(domSource, new StreamResult(outputStream));

			return outputStream;
		} catch (TransformerException ex) {
			throw new InternalError("Error setting XML transformer property: " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			throw new InternalError("Error setting XML transformer property: " + ex.getMessage());
		}
	}

	private Document createXML(MatchTrace trace) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();

			// Document.
			Document xmldoc = impl.createDocument(null, null, null);
			xmldoc.setXmlVersion("1.0");
			xmldoc.appendChild(impl.createDocumentType(GAMETRACE, null, null));

			// Root element.
			Element root = xmldoc.createElement(GAMETRACE);
			xmldoc.appendChild(root);

			root.appendChild(createGameElement(trace.getGameName(), xmldoc));
			root.appendChild(createStepsElement(trace.getSteps(), xmldoc));

			return xmldoc;
		} catch (ParserConfigurationException ex) {
			throw new InternalError("Could not configure XML parser: " + ex.getMessage());
		}
	}

	private Node createGameElement(String gameName, Document xmldoc) {
		Element gameElement = xmldoc.createElement(GAME);
		gameElement.setTextContent(gameName);

		return gameElement;
	}

	private Node createStepsElement(List<TracedStep> steps, Document xmldoc) {
		Element stepsElement = xmldoc.createElement(STEPS);
		for (TracedStep step : steps) {
			stepsElement.appendChild(createStepElement(step, xmldoc));
		}
		return stepsElement;
	}

	private Node createStepElement(TracedStep step, Document xmldoc) {
		Element stepElement = xmldoc.createElement(STEP);
		stepElement.setAttribute(STEP_NUMBER, String.valueOf(step.getStepNumber()));
		if (step.getFluents() != null) {
			stepElement.appendChild(createStateElement(step.getFluents(), xmldoc));
		}
		if (step.getLegalMoves() != null) {
			stepElement.appendChild(createLegalMovesElement(step.getLegalMoves(), xmldoc));
		}
		if (step.getMoves() != null) {
			stepElement.appendChild(createMovesElement(step.getMoves(), xmldoc));
		}
		if (step.isTerminal()) {
			stepElement.appendChild(createTerminalElement(xmldoc));
		}
		if (step.getGoalValues() != null) {
			stepElement.appendChild(createGoalValuesElement(step.getGoalValues(), xmldoc));
		}

		return stepElement;
	}

	private Node createStateElement(List<String> fluents, Document xmldoc) {
		Element stateElement = xmldoc.createElement(STATE);
		
		List<String> sortedFluents = new LinkedList<String>(fluents);
		Collections.sort(sortedFluents);
		
		for (String fluent: sortedFluents) {
			stateElement.appendChild(createFluentElement(fluent, xmldoc));
		}
		return stateElement;
	}
	
	private Node createFluentElement(String fluent, Document xmldoc) {
		Element fluentElement = xmldoc.createElement(FLUENT);
		fluentElement.setTextContent(fluent);
		return fluentElement;
	}

	private Node createLegalMovesElement(Map<String, List<String>> legalMoves, Document xmldoc) {
		Element legalMovesElement = xmldoc.createElement(LEGAL_MOVES);
		List<String> roles = new LinkedList<String>(legalMoves.keySet());
		Collections.sort(roles);
		
		for (String role : roles) {
			List<String> moves = new LinkedList<String>(legalMoves.get(role));
			Collections.sort(moves);
			
			for (String move : moves) {
				legalMovesElement.appendChild(createMoveElement(role, move, xmldoc));
			}
		}
		
		return legalMovesElement;
	}

	private Node createMovesElement(Map<String, String> moves, Document xmldoc) {
		Element movesElement = xmldoc.createElement(MOVES);
		List<String> roles = new LinkedList<String>(moves.keySet());
		Collections.sort(roles);
		
		for (String role : roles) {
			movesElement.appendChild(createMoveElement(role, moves.get(role), xmldoc));
		}
		
		return movesElement;
	}

	private Node createMoveElement(String role, String move, Document xmldoc) {
		Element moveElement = xmldoc.createElement(MOVE);
		
		moveElement.setAttribute(ROLE ,role);
		moveElement.setTextContent(move);
		
		return moveElement;
	}
	
	private Node createTerminalElement(Document xmldoc) {
		Element terminalElement = xmldoc.createElement(TERMINAL);
		return terminalElement;
	}

	private Node createGoalValuesElement(Map<String, List<Integer>> goalValuesMap, Document xmldoc) {
		Element goalValuesElement = xmldoc.createElement(GOAL_VALUES);
		
		List<String> roles = new LinkedList<String>(goalValuesMap.keySet());
		Collections.sort(roles);
		
		for (String role : roles) {
			List<Integer> goalValues = new LinkedList<Integer>(goalValuesMap.get(role));
			Collections.sort(goalValues);
			
			for (Integer goalValue : goalValues) {
				goalValuesElement.appendChild(createGoalValueElement(role, goalValue, xmldoc));
			}
		}
		
		return goalValuesElement;
	}

	private Node createGoalValueElement(String role, Integer goalValue, Document xmldoc) {
		Element moveElement = xmldoc.createElement(GOAL_VALUE);
		
		moveElement.setAttribute(ROLE ,role);
		moveElement.setTextContent(goalValue.toString());
		
		return moveElement;
	}
}
