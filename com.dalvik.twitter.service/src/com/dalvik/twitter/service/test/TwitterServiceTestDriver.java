package com.dalvik.twitter.service.test;

import com.dalvik.tomcat.exception.WebAppDeployerException;
import com.dalvik.tomcat.impl.MicroServiceDeployer;

public class TwitterServiceTestDriver {

	public static void main(String args[]) {
		try {
			MicroServiceDeployer.getInstance().deploy(8888, "/twitter", "build/libs/twitter.war");
			MicroServiceDeployer.getInstance().start();
		} catch (WebAppDeployerException e) {
			e.printStackTrace();
		}
	}
}
