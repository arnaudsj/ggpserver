/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/


package tud.gamecontroller.traces;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class MatchTraceReaderWriterTest {

    public MatchTraceReaderWriterTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

	@Test
	public void testReadWrite() throws Exception {
		File datadir = new File("testdata/traces/");
		
		for (File inputFile : datadir.listFiles()) {
			MatchTraceReader reader = new MatchTraceReader();
			MatchTrace trace = reader.read(inputFile);
			
			File outputFile = File.createTempFile("trace", ".xml");
			
			MatchTraceWriter writer = new MatchTraceWriter();
			writer.write(trace, outputFile);
			
			assertTrue("read/write did not produce identical output: " + inputFile, equalFiles(inputFile, outputFile));
			assertTrue("could not delete output file", outputFile.delete());
		}
	}
	
	public static boolean equalFiles(File file1, File file2) throws IOException {
		BufferedReader reader1 = null, reader2 = null;
		try {
			reader1 = new BufferedReader(new FileReader(file1));
			reader2 = new BufferedReader(new FileReader(file2));
			
			String line1, line2;
			
			while (true) {				
				line1 = reader1.readLine();
				line2 = reader2.readLine();
				
				if (line1 == null && line2 == null) {
					return true;
				} else if (line1 == null || !line1.equals(line2)) {
					return false;
				}
			}
		} finally {
			if (reader1 != null) {
				reader1.close();
			}
			if (reader2 != null) {
				reader2.close();
			}
		}
	}
}