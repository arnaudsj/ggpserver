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

import java.util.List;
import java.util.logging.Logger;

import tud.ggpserver.filter.htmlform.DropDownMenu;
import tud.ggpserver.filter.htmlform.HtmlForm;
import tud.ggpserver.filter.htmlform.TextBox;
import tud.ggpserver.filter.htmlform.DropDownMenu.Option;

public abstract class Matcher<ComparisonType, PatternType> extends HtmlForm {

	private ComparisonType comparison = null;
	private PatternType pattern = null;

	public Matcher(String id, ComparisonType comparison, PatternType pattern) {
		super(id);
		setComparison(comparison);
		setPattern(pattern);
	}

	public ComparisonType getComparison() {
		return comparison;
	}

	public boolean setComparison(ComparisonType comparison) {
		if(comparison==this.comparison || (comparison!=null && comparison.equals(this.comparison)))
			return false;
		this.comparison = comparison;
		return true;
	}

	/**
	 * called upon update of the comparison
	 * 
	 * The method should check if the comparison is correct, setting error messages using addErrorMessage(String msg) if necessary.
	 */
	public abstract boolean setComparisonFromString(String comparison);

	/**
	 * Override this method to change the string representation of the comparison in case ComparisonType.toString() does not do the right thing.
	 */
	public String getStringForComparison() {
		return comparison.toString();
	}

	public PatternType getPattern() {
		return pattern;
	}

	public boolean setPattern(PatternType pattern) {
		if(pattern==this.pattern || (pattern!=null && pattern.equals(this.pattern)))
			return false;
		this.pattern = pattern;
		return true;
	}

	/**
	 * called upon update of the pattern
	 * 
	 * The method should check if the pattern is correct, setting error messages using addErrorMessage(String msg) if necessary.
	 */
	public abstract boolean setPatternFromString(String pattern);

	/**
	 * Override this method to change the string representation of the pattern in case PatternType.toString() does not do the right thing.
	 */
	public String getStringForPattern() {
		return pattern.toString();
	}

	/**
	 * Override this method to change the DropDownMenu. The default implementation uses getOptions to get a list of Options for the menu. 
	 * @see getOptions
	 * @return the initial DropDownMenu
	 */
	protected DropDownMenu createMenu() {
		List<Option> options = getOptions();
		return new DropDownMenu(getId(), options, getStringForComparison());
	}

	/**
	 * Override this method to change the TextBox for the pattern.
	 * @return the initial TextBox
	 */
	protected TextBox createTextBox() {
		String patternString = "*";
		if(pattern!=null)
			patternString = getStringForPattern();
		return new TextBox(getId(), patternString);
	}

	public abstract List<Option> getOptions();

	@Override
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		DropDownMenu comparisonMenu = createMenu();
		TextBox patternTextBox = createTextBox();
		sb.append(comparisonMenu.getHtml()).append(patternTextBox.getHtml()).append(getErrorHtml());
		return sb.toString();
	}

	public boolean update(String comparison, String pattern) {
		resetErrorMessage();
		boolean changed = false;
		if(setComparisonFromString(comparison))
			changed = true;
		if(setPatternFromString(pattern))
			changed = true;
		return changed;
	}
	
	public abstract boolean isMatching(PatternType t);

	@Override
	public String toString() {
		return comparison+" "+pattern;
	}
}
