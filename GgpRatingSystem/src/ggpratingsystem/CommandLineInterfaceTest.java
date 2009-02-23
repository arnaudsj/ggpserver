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

import java.io.File;

import ggpratingsystem.util.Util;

import com.martiansoftware.jsap.CommandLineTokenizer;
import com.martiansoftware.jsap.JSAPException;

import junit.framework.TestCase;

public class CommandLineInterfaceTest extends TestCase {

	
	public void testMain() throws Exception {
		System.err.println("\n\n\n\n\n\n\n\n\n\n");
		

		String cmdLine = 
			" --input-dir " + (new File(Util.getDataDir(), "competition2007" + File.separator + "xml")).toString()
			+ " --output-dir " + "/tmp/ggp-rating-system/"
			+ " --dynamic-linear-regression-rating 60" 	// (60 is a good number here, because we have 44 MatchSets and 60 > 44)
			+ " --constant-linear-regression-rating 1.0"
			+ " --direct-scores-rating"
			+ " --csv-output"
			+ " --debug-level ALL";
		String[] args = CommandLineTokenizer.tokenize(cmdLine);
		
		CommandLineInterface.main(args);
	}

	public void testMainEmptyArgs() throws Exception {
		System.err.println("\n\n\n\n\n\n\n\n\n\n");

		String cmdLine = "";
		String[] args = CommandLineTokenizer.tokenize(cmdLine);
		try {
			CommandLineInterface.main(args);
		} catch (JSAPException e) {
			return;	// this is expected
		}
		fail("should throw an exception!");
	}

	public void testMainHelp() throws Exception {
		System.err.println("\n\n\n\n\n\n\n\n\n\n");

		String cmdLine = " --help";
		String[] args = CommandLineTokenizer.tokenize(cmdLine);
		try {
			CommandLineInterface.main(args);
		} catch (JSAPException e) {
			return;	// this is expected
		}
		fail("should throw an exception!");
	}

	public void testMainPrevious() throws Exception {
		System.err.println("\n\n\n\n\n\n\n\n\n\n");

		String cmdLine = 
			" --input-dir " + (new File(Util.getDataDir(), "competition2008" + File.separator + "xml")).toString()
			+ " --output-dir " + "/tmp/ggp-rating-system/"
			+ " --previous " + (new File(Util.getDataDir(), "competition2007" + File.separator + "output" + File.separator + "constant_linear_regression_1.0.csv")).toString()
			+ " --constant-linear-regression-rating 1.0"
			+ " --csv-output"
			+ " --html-output";
		String[] args = CommandLineTokenizer.tokenize(cmdLine);


		CommandLineInterface.main(args);
	}	

	public void testMain2006() throws Exception {
		System.err.println("\n\n\n\n\n\n\n\n\n\n");

		String cmdLine = 
			" --input-dir " + (new File(Util.getDataDir(), "competition2006" + File.separator + "xml")).toString()
			+ " --output-dir " + "/tmp/ggp-rating-system/"
			+ " --constant-linear-regression-rating 1.0"
			+ " --csv-output"
			+ " --html-output";
		String[] args = CommandLineTokenizer.tokenize(cmdLine);


		CommandLineInterface.main(args);
	}	

	public void testMain2005() throws Exception {
		System.err.println("\n\n\n\n\n\n\n\n\n\n");

		String cmdLine = 
			" --input-dir " + (new File(Util.getDataDir(), "competition2005" + File.separator + "xml")).toString()
			+ " --output-dir " + "/tmp/ggp-rating-system/"
			+ " --constant-linear-regression-rating 1.0"
			+ " --csv-output"
			+ " --html-output";
		String[] args = CommandLineTokenizer.tokenize(cmdLine);


		CommandLineInterface.main(args);
	}		
}
