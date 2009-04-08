package tud.ggpserver.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This does exactly the same as org.apache.catalina.realm.RealmBase.Digest(),
 * but without the security risk of allowing the web app access to catalina.jar .
 * 
 * The source code of this class was taken from here:
 * http://www.mail-archive.com/tomcat-user@jakarta.apache.org/msg107847.html
 * 
 * @author Chris Schultz
 */
public class Digester {
	private static final char[] hex = "0123456789abcdef".toCharArray();

	/**
	 * Returns a message digest of the specified string using the specified
	 * digest algorithm.
	 * <p>
	 * 
	 * @param cleartext
	 *            The cleartext string to be digested.
	 * @param algorithm
	 *            The digest algorithm to use (try "<code>MD5</code>" or "<code>SHA-1</code>".
	 * 
	 * @return A String of hex characters representing the message digest of
	 *         the given cleartext string.
	 * @throws NoSuchAlgorithmException
	 */
	public static String digest(String cleartext, String algorithm)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);

		md.update(cleartext.getBytes()); // Might want to use a specific char encoding?

		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer(2 * digest.length);

		for (int i = 0; i < digest.length; ++i) {
			int high = (digest[i] & 0xf0) >> 4;
			int low = (digest[i] & 0x0f);

			sb.append(hex[high]);
			sb.append(hex[low]);
		}

		return (sb.toString());
	}
}
