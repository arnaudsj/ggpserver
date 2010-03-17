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

import tud.ggpserver.filter.htmlform.TextBox;

public class LongMatcher extends ComparableMatcher<Long> {

	private long minValue, maxValue;
	
	public LongMatcher(String id) {
		this(id, Comparison.Equal, null, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public LongMatcher(String id, Comparison comparison, Long pattern) {
		this(id, comparison, pattern, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public LongMatcher(String id, Comparison comparison, Long pattern, long min, long max) {
		super(id, comparison, pattern);
		this.minValue = min;
		this.maxValue = max;
		// long initialValue = Math.min(Math.max(0, minValue), maxValue);
	}

	@Override
	protected TextBox createTextBox() {
		TextBox patternTextBox = super.createTextBox();
		int maxLength = -1;
		maxLength = (int)Math.ceil(
							Math.log10(
									Math.max(Math.abs(minValue), Math.abs(maxValue))
							) + 1);
		if (minValue<0) {
			maxLength++;
		}
		if(maxLength>-1) {
			patternTextBox.setMaxLength(maxLength);
		}
		return patternTextBox;
	}

	@Override
	public boolean setPatternFromString(String pattern) {
		if(pattern.equals("*")) {
			return setPattern(null);
		} else {
			try {
				Long l = null;
				l = Long.valueOf(pattern);
				if (l<minValue || l>maxValue) {
					addErrorMessage("Number out of range ["+minValue+", "+maxValue+"]!");
				} else {
					return setPattern(l);
				}
			} catch (NumberFormatException ex) {
				addErrorMessage("Pattern is neither '*' nor a valid integer!");
			}
		}
		return false;
	}
}
