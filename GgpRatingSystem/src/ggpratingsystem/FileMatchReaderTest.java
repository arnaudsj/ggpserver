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

import ggpratingsystem.util.Util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class FileMatchReaderTest extends TestCase {

	/*
	 * Test method for 'ggpratingsystem.MatchReader.readMatches(String)'
	 */
	public void testReadMatches() throws IOException {
		MatchReader matchReader = new FileMatchReader(new File(Util.getDataDir(), "competition2007" + File.separator + "xml"));
		
		int numMatchSets = 0;
		while (matchReader.hasNext()) {
			numMatchSets++;
			matchReader.readMatchSet();
		}
		assertEquals(47, numMatchSets);
	}
}
