package com.dalvik.twitter.service.timer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import com.dalvik.twitter.service.TwitterTrendManager;
import com.dalvik.twitter.service.exception.TwitterServiceException;
import com.dalvik.twitter.service.listeners.TrendUpdateListener;
import com.dalvik.twitter.service.websocket.RealTimeTwitterStreamManager;
import com.rits.cloning.Cloner;

import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TopTrendsTimerTask extends TimerTask {

	private Map<String, String> authProps;
	private int woeId;
	private Set<TrendUpdateListener> listeners;

	private static final String consumerKey = "consumerKey";
	private static final String consumerSecret = "consumerSecret";
	private static final String accessToken = "accessToken-3E24VWIPqVgmhJ5mD5Jrn2uAK52a0f2";
	private static final String accessTokenSecret = "accessTokenSecret";

	public TopTrendsTimerTask(int woeid) {
		this.woeId = woeid;
		authProps = new HashMap<>();
		listeners = new LinkedHashSet<>();
		loadAuthProps();
		registerListener();
	}

	private void registerListener() {
		listeners.add(TwitterTrendManager.getInstance());
		listeners.add(RealTimeTwitterStreamManager.getInstance());
	}

	/**
	 * Hard Coded values are used.But it can be loaded from file or DB.
	 */
	private void loadAuthProps() {
		authProps.put(consumerKey, "SEdTd1XueSUSxueDrEzefCCbP");
		authProps.put(consumerSecret, "aa1unLqL5x0Tkrp1OHgnzBpBVnjffuyvoYe5xrO0YZEwO58sYU");
		authProps.put(accessToken, "741303431957123073-3E24VWIPqVgmhJ5mD5Jrn2uAK52a0f2");
		authProps.put(accessTokenSecret, "sZFcen84NVPBzWwPorqdVlR28bcRZUJB3ZkQudKEs1tRU");
	}

	@Override
	public void run() {

		TwitterFactory twitterFactory = new TwitterFactory();
		Twitter twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(authProps.get(consumerKey), authProps.get(consumerSecret));
		twitter.setOAuthAccessToken(new AccessToken(authProps.get(accessToken), authProps.get(accessTokenSecret)));
		try {
			Trends trends = twitter.getPlaceTrends(woeId);
			if (trends.getTrends().length > 0) {
				notifyListeners(makeTrendsClone(trends));
			}
		} catch (TwitterException e) {
			System.out.println("Exception occcured in TrendTimer while fetching top trend -- " + e.getMessage());
			// TODO : need to handle , start timer again if exception occurs
		} catch (TwitterServiceException e) {
			System.out.println("Exception occcured in TrendTimer while fetching top trend -- " + e.getMessage());
			// TODO : need to handle , start timer again if exception occurs
		}
	}

	private void notifyListeners(Trends clonedTrend) throws TwitterServiceException {

		for (TrendUpdateListener listener : listeners) {
			listener.onTrendUpdate(woeId, clonedTrend);
		}
	}

	/**
	 * Keep actual object locally, Return copy.
	 * 
	 * @param trends
	 * @return
	 */
	private Trends makeTrendsClone(Trends trends) {
		Cloner trendCloner = new Cloner();
		Trends clonedTrends = trendCloner.deepClone(trends);
		return clonedTrends;
	}

}
