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

import tud.ggpserver.filter.htmlform.DropDownMenu;
import tud.ggpserver.filter.htmlform.HtmlForm;
import tud.ggpserver.filter.htmlform.TextBox;
import tud.ggpserver.filter.htmlform.DropDownMenu.Option;

public abstract class Matcher<T> extends HtmlForm {

	protected DropDownMenu comparisonMenu;
	protected TextBox patternTextBox;

	public Matcher(String id) {
		super(id);
		
		comparisonMenu = createMenu();
		patternTextBox = createTextBox();

		initMatcher();
	}

	/**
	 * Override this method to change the DropDownMenu. The default implementation uses getOptions to get a list of Options for the menu. 
	 * @see getOptions
	 * @return the initial DropDownMenu
	 */
	protected DropDownMenu createMenu() {
		List<Option> options = getOptions();
		return new DropDownMenu(getId(), options, options.get(0).value);
	}

	/**
	 * Override this method to change the TextBox for the pattern.
	 * @return the initial TextBox
	 */
	protected TextBox createTextBox() {
		return new TextBox(getId());
	}

	public abstract List<Option> getOptions();

	/**
	 * called upon update of the value of the comparisonMenu or the patternTextBox
	 * 
	 * The method should check of the pattern is correct, setting error messages using addErrorMessage(String msg) if necessary.
	 */
	protected abstract void initMatcher();

	@Override
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append(comparisonMenu.getHtml()).append(patternTextBox.getHtml()).append(getErrorHtml());
		return sb.toString();
	}

	public boolean update(String comparison, String pattern) {
		resetErrorMessage();
		boolean changed = false;
		if (!comparisonMenu.getSelectedValue().equals(comparison)) {
			comparisonMenu.setSelectedValue(comparison);
			changed = true;
		};
		
		if (!patternTextBox.getValue().equals(pattern)) {
			patternTextBox.setValue(pattern);
			changed = true;
		}
		if(changed){
			initMatcher();
		}
		return changed;
	}
	
	public void setPattern(String pattern) {
		resetErrorMessage();
		patternTextBox.setValue(pattern);
		initMatcher();
	}
	
	public abstract boolean isMatching(T t);

	@Override
	public String toString() {
		return comparisonMenu.getSelectedValue()+" "+patternTextBox.getValue();
	}
}
