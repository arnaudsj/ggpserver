package ggpratingsystem.output;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public interface RatingsWriter extends Closeable, Flushable {
	public abstract void println(String[] strings) throws IOException;

	public abstract void println(double[] doubles) throws IOException;

	public void printAll(double[][] doubles) throws IOException;
}
