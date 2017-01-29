package com.dalvik.twitter.service;

import twitter4j.Trend;

public class CustomTrend {

	private final String name;
	private String url = null;
	private String query = null;

	public CustomTrend(String name, String url, String query) {
		this.name = name;
		this.url = url;
		this.query = query;
	}

	public String getName() {
		return name;
	}

	public String getURL() {
		return url;
	}

	public String getQuery() {
		return query;
	}

	public boolean equals(Trend trend) {
		if (trend == null) {
			return false;
		} else {
			if (trend.getName() != null && trend.getName().equals(this.name) && trend.getQuery() != null
					&& trend.getQuery().equals(this.query) && trend.getURL() != null
					&& trend.getURL().equals(this.url)) {
				return true;
			} else {
				return false;
			}
		}
	}
}
