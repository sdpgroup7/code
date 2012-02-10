package uk.ac.ed.inf.sdp2012.group7.testing.vision;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Point;

import uk.ac.ed.inf.sdp2012.group7.vision.InitialLocation;
import org.junit.Test;

public class VisionTest {

	@Test
	public void testProcessAndUpdateImage() {
		assertTrue(true);
	}

	@Test
	public void testMarkObjects() {
		assertTrue(true);
	}
	
	@Test
	public void testCorrectColoursFromImage() {
		System.out.print("Testing correct colors returned: ");
		InitialLocation il = new InitialLocation();
		File input = new File("testData/.testImage.png");
		try {
			BufferedImage image = ImageIO.read(input);
			assertTrue(il.getColor(new Point(100,100), image).equals(new Color(99,160,43)));
			assertTrue(il.getColor(new Point(200,200), image).equals(new Color(119,197,62)));
			assertTrue(il.getColor(new Point(300,300), image).equals(new Color(121,189,52)));
			assertTrue(il.getColor(new Point(400,400), image).equals(new Color(102,82,55)));
			System.out.println("Finished");
		} catch (IOException e) {
			System.out.println("Failed to load test image");
			assertTrue(1==0);
		}
	}
	
	@Test
	public void testVisionTestingGetOrientation() {
		VisionTesting v = new VisionTesting();
		double O = v.getOrientation(new Point(0,0),new Point(1,1));
		assertTrue(Math.abs(O-(Math.PI/4.0)) < 0.001);
		O = v.getOrientation(new Point(0,0),new Point(-1,1));
		System.out.println(O);
		assertTrue(Math.abs(O-(7.0*Math.PI/4.0)) < 0.001);
		System.out.println("Angle Calculation Passed");
	}

}
