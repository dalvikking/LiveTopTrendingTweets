package com.dalvik.webui.deployer;


import com.dalvik.tomcat.exception.WebAppDeployerException;
import com.dalvik.tomcat.impl.MicroServiceDeployer;

public class WebUIDriver {

	public static void main(String args[]) {
		try {
			MicroServiceDeployer.getInstance().deploy(8080, "/webui", "build/libs/webui.war");
			MicroServiceDeployer.getInstance().start();
		} catch (WebAppDeployerException e) {
			e.printStackTrace();
		}
	}
}
