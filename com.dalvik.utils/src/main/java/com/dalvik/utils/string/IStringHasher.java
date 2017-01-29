package com.dalvik.utils.string;

/**
 * This interface defines types of Hashing Algorithms and give function to find
 * hash of any string
 */
public interface IStringHasher {

	public enum HashingAlgo {
		MD5
	}

	/**
	 * Return hashed String for the input string passed
	 * 
	 * @param string
	 *            - input String
	 * @return hashed String
	 */
	public String getHashedString(String string) throws StringHashingException;

}
