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

import tud.ggpserver.filter.htmlform.DropDownMenu.Option;

public class StringMatcher extends Matcher<Boolean, String> {

	private static final String IS = "is"; 
	private static final String ISNOT = "is not"; 

	protected Pattern regExpPattern = null;

	public StringMatcher(String id, boolean isMatch, String pattern) {
		super(id, isMatch, pattern);
	}

	@Override
	public List<Option> getOptions() {
		return Arrays.asList(new Option(IS), new Option(ISNOT));
	}
	
	protected void initMatcher() {
		String patternString;
		patternString = getPattern().replace('?', '.');
		patternString = patternString.replace("*", ".*");
		try {
			regExpPattern = Pattern.compile(patternString);
		} catch(PatternSyntaxException ex) {
			addErrorMessage("invalid pattern: \""+ patternString.substring(Math.max(ex.getIndex()-2,0), Math.min(ex.getIndex()+2, patternString.length())) + "\"");
		}
		// Logger.getLogger(StringMatcher.class.getName()).info("initMatcher: " + regExpPattern);
	}

	@Override
	public boolean setPattern(String pattern) {
		boolean changed = super.setPattern(pattern);
		if(changed || regExpPattern == null) {
			initMatcher();
		}
		return changed;
		
	}
	
	public boolean isMatching(String s) {
		return (!patternMatches(s)) ^ getComparison(); // (matches && "is") || (!matches && !"is")   
	}

	public boolean patternMatches(String s) {
		// Logger.getLogger(StringMatcher.class.getName()).info("regExp: \"" + regExpPattern+ "\", s:\"" + s + "\"");
		return s!=null && regExpPattern.matcher(s).matches();
	}

	public boolean shouldMatch() {
		return getComparison();
	}

	@Override
	public boolean setComparisonFromString(String comparison) {
		if (comparison.equals(IS)) {
			return setComparison(true);
		} else if(comparison.equals(ISNOT)) {
			return setComparison(false);
		} else {
			addErrorMessage("invalid comparison: \""+comparison+"\"");
			return false;
		}
	}

	@Override
	public boolean setPatternFromString(String pattern) {
		return setPattern(pattern);
	}

	@Override
	public String getStringForComparison() {
		if(getComparison())
			return IS;
		else
			return ISNOT;
	}
}
