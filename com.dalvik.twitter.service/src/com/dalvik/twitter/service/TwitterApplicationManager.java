package com.dalvik.twitter.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TwitterApplicationManager implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// stop trend fetching timer service here
		System.out.println("Stop Server");
		TwitterTrendManager.getInstance().stopTrendTask();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// start trend fetching timer service here
		System.out.println("Start Server");
		TwitterTrendManager.getInstance().startTrendTask();
	}

}
