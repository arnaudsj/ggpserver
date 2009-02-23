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

package ggpratingsystem.output;

import java.io.IOException;
import java.io.Writer;

import au.com.bytecode.opencsv.CSVWriter;

public class CSVRatingsWriter extends CSVWriter implements RatingsWriter {

	public CSVRatingsWriter(Writer writer) {
		super(writer);
	}

//	public CSVRatingsWriter(Writer writer, char separator) {
//		super(writer, separator);
//	}
//
//	public CSVRatingsWriter(Writer writer, char separator, char quotechar) {
//		super(writer, separator, quotechar);
//	}
//
//	public CSVRatingsWriter(Writer writer, char separator, char quotechar,
//			char escapechar) {
//		super(writer, separator, quotechar, escapechar);
//	}
//
//	public CSVRatingsWriter(Writer writer, char separator, char quotechar,
//			String lineEnd) {
//		super(writer, separator, quotechar, lineEnd);
//	}
//
//	public CSVRatingsWriter(Writer writer, char separator, char quotechar,
//			char escapechar, String lineEnd) {
//		super(writer, separator, quotechar, escapechar, lineEnd);
//	}

	public void printAll(double[][] doubles) throws IOException {
		for (double[] ds : doubles) {
			println(ds);
		}
	}

	public void println(String[] strings) throws IOException {
		super.writeNext(strings);
	}

	public void println(double[] doubles) throws IOException {
		String[] strings = new String[doubles.length];
		
		for (int i = 0; i < doubles.length; i++) {
			strings[i] = String.valueOf(doubles[i]);			
		}
		println(strings);
	}
}
