package com.dalvik.utils.string;

public class MD5Hasher implements IStringHasher {

	private static MD5Hasher instance = null;

	protected MD5Hasher() {

	}

	public static MD5Hasher getInstance() {
		if (instance == null) {
			instance = new MD5Hasher();
		}
		return instance;
	}

	@Override
	public String getHashedString(String string) throws StringHashingException {

		String hashedString;
		try {
			hashedString = MD5Signature.getMD5Sum(string);
		} catch (StringHashingException e) {
			throw new StringHashingException("Unable to get md5 of string", e);
		}

		return hashedString;
	}
}
