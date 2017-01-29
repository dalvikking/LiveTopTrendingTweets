package com.dalvik.tomcat.impl;

import java.io.File;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.VersionLoggerListener;

import com.dalvik.tomcat.api.IWebAppDeployer;
import com.dalvik.tomcat.exception.WebAppDeployerException;

public class MicroServiceDeployer implements IWebAppDeployer {

	private static MicroServiceDeployer instance;
	Tomcat tomcat = null;

	public static MicroServiceDeployer getInstance() {
		if (instance == null) {
			instance = new MicroServiceDeployer();
		}
		return instance;
	}

	@Override
	public void deploy(Integer port, String appName, String webappDirPath) throws WebAppDeployerException {

		File catalinaHome = new File("." + appName); // folder must exist
		tomcat = new Tomcat();
		tomcat.setPort(port); // HTTP port
		tomcat.setBaseDir(catalinaHome.getAbsolutePath());
		tomcat.getServer().addLifecycleListener(new VersionLoggerListener());
		File war = new File(webappDirPath);
		try {
			tomcat.addWebapp(appName, war.getAbsolutePath());
			if (!tomcat.getServer().getState().equals(LifecycleState.STARTED)) {
			}
		} catch (ServletException e) {
			throw new WebAppDeployerException("Unable to Deploy WebService " + appName);
		}
	}

	public void start() {
		try {
			tomcat.start();
			tomcat.getServer().await();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
	}
}
