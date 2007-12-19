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
