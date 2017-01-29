package com.dalvik.twitter.service.websocket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.dalvik.twitter.service.CustomTrend;

import twitter4j.JSONException;
import twitter4j.JSONObject;

@ServerEndpoint("/liveTrendingStatus")
public class StatusWebSocket {

	private final UserSessionHandler sessionHandler = UserSessionHandler.getInstance();

	@OnOpen
	public void onOpen(Session session) {
		sessionHandler.addSession(session);
		System.out.println("New Session Added To Handler");
	}

	@OnMessage
	public void onMessage(String message, Session session) {

		if (message == null || message.length() == 0) {
			return;
		}

		try {
			JSONObject trendJson = new JSONObject(message);
			CustomTrend trend = new CustomTrend(trendJson.getString("name"), trendJson.getString("url"),
					trendJson.getString("query"));
			int woeId = trendJson.getInt("woeId");
			sessionHandler.registerTrendStatusListener(woeId, trend, session);
		} catch (JSONException e) {
			try {
				session.getBasicRemote().sendText("Error occured while parsing Trend request json.");
			} catch (IOException e1) {
				System.out.println("Error occured while sending data to client");
			}
			System.out.println("Error occured while parsing Trend request json.");
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		sessionHandler.removeSession(session);
		System.out.println("Session Closed and removed from the Handler");
	}

}
