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

import ggpratingsystem.ratingsystems.RatingException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.martiansoftware.jsap.JSAPException;

/**
 * @author martin
 * 
 */
public class GgpRatingSystem {
	private static final Logger log = Logger.getLogger(GgpRatingSystem.class.getName());

	public static void main(String[] args) {
		try {
			CommandLineInterface.main(args);
		} catch (JSAPException e) {
			// simply drop all JSAPExceptions (they already have generated output)
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			System.err.println("Fatal error, exiting!");
		} catch (RatingException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			System.err.println("Fatal error, exiting!");
		}
	}
}
