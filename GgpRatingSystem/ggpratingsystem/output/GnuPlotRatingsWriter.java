package ggpratingsystem.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class GnuPlotRatingsWriter extends PrintWriter implements RatingsWriter {
	public GnuPlotRatingsWriter(Writer out) {
		super(out);
	}

	public GnuPlotRatingsWriter(OutputStream out) {
		super(out);
	}

	public GnuPlotRatingsWriter(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	public GnuPlotRatingsWriter(File file) throws FileNotFoundException {
		super(file);
	}

	public GnuPlotRatingsWriter(Writer out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public GnuPlotRatingsWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public GnuPlotRatingsWriter(String fileName, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
	}

	public GnuPlotRatingsWriter(File file, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
	}

	public void printAll(double[][] doubles) throws IOException {
		for (double[] ds : doubles) {
			println(ds);
		}
	}

	/**
	 * Print the line to the output stream, but with a "#" before the line (comment)
	 */
	public void println(String[] strings) throws IOException {
		print("# ");
		printlnInner(strings);
	}

	public void println(double[] doubles) throws IOException {
		String[] strings = new String[doubles.length];
		
		for (int i = 0; i < doubles.length; i++) {
			strings[i] = String.valueOf(doubles[i]);			
		}
		printlnInner(strings);
	}

	private void printlnInner(String[] strings) throws IOException {
		for (String string : strings) {
			print(string);
			print(" ");
		}
		println();
	}
}
