package com.dalvik.tomcat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigFileReader {

	public static Map<String, String> getProperties(String filePath) {

		Properties prop = new Properties();
		InputStream input = null;

		try {
			if (filePath != null) {
				input = new FileInputStream(filePath);
				prop.load(input);
				Map<String, String> propMap = new HashMap<String, String>();
				for (final String name : prop.stringPropertyNames())
					propMap.put(name, prop.getProperty(name));
				return propMap;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
