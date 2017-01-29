package com.dalvik.twitter.service.websocket;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.dalvik.twitter.service.exception.TwitterServiceException;
import com.dalvik.twitter.service.listeners.TrendUpdateListener;

import twitter4j.FilterQuery;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

/**
 * Uses twitter streaming api for fetching Stream of statuses. Notify all
 * listeners. Singleton Implementation
 * 
 * @author varun
 *
 */
public class RealTimeTwitterStreamManager implements TrendUpdateListener {

	private static RealTimeTwitterStreamManager instance = null;
	private TwitterStream twitterStream;
	private Set<StatusStreamListener> registeredListeners;
	private static AtomicBoolean isStreamActive;

	// hard coded value for testing pupose only
	private static final String consumerKey = "SEdTd1XueSUSxueDrEzefCCbP";
	private static final String consumerSecret = "aa1unLqL5x0Tkrp1OHgnzBpBVnjffuyvoYe5xrO0YZEwO58sYU";
	private static final String accessToken = "741303431957123073-3E24VWIPqVgmhJ5mD5Jrn2uAK52a0f2";
	private static final String accessTokenSecret = "sZFcen84NVPBzWwPorqdVlR28bcRZUJB3ZkQudKEs1tRU";

	/**
	 * Singleton
	 * 
	 * @return
	 */
	public static RealTimeTwitterStreamManager getInstance() {
		if (instance == null) {
			synchronized (RealTimeTwitterStreamManager.class) {
				instance = new RealTimeTwitterStreamManager();
			}
		}
		return instance;
	}

	private RealTimeTwitterStreamManager() {
		isStreamActive = new AtomicBoolean(false);
		registeredListeners = new LinkedHashSet<>();
	}

	/**
	 * makes new connection with twitter Stream EndPoint
	 */
	private void initTwitterStream() {
		twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
		twitterStream.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
	}

	/**
	 * registers all Trend Listeners. Currently we are not using woeid.
	 * Hardcoded for India only.But it can be extended easily.
	 * 
	 * @param trends
	 * @param woeid
	 */
	private void registeredAllListeners(int woeid, Trends trends) {
		for (Trend trend : trends.getTrends()) {
			RealTimeTrendingStatusStream<Trend> streamListener = new RealTimeTrendingStatusStream<>(trend);
			registerStreamListener(streamListener);
		}
	}

	/**
	 * Always taking lock to ensure thread safety. Adding listener to local Set
	 * 
	 * @param listener
	 */
	private void registerStreamListener(StatusStreamListener listener) {
		twitterStream.addListener(listener);
		synchronized (registeredListeners) {
			if (!registeredListeners.contains(listener)) {
				registeredListeners.add(listener);
				System.out.println("Trend Listener Registered : " + listener.getTrend().getName());
			}
		}
	}

	private void startStreamFilterTrack() {
		isStreamActive.set(false);
		String track[] = new String[registeredListeners.size()];
		int idx = 0;
		for (StatusStreamListener listener : registeredListeners) {
			track[idx++] = listener.getTrend().getName();
		}
		twitterStream.sample();
		double indiaBox[][] = { { 10.272397, 72.719572 }, { 30.470533, 87.697253 } };
		twitterStream.filter(new FilterQuery().track(track).locations(indiaBox));
		isStreamActive.set(true);
		System.out.println("Stream Manager Updated => " + Arrays.deepToString(track));
	}

	/**
	 * Stops fetching live tweets stream
	 */
	private void stopStreaming() {
		if (twitterStream != null) {
			twitterStream.cleanUp();
			twitterStream = null;
			isStreamActive.set(false);
		}
	}

	/**
	 * Whenever trend get updated, reset the stream filter and stop consumer
	 * task again for new trending tweets. It makes a new connection after every
	 * 15 min configured time. Since we have rate limit on Stream connection
	 * api.
	 */
	@Override
	public void onTrendUpdate(int woeid, Trends trends) throws TwitterServiceException {

		// reset old stream, cleanup listeners, stop listeners consumer task
		// before cleaning
		if (registeredListeners != null && registeredListeners.size() > 0) {
			for (StatusStreamListener listener : registeredListeners) {
				listener.stopConsumerTimer();
			}
		}
		registeredListeners.clear();
		stopStreaming();
		// init new stream object
		initTwitterStream();
		// register new listeners
		registeredAllListeners(woeid, trends);
		// update Stream Filter
		startStreamFilterTrack();

	}

}
