package com.dalvik.twitter.service.listeners;

import com.dalvik.twitter.service.exception.TwitterServiceException;

import twitter4j.Status;
import twitter4j.Trend;

/**
 * SessionHandler will listener for updation in status stream and send pushes to
 * all connected clients
 * 
 * @author varun
 */
public interface StatusStreamUpdateListener {

	/**
	 * Returns true if status is broadcasted to all clients.
	 * Else return false.
	 * @param trend
	 * @param status
	 * @return
	 * @throws TwitterServiceException
	 */
	public boolean onStatusStreamUpdate(Trend trend, Status status) throws TwitterServiceException;

}
