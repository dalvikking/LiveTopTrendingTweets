package com.dalvik.twitter.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import com.dalvik.twitter.service.exception.TwitterServiceException;
import com.dalvik.twitter.service.listeners.TrendUpdateListener;
import com.dalvik.twitter.service.timer.TopTrendsTimerTask;

import twitter4j.Trends;

/**
 * Manages and fetch all Top Trends
 * 
 * @author varun
 *
 */
public class TwitterTrendManager implements ITrendsManager, TrendUpdateListener {

	private static TwitterTrendManager instance = null;
	private TopTrendsTimerTask trendTimerTask;
	private Map<Integer, Trends> topTrendsLocationWise;
	private Timer trendTimer;
	private static final int FETCH_INTERVAL =  60 * 1000; // 15 min
	private static final int INDIA_WOEID = 23424848;

	public static TwitterTrendManager getInstance() {
		if (instance == null) {
			synchronized (TwitterTrendManager.class) {
				instance = new TwitterTrendManager();
			}
		}
		return instance;
	}

	private TwitterTrendManager() {
		topTrendsLocationWise = new HashMap<>();
		trendTimer = new Timer();
	}

	public void startTrendTask() {
		trendTimerTask = new TopTrendsTimerTask(INDIA_WOEID);
		trendTimer.schedule(trendTimerTask, 0, FETCH_INTERVAL);
	}

	public void stopTrendTask() {
		trendTimer.cancel();
	}

	/**
	 * Start Timer for requested woeid if not exist For now we have only wwoeid
	 * for India only -- But it can be extended easily
	 */
	@Override
	public Trends getTrends(Integer woeId) throws TwitterServiceException {
		synchronized (topTrendsLocationWise) {
			if (topTrendsLocationWise.containsKey(woeId)) {
				return topTrendsLocationWise.get(woeId);
			} else {
				return null;
			}
		}
	}

	/**
	 * Timer Task will noitfy on updating trends
	 */
	@Override
	public void onTrendUpdate(int woeid, Trends trends) throws TwitterServiceException {

		synchronized (topTrendsLocationWise) {
			if (!topTrendsLocationWise.containsKey(woeid)) {
				topTrendsLocationWise.put(woeid, null);
			}
			topTrendsLocationWise.put(woeid, trends);
		}
	}
}
