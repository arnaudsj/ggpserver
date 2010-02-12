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

public class LongMatcher extends ComparableMatcher<Long> {

	private long minValue, maxValue;
	
	public LongMatcher(String id) {
		this(id, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public LongMatcher(String id, long min, long max) {
		super(id);
		this.minValue = min;
		this.maxValue = max;
		int maxLength = -1;
		maxLength = (int)Math.ceil(
							Math.log10(
									Math.max(Math.abs(min), Math.abs(max))
							) + 1);
		if (min<0) {
			maxLength++;
		}
		if(maxLength>-1) {
			patternTextBox.setMaxLength(maxLength);
		}
		long initialValue = Math.min(Math.max(0, minValue), maxValue);
		patternTextBox.setValue(String.valueOf(initialValue));
		initMatcher();
	}

	@Override
	protected Long parsePattern(String pattern) throws IllegalArgumentException {
		Long l = null;
		try {
			l = Long.valueOf(pattern);
			
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Pattern is not a valid integer!");
		}
		if (l<minValue || l>maxValue) {
			throw new IllegalArgumentException("Number out of range ["+minValue+", "+maxValue+"]!");
		}
		return l;
	}

}
