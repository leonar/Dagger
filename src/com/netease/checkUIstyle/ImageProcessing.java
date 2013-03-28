package com.netease.checkUIstyle;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;

public class ImageProcessing {
	public static String sImg = "res/samples/";
	public static String cImg = "images/";
	/**
	 * ScreenShot
	 * @param augmentedDriver	WebDriver
	 * @param folderName
	 * @param imageName
	 * @param type
	 * @return	 path
	 */
	public static String screenShot(WebDriver augmentedDriver, String folderName, String imageName, String type) {
		Robot rb = null;
		try {
			rb = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		rb.mouseMove(0, 0);
		
		String dir_name = null;
		if(type.equals("1"))
		{
			dir_name = sImg + folderName;
		}
		else if(type.equals("2"))
		{
			dir_name = cImg + folderName; 
		}
		else
		{
			Assert.fail("Wrong type！Please check type.properties！");
		}
		augmentedDriver = new Augmenter().augment(augmentedDriver);				
		try {
			File source_file = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(source_file, new File(dir_name + File.separator + imageName + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dir_name + File.separator + imageName + ".png";
	}
	
	/**
	 * clear images
	 * @param dir
	 */
	public static void deleteFiles(String dir){
		try{
			File file = new File(dir);
			if (!file.exists()) { 
				return; 
				} 	
		    if (!file.isDirectory()) { 
		    	return; 
		    	}
		    String[] tempList = file.list(); 
		       File temp = null;
		       for (int i = 0; i < tempList.length; i++) { 
		    	   if (dir.endsWith(File.separator)) { 
		               temp = new File(dir + tempList[i]); 
		           } 
		           else { 
		               temp = new File(dir + File.separator + tempList[i]); 
		           } 
		           if (temp.isFile()) { 
		               temp.delete(); 
		           } 
		       }
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * get screenshot type
	 * @return type
	 */
	public static String getType(){
		File pFile = new File("type.properties");
		FileInputStream pInStream=null;
		try {
			pInStream = new FileInputStream(pFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Properties p = new Properties();
		try {
			p.load(pInStream); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p.getProperty("type");
	}
	
	/**
	 * image process
	 * @param driver
	 * @param folderName
	 * @param checkPoint
	 * @param xpath
	 */	
	public static void process(WebDriver driver, String folderName, String checkPoint, String xpath) throws Exception
	{
		WebElement we = FindElement(driver, By.xpath(xpath));
		String sampleImage = checkPoint + "_sample";
		String differenceImage = checkPoint + "_difference";
		String actualImage = null;
		String type = ImageProcessing.getType();
		if(type.equals("1"))
		{
			actualImage = ImageProcessing.screenShot(driver, folderName, sampleImage, type);
		}else if(type.equals("2"))
		{
			actualImage = ImageProcessing.screenShot(driver, folderName, checkPoint, type);
			ImageContrast.contrastImages(sImg + folderName + sampleImage, actualImage, folderName + differenceImage, we);
		}
		else
		{
			Assert.fail("Wrong type！Please check type.properties！");
		}
	}

	private static WebElement FindElement(WebDriver driver, By by) {
		WebElement webElement = null;
		try {
			webElement = driver.findElement(by);
		} catch (Exception e) {
			Assert.fail(by.toString() + " can not be found！");
		}
		return webElement;
	}
}
