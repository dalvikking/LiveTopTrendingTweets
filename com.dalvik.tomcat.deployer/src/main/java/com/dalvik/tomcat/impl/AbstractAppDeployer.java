package com.dalvik.tomcat.impl;

import org.apache.catalina.startup.Tomcat;

import com.dalvik.tomcat.api.IWebAppDeployer;

public abstract class AbstractAppDeployer implements IWebAppDeployer {

	protected Tomcat tomcat;
	protected Integer port;
	protected String appName;

}
