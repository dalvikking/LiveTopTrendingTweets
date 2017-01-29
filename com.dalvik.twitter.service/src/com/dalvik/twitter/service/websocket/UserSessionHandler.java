package com.dalvik.twitter.service.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import com.dalvik.twitter.service.CustomTrend;
import com.dalvik.twitter.service.ITrendsManager;
import com.dalvik.twitter.service.TwitterTrendManager;
import com.dalvik.twitter.service.exception.TwitterServiceException;
import com.dalvik.twitter.service.listeners.StatusStreamUpdateListener;
import com.dalvik.utils.json.JSONHelper;
import com.dalvik.utils.json.SerializationException;

import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;

/**
 * Maintains all active Session of connected clients and send data to all
 * clients on receiving notification
 * 
 * @author varun
 */
public class UserSessionHandler implements StatusStreamUpdateListener {

	private Set<Session> sessions = null;
	private Map<Trend, Set<Session>> trendVsSessionsSet;
	private Map<Session, Trend> sessionVsTrend;
	private static UserSessionHandler instance = null;

	/**
	 * Singleton Impl to ensure only one SessionHandler
	 * 
	 * @return
	 */
	public static UserSessionHandler getInstance() {
		if (instance == null) {
			synchronized (UserSessionHandler.class) {
				instance = new UserSessionHandler();
			}
		}
		return instance;
	}

	private UserSessionHandler() {
		sessions = new HashSet<>();
		trendVsSessionsSet = new HashMap<>();
		sessionVsTrend = new HashMap<>();
	}

	/**
	 * Add session of new connected client
	 * 
	 * @param session
	 */
	public void addSession(Session session) {
		synchronized (sessions) {
			sessions.add(session);
			System.out.println("Total active Session Count : " + sessions.size());
		}
	}

	/**
	 * removes session of disconnected client
	 * 
	 * @param session
	 */
	public void removeSession(Session session) {
		synchronized (sessions) {
			try {
				session.close();
			} catch (IOException e) {
				System.out.println("Error occured while closing session.");
			} finally {
				// remove session any way and free up resources
				unregisterTrendStatusListener(session);
				sessions.remove(session);
				System.out.println("Total active Session Count : " + sessions.size());
			}
		}
	}

	/**
	 * Broadcast status to all connected clients.
	 */
	@Override
	public boolean onStatusStreamUpdate(Trend trend, Status status) throws TwitterServiceException {

		synchronized (trendVsSessionsSet) {
			Set<Session> liveSessionForStream = trendVsSessionsSet.get(trend);
			if (liveSessionForStream != null && liveSessionForStream.size() > 0) {
				System.out.println("Sending Tweet : " + trend.getName());
				for (Session session : liveSessionForStream) {
					try {
						String streamMessage = JSONHelper.convertBeanToJson(status, true);
						session.getBasicRemote().sendText(streamMessage);
					} catch (SerializationException e) {
						System.out.println("Error occured while parsing Status Stream");
					} catch (IOException e) {
						System.out.println("Error occured while sending status stream to client");
					}
				}
				return true;
			}
			return false;
		}
	}

	public void registerTrendStatusListener(int woeId, CustomTrend trend, Session session) {

		Trend actualTrendObject = getTrend(woeId, trend);

		if (actualTrendObject != null) {

			synchronized (sessionVsTrend) {
				// check if session have existing Trending Status Stream or not
				if (!sessionVsTrend.containsKey(session)) {
					sessionVsTrend.put(session, actualTrendObject);
				} else {
					// if session have Trend Stream then
					// remove old Trend Entry. i.e., remove Session from
					// trendVsSessionSet
					Trend oldTrend = sessionVsTrend.get(session);
					sessionVsTrend.put(session, actualTrendObject);
					Set<Session> sessionSet = trendVsSessionsSet.get(oldTrend);
					if (sessionSet.contains(session)) {
						sessionSet.remove(session);
					}
				}
			}

			synchronized (trendVsSessionsSet) {
				// update Session in trendVsSessionSet
				if (trendVsSessionsSet.get(actualTrendObject) == null) {
					trendVsSessionsSet.put(actualTrendObject, new HashSet<Session>());
				}
				Set<Session> sessionSet = trendVsSessionsSet.get(actualTrendObject);
				sessionSet.add(session);
			}

		} else {
			System.out.println("Unable to register the listener");
		}
	}

	/**
	 * Clean up and freeup resurces when session is closed
	 * 
	 * @param session
	 */
	private void unregisterTrendStatusListener(Session session) {

		if (sessionVsTrend != null) {
			synchronized (sessionVsTrend) {
				// remove session from active session VsTrendMap
				Trend trend = sessionVsTrend.get(session);
				sessionVsTrend.remove(session);
				// remove session from Trend
				if (trendVsSessionsSet.containsKey(trend) && trendVsSessionsSet.get(trend) != null) {
					trendVsSessionsSet.get(trend).remove(session);
				}
			}
		}
	}

	/**
	 * returns the actual trend object stored in JVM runtime heap
	 * 
	 * @param woeid
	 * @param customTrend
	 * @return
	 */
	private Trend getTrend(int woeid, CustomTrend customTrend) {
		ITrendsManager manager = TwitterTrendManager.getInstance();
		try {
			Trends trends = manager.getTrends(woeid);

			for (Trend trend : trends.getTrends()) {
				if (customTrend.equals(trend)) {
					return trend;
				}
			}
			return null;
		} catch (TwitterServiceException e) {
			return null;
		}
	}

}
