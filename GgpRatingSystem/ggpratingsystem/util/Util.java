package ggpratingsystem.util;

import java.io.File;

public class Util {

	/**
	 * @return
	 */
	public static File getDataDir() {
		String userdir = System.getProperty("user.dir");
		
		if (!userdir.endsWith(File.separator)) {
			userdir += File.separator;
		}
		
		return new File(userdir + "data");
	}
}
