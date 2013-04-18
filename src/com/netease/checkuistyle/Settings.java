package com.netease.checkuistyle;

import java.io.FileInputStream;
import java.util.Properties;

public class Settings {
	public static Properties prop = getProperties();

	public static int ScreenShotType = Integer.parseInt(prop.getProperty("ScreenShotType", "1"));

	public static String SampleImagePath = prop.getProperty("SampleImagePath", "res/samples/");

	public static String ContrastImagePath = prop.getProperty("ContrastImagePath", "images/");

	public static double MaxColorThreshold = Double.parseDouble(prop.getProperty("MaxColorThreshold", "0"));

	public static int BrowserCoreType = Integer.parseInt(prop.getProperty("BrowserCoreType", "2"));
	
	public static String getProperty(String Property) {
		return prop.getProperty(Property);
	}

	public static Properties getProperties() {
		Properties prop = new Properties();
		try {
			FileInputStream file = new FileInputStream("type.properties");
			prop.load(file);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
}
