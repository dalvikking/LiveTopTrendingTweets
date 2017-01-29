package com.dalvik.twitter.service.listeners;

import com.dalvik.twitter.service.exception.TwitterServiceException;

import twitter4j.Trends;

public interface TrendUpdateListener {

	public void onTrendUpdate(int woeid, Trends trends) throws TwitterServiceException;

}
