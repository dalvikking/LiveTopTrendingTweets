package com.dalvik.twitter.service.websocket;

import java.util.Timer;

import twitter4j.StatusListener;
import twitter4j.Trend;

public abstract class StatusStreamListener implements StatusListener {

	Trend trend;
	Timer timer;

	public Trend getTrend() {
		return trend;
	}

	public void stopConsumerTimer() {
		if (timer != null) {
			System.out.println("Consumer Timer is stoped for Trend : " + trend.getName());
			timer.cancel();
		}
	}

}
