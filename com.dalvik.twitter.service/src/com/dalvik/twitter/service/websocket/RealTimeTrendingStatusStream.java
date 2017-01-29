package com.dalvik.twitter.service.websocket;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import com.dalvik.twitter.service.exception.TwitterServiceException;

import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Trend;

/**
 * Using Generics This class will maintain the stream of status for Trend T. T
 * is the Trend. BlockingQueue will have some fixed configurabel size
 * 
 * @author varun
 *
 * @param <T>
 */
public class RealTimeTrendingStatusStream<T extends Trend> extends StatusStreamListener {

	// thread safe queue
	private LinkedBlockingQueue<Status> trendingStatusQueue;
	private static final int maxCapacity = 100;
	private static final int STREAM_UPDATE_INTERVAL = 5 * 1000;
	private UserSessionHandler sessionHandler = null;

	public RealTimeTrendingStatusStream(Trend t) {
		this.trend = (T) t;
		sessionHandler = UserSessionHandler.getInstance();
		trendingStatusQueue = new LinkedBlockingQueue<>(maxCapacity);
		startConsumerTask();
	}

	public T getTrend() {
		return (T) trend;
	}

	@Override
	public void onException(Exception ex) {
		stopConsumerTimer();
		System.err.println("Error ocurred while listening to Trend Stream : " + trend.getName());
	}

	/**
	 * Producer of status. Adds new status into the queue.
	 */
	@Override
	public void onStatus(Status status) {
		// Add status to blocking queue
		// filter status on the basis of Trend
		if (status != null && status.getText().toLowerCase().contains(trend.getName().toLowerCase())) {
			try {
				// if max limit reached, remove oldest item
				if (trendingStatusQueue.remainingCapacity() == 0) {
					trendingStatusQueue.take();
				}
				trendingStatusQueue.put(status);
			} catch (InterruptedException e) {
				System.out.println("Error occured while adding Status to Queue");
			}
		}
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
	}

	@Override
	public void onStallWarning(StallWarning warning) {
	}

	/**
	 * Consumer task, which poll status from queue and sends to
	 * UserSessionHandler
	 * 
	 * @author varun
	 *
	 */
	private class StatusConsumerTask extends TimerTask {

		@Override
		public void run() {
			if (trendingStatusQueue != null && trendingStatusQueue.size() > 0) {
				System.out.println("Trend : " + trend.getName() + "--->" + trendingStatusQueue.size());
				try {
					Status latestStatus = trendingStatusQueue.peek();
					boolean flag = sessionHandler.onStatusStreamUpdate(trend, latestStatus);
					// if status is broadcasted remove from queue else cache it.
					if (flag) {
						trendingStatusQueue.take();
					}
				} catch (TwitterServiceException e) {
					System.out.println("Error Occured while Polling latest Status");
				} catch (InterruptedException e) {
					System.out.println("Error Occured while Polling latest Status");
				}
			}
		}
	}

	/**
	 * Stream will get updated after every 5 sec
	 */
	private void startConsumerTask() {
		if (timer == null) {
			timer = new Timer();
			System.out.println("Consumer timer started for Trend : " + trend.getName());
			timer.schedule(new StatusConsumerTask(), 0, STREAM_UPDATE_INTERVAL);
		}
	}

	/**
	 * {@link RealTimeTrendingStatusStream} will be equal if there Trend are
	 * also same.
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof RealTimeTrendingStatusStream<?>) {
			RealTimeTrendingStatusStream<Trend> obj1 = (RealTimeTrendingStatusStream<Trend>) obj;
			Trend trend = obj1.getTrend();
			if (this.trend.equals(trend)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

}
