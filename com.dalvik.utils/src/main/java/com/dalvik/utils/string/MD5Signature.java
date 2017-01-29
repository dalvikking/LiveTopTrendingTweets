package com.dalvik.utils.string;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class MD5Signature {

	private static MessageDigest md = null;
	private static final String ENCODING = "UTF-8";
	private static final String ALGORITHM = "MD5";

	public static String getMD5Sum(String str) throws StringHashingException {

		try {

			return getMD5Sum(str.getBytes(ENCODING));

		} catch (UnsupportedEncodingException e) {
			throw new StringHashingException(e);
		}
	}

	private static String getMD5Sum(byte[] input) throws StringHashingException {

		try {

			if (md == null) {
				md = MessageDigest.getInstance(ALGORITHM);
			}

			md.reset();
			byte[] digest;

			digest = md.digest(input);

			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < digest.length; i++) {
				hexString.append(hexDigit(digest[i]));
			}

			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			throw new StringHashingException(e);
		}
	}

	private static String hexDigit(byte x) {

		StringBuffer sb = new StringBuffer();
		char c;

		// First nibble
		c = (char) ((x >> 4) & 0xf);
		if (c > 9) {
			c = (char) ((c - 10) + 'a');
		} else {
			c = (char) (c + '0');
		}

		sb.append(c);

		// Second nibble
		c = (char) (x & 0xf);
		if (c > 9) {
			c = (char) ((c - 10) + 'a');
		} else {
			c = (char) (c + '0');
		}

		sb.append(c);

		return sb.toString();
	}
}
