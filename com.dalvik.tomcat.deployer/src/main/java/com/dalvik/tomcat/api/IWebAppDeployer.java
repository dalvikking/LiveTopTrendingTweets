package com.dalvik.tomcat.api;

import com.dalvik.tomcat.exception.WebAppDeployerException;

public interface IWebAppDeployer {

	public void deploy(Integer port, String appName, String warFilePath) throws WebAppDeployerException;

}
