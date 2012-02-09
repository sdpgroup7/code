package uk.ac.ed.inf.sdp2012.group7.testing.vision;

import java.awt.Graphics;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class VisionPanel extends Panel {
	
	private static final long serialVersionUID = -7017770406220738001L;
	BufferedImage image;
	
	public VisionPanel(String imageName) {
		try {
			File input = new File(imageName);
			image = ImageIO.read(input);
		} catch (IOException ie) {
			System.out.println("Error: " + ie.getMessage());
		}
	}

	public BufferedImage getImage(){
		return this.image;
	}
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
}
