package com.dalvik.tomcat;

import java.io.IOException;
import java.util.Map;

import com.dalvik.tomcat.exception.WebAppDeployerException;
import com.dalvik.tomcat.impl.MicroServiceDeployer;

public class TomcatRunnerDeployer {

	private static final String fileName = "config.properties";
	private static final String path = "service.war.path";
	private static final String address = "service.host.address";
	private static final String port = "service.host.port";

	public static void main(String[] args) throws IOException {

		try {
			Map<String, String> serverProps = ConfigFileReader.getProperties(fileName);
			System.out.println(serverProps.toString());
			MicroServiceDeployer.getInstance().deploy(Integer.valueOf(serverProps.get(port)),
					"/" + serverProps.get(address), serverProps.get(path));
			MicroServiceDeployer.getInstance().start();
		} catch (WebAppDeployerException e) {
			System.out.println("Error occured while deployong Service");
		}
	}
}
