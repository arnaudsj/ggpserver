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

/**
 * This Exception is thrown if anything goes wrong while parsing a Match XML file.
 * 
 * @author martin
 *
 */
public class MatchParsingException extends Exception {

	private static final long serialVersionUID = -5683610441304293710L;

	public MatchParsingException() {
		super();
	}

	public MatchParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MatchParsingException(String message) {
		super(message);
	}

	public MatchParsingException(Throwable cause) {
		super(cause);
	}
}
