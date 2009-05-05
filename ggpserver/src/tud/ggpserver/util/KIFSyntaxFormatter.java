/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.util;


public class KIFSyntaxFormatter {
	private final String gameDescription;
	private String formattedGameDescription = null;

	public KIFSyntaxFormatter(final String gameDescription) {
		super();
		this.gameDescription = gameDescription;
	}

	public String getGameDescription() {
		return gameDescription;
	}

	/**
	 * Returns a HTML-formatted form of the game description
	 */
	public String getFormattedGameDescription() {
		if (formattedGameDescription == null) {
			formattedGameDescription = format(gameDescription);
		}
		return formattedGameDescription;
	}

	/**
	 * This could be done in a much nicer way, but I don't want to write a complete KIF parser for this.
	 */
	private static String format(String gameDescription) {
		String result = gameDescription;
		
		result = result.toLowerCase();
		result = formatLinebreaks(result, 0);
		result = formatRules(result);
		
		return result;
	}

	private static String formatLinebreaks(String input, int indentLevel) {
		StringBuilder output = new StringBuilder();
		formatRule(input, output, 0);
		
		return output.toString();
	}

	/**
	 * @return number of scanned characters
	 */
	private static int formatRule(String input, StringBuilder output, int indentLevel) {
		int curLevel = 0;
		
		for (int curIndex = 0; curIndex < input.length(); curIndex++) {
			char currentChar = input.charAt(curIndex);
			output.append(currentChar);
			
			if (currentChar == '(') {
				curLevel++;
			} else if (currentChar == ')') {
				curLevel--;
				
				// insert a "<br />\n" whenever the desired indentation level is reached
				if ((curLevel == indentLevel)) {
					if ((curIndex == input.length()) || (input.charAt(curIndex + 1)) != ')') {
						output.append("<br>\n");
						for (int i = 0; i < indentLevel; i++) {
							output.append("&nbsp;&nbsp;&nbsp");
						}
					}
				} else if (curLevel == indentLevel - 1) {
					return curIndex;
				}
			}
		}
		
		return input.length();
	}
	
	private static String formatRules(String input) {
		StringBuilder output = new StringBuilder();
		
		int lastIndex = 0;
		int curIndex = input.indexOf("(<=");
		
		while (curIndex != -1) {
			// write stuff between rules 
			output.append(input.substring(lastIndex, curIndex));
			
			int ruleLength = formatRule(input.substring(curIndex), output, 1);
				// this doesn't catch atoms without parantheses yet. 
			
			lastIndex = curIndex + ruleLength;
			curIndex = input.indexOf("(<=", lastIndex + 3);
		}
		
		return output.toString();
	}
	
}
