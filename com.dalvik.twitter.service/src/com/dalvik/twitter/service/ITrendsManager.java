package com.dalvik.twitter.service;

import com.dalvik.twitter.service.exception.TwitterServiceException;

import twitter4j.Trends;

/**
 * Servlet End Point will use this interface to get trends for given woeid
 * 
 * @author varun
 *
 */
public interface ITrendsManager {

	public Trends getTrends(Integer woeId) throws TwitterServiceException;

}
