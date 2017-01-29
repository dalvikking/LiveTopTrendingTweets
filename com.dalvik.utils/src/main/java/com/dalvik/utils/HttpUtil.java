package com.dalvik.utils;

import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class HttpUtil {

	public static final String authHeader = "auth-header";
	public static final String username = "username";
	public static final String password = "password";
	public static final String issuer = "issuer";
	public static final String subject = "subject";

	public static Map<String, String> getParams(HttpServletRequest req) {
		Enumeration<String> paramNames = req.getParameterNames();
		if (paramNames == null) {
			return null;
		}
		Map<String, String> params = new HashMap<>();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			params.put(paramName, req.getParameter(paramName));
		}
		return params;
	}

	public static Map<String, String> getHeaders(HttpServletRequest req) {
		Enumeration<String> headerNames = req.getHeaderNames();
		if (headerNames == null) {
			return null;
		}
		Map<String, String> headers = new HashMap<>();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			headers.put(headerName, req.getHeader(headerName));
		}
		return headers;
	}

	public static String getResponseBody(HttpServletRequest req) {
		try {
			StringBuilder buffer = new StringBuilder();
			BufferedReader reader = req.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			return buffer.toString();
		} catch (Exception e) {
			System.out.println("String parsing Error");
		}
		return null;
	}
}
