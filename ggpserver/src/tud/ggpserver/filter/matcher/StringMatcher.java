/*
    Copyright (C) 2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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

package tud.ggpserver.filter.matcher;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import tud.ggpserver.filter.htmlform.TextBox;
import tud.ggpserver.filter.htmlform.DropDownMenu.Option;

public class StringMatcher extends Matcher<String> {

	private static final String IS = "is"; 
	private static final String ISNOT = "is not"; 

	protected Pattern pattern;

	public StringMatcher(String id) {
		super(id);
	}

	@Override
	public List<Option> getOptions() {
		return Arrays.asList(new Option(IS), new Option(ISNOT));
	}
	
	@Override
	public TextBox createTextBox() {
		return new TextBox(getId(), "*");
	}

	@Override
	protected void initMatcher() {
		String patternString;
		patternString = patternTextBox.getValue().replace('?', '.');
		patternString = patternString.replace("*", ".*");
		try {
			pattern = Pattern.compile(patternString);
		} catch(PatternSyntaxException ex) {
			addErrorMessage("invalid pattern: \""+ patternString.substring(Math.max(ex.getIndex()-2,0), Math.min(ex.getIndex()+2, patternString.length())) + "\"");
		}
	}

	public boolean isMatching(String s) {
		return (!patternMatches(s)) ^ comparisonMenu.getSelectedValue().equals(IS); // (matches && "is") || (!matches && !"is")   
	}

	public boolean patternMatches(String s) {
		return pattern.matcher(s).matches();
	}

	public boolean shouldMatch() {
		return comparisonMenu.getSelectedValue().equals("is");
	}

}
