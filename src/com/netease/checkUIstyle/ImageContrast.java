package com.netease.checkuistyle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

@SuppressWarnings("restriction")
public class ImageContrast {
	private static final double MAX_DELTA_THRESHOLD = 0; // color difference
															// threshold
	private static Point wePosition;
	private static Dimension weDim;

	/**
	 * change RGB to XYZ
	 * 
	 * @param rgb
	 * @return xyz
	 */
	public static ColorXYZ RGB2XYZ(int rgb) {
		ColorXYZ xyz = new ColorXYZ();
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0xff00) >> 8;
		int b = (rgb & 0xff);
		if ((r == 0) && (g == 0) && (b == 0)) {
			xyz.x = 0;
			xyz.y = 0;
			xyz.z = 0;
		} else {
			xyz.x = (0.490 * r + 0.310 * g + 0.200 * b)
					/ (0.667 * r + 1.132 * g + 1.200 * b);
			xyz.y = (0.117 * r + 0.812 * g + 0.010 * b)
					/ (0.667 * r + 1.132 * g + 1.200 * b);
			xyz.z = (0.000 * r + 0.010 * g + 0.990 * b)
					/ (0.667 * r + 1.132 * g + 1.200 * b);
		}
		return xyz;
	}

	/**
	 * change XYZ to LAB
	 * 
	 * @param xyz
	 * @return lab
	 */
	public static ColorLAB XYZ2LAB(ColorXYZ xyz) {
		ColorLAB lab = new ColorLAB();
		double x = xyz.x / 95.047;
		double y = xyz.y / 100.000;
		double z = xyz.z / 108.883;
		x = (x > 0.008856) ? Math.pow(x, 1.0 / 3.0) : (7.787 * x + 16 / 116);
		y = (y > 0.008856) ? Math.pow(y, 1.0 / 3.0) : (7.787 * y + 16 / 116);
		z = (z > 0.008856) ? Math.pow(z, 1.0 / 3.0) : (7.787 * z + 16 / 116);
		lab.l = 116 * Math.pow(y, 1.0 / 3.0) - 16;
		lab.a = 500 * (Math.pow(x, 1.0 / 3.0) - Math.pow(y, 1.0 / 3.0));
		lab.b = 200 * (Math.pow(y, 1.0 / 3.0) - Math.pow(z, 1.0 / 3.0));
		return lab;
	}

	/**
	 * Calculate the color difference
	 * 
	 * @param lab1
	 * @param lab2
	 * @return totalColorDifference
	 */
	public static double getDelta(ColorLAB lab1, ColorLAB lab2) {
		double deltaL = lab1.l - lab2.l; // lightness difference
		double deltaA = lab1.a - lab2.a; // chromaticity difference
		double deltaB = lab1.b - lab2.b; // chromaticity difference
		return Math.pow((Math.pow(deltaL, 2) + Math.pow(deltaA, 2) + Math.pow(
				deltaB, 2)), 0.5); // total color difference
	}

	/**
	 * contrast images
	 * 
	 * @param sampleImagePath
	 * @param actualImagePath
	 * @param differenceImagePath
	 */
	public static boolean contrastImages(String sampleImagePath,
			String actualImagePath, String differenceImagePath, WebElement we)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String time = sdf.format(new Date());
		BufferedImage sample = null, actual = null, difference = null;
		wePosition = we.getLocation();
		weDim = we.getSize();
		int left = wePosition.getX();
		int top = wePosition.getY();
		Integer width = weDim.getWidth();
		Integer height = weDim.getHeight();
		try {
			sample = ImageIO.read(new File(sampleImagePath + ".png"));
			actual = ImageIO.read(new File(actualImagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		difference = new BufferedImage(sample.getWidth(), sample.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		boolean isMatched = true;
		for (int y = top; y < top + height; ++y) {
			for (int x = left; x < left + width; ++x) {
				int expRGB = sample.getRGB(x, y);
				int actRGB = actual.getRGB(x, y);
				int newRGB = actRGB;
				if (expRGB != actRGB) {
					double deltaE = getDelta(XYZ2LAB(RGB2XYZ(expRGB)),
							XYZ2LAB(RGB2XYZ(actRGB)));
					if (deltaE > MAX_DELTA_THRESHOLD) {
						newRGB = 0xff0000;// set red in difference place
					}
					isMatched = false;
				}
				difference.setRGB(x, y, newRGB);
			}
		}
		if (!isMatched) {
			try {
				FileOutputStream out = new FileOutputStream("images/"
						+ differenceImagePath + time + ".png");
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				encoder.encode(difference);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				Assert.fail("UIstyle wrong!" + "images/" + differenceImagePath
						+ time + ".png");
			}
		}
		return isMatched;
	}

	public static class ColorLAB {
		public double l;
		public double a;
		public double b;
	}

	public static class ColorXYZ {
		public double x;
		public double y;
		public double z;
	}
}
