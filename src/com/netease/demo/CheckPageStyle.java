package com.netease.demo;

import com.netease.checkuistyle.ImageProcessing;
import com.netease.dagger.BrowserEmulator;

public class CheckPageStyle {
	public static void main(String[] args) throws Exception {
		String githubUrl = "https://github.com/";
		String checkPoint = "topbar";
		String folderName = "github";	
		BrowserEmulator be = new BrowserEmulator();
		be.open(githubUrl);//change url to https://github.com/login for contrast, remember change ScreenShotType to 2 in type.properties
		ImageProcessing.process(be.getBrowserCore(), folderName, checkPoint, "//div[@class='container clearfix']");
		be.quit();
	}
}
