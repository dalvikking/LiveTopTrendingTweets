package com.dalvik.twitter.service.servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dalvik.twitter.service.ITrendsManager;
import com.dalvik.twitter.service.TwitterTrendManager;
import com.dalvik.twitter.service.exception.TwitterServiceException;

import twitter4j.Trends;

@Path("/trends")
public class TrendService {

	/**
	 * Used to fetch all trends for location woeid Current Implementation only
	 * return for INDIA WOEID id hard coded
	 * 
	 * @param woeId
	 * @return -- Json of all Top Trends
	 */
	@GET
	@Path("/{woeid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerService(@PathParam("woeid") Integer woeId) {
		ITrendsManager trendsManager = TwitterTrendManager.getInstance();
		Trends topTrends = null;
		try {
			topTrends = trendsManager.getTrends(woeId);
		} catch (TwitterServiceException e) {
			e.printStackTrace();
		}
		Response response = Response.status(200).entity(topTrends).header("Access-Control-Allow-Origin", "*").build();
		return response;
	}
}
