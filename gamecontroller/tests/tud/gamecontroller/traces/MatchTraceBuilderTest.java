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
import static tud.gamecontroller.traces.MatchTraceReaderWriterTest.equalFiles;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import tud.gamecontroller.GDLVersion;

public class MatchTraceBuilderTest {

    public MatchTraceBuilderTest() {
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
	public void testBuilder() throws IOException, TransformerFactoryConfigurationError, TransformerFactoryConfigurationError, SAXException {
		File inputDir = new File("testdata/traces/");
		File gamesDir = new File("testdata/games/");
		
		for (File inputFile : inputDir.listFiles()) {
			String gameName = inputFile.getName().substring(0, inputFile.getName().indexOf("."));
			
			File gameFile = new File(gamesDir, gameName + ".lisp");
			File outputFile = File.createTempFile(gameName, ".trace.xml");
			
			RetraceGameControllerRunner.retrace(inputFile, outputFile, gameFile, GDLVersion.v1);
			
			assertTrue("retrace did not produce identical output: " + inputFile, equalFiles(inputFile, outputFile));
			assertTrue("could not delete output file", outputFile.delete());

		}
	}

}