package uk.ac.ed.inf.sdp2012.group7.simulator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;

import javax.swing.JLabel;

public class Receiver extends Thread {
	
	private String simHost = "localhost";
	private int    simPort = 10002;

	private Socket       socket;
	private InputStream  is;
	
	private Body blueRobot;
	private Body yellowRobot;
	private Body ball;
	
	private BufferedImage frameImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
	private BufferedImage background;
	private BufferedImage blueRobot_img;
	private BufferedImage yellowRobot_img;
	
	private JLabel label;
	
	public void init(Body blueRobot, Body yellowRobot, Body ball, BufferedImage background, BufferedImage blueRobot_img, BufferedImage yellowRobot_img, JLabel label) {
		this.blueRobot = blueRobot;
		this.yellowRobot = yellowRobot;
		this.ball = ball;
		this.background = background;
		this.blueRobot_img = blueRobot_img;
		this.yellowRobot_img = yellowRobot_img;
		this.label = label;
	}

	private int simAngleToNormal(int simAngle) {
		simAngle = simAngle - 90;
		if (simAngle < 0)
			return 360 + simAngle;
		return simAngle;
	}
	
	private boolean isBallClose(Body robot) {
		AffineTransform at = AffineTransform.getRotateInstance(
				(robot.getRotation()*(-1)), robot.getPosition().getX(), robot.getPosition().getY());
		Point2D.Double p = new Point2D.Double((double) ball.getPosition().getX(), (double) ball.getPosition().getY());
		at.transform(p,p);
		int kickDist = 20;
		if ((Math.abs(p.getX() - robot.getPosition().getX() - (47/2)) <= kickDist) && (Math.abs(p.getY() - robot.getPosition().getY()) <= (33/2))) {
			return true;
		}
		return false;
	}
	
	public void run() {
		try {
			socket = new Socket(simHost, simPort);
			is = socket.getInputStream();
		} catch (Exception e) {
			Simulator.logger.fatal("Connecting to simulator failed: "+e.toString());
		}
		
		while(true) {
			int[] buf = new int[8];
			byte[] int_buf = new byte[4];
						
			for (int i = 0; i < 8; ++i) {
				try {
					is.read(int_buf);
				} catch (IOException e) {
					Simulator.logger.fatal("Failed to receive packet: "+e.toString());
				}
				buf[i] = (0x000000FF & (int)int_buf[0])
				      | ((0x000000FF & (int)int_buf[1]) << 8)
				      | ((0x000000FF & (int)int_buf[2]) << 16)
				      | ((0x000000FF & (int)int_buf[3]) << 24);
			}
			
			
			buf[2] = simAngleToNormal(buf[2]);
			buf[5] = simAngleToNormal(buf[5]);
			
			Simulator.worldState.getBlueRobot().setPosition(buf[0], buf[1]);
			Simulator.worldState.getBlueRobot().setAngle(Math.toRadians(buf[2]));
			blueRobot.setPosition(buf[0], buf[1]);
			blueRobot.setRotation((float) Math.toRadians(buf[2]));
			
			Simulator.worldState.getYellowRobot().setPosition(buf[3], buf[4]);
			Simulator.worldState.getYellowRobot().setAngle(Math.toRadians(buf[5]));
			yellowRobot.setPosition(buf[3], buf[4]);
			yellowRobot.setRotation((float) Math.toRadians(buf[5]));
			
			if (buf[6] > 0) {
				if (isBallClose(blueRobot)) {
					ball.addForce(new Vector2f(
							10000*(float)Math.cos(blueRobot.getRotation()),
							10000*(float)Math.sin(blueRobot.getRotation())
					));
				}
			}
			
			if (buf[7] > 0) {
				if (isBallClose(yellowRobot)) {
					ball.addForce(new Vector2f(
							10000*(float)Math.cos(yellowRobot.getRotation()),
							10000*(float)Math.sin(yellowRobot.getRotation())
					));
				}
			}
			
			Simulator.worldState.getBall().setPosition((int)ball.getPosition().getX(), (int)ball.getPosition().getY());
			
			frameImage.setData(background.getData());
			Graphics2D g = (Graphics2D) frameImage.getGraphics();
			
			AffineTransform at_b = AffineTransform.getTranslateInstance(blueRobot.getPosition().getX()-47/2,blueRobot.getPosition().getY()-33/2);
			at_b.rotate(blueRobot.getRotation(), 47/2,33/2);
			g.drawImage(blueRobot_img, at_b, null);
			
			AffineTransform at_y = AffineTransform.getTranslateInstance(yellowRobot.getPosition().getX()-47/2,yellowRobot.getPosition().getY()-33/2);
			at_y.rotate(yellowRobot.getRotation(), 47/2,33/2);
			g.drawImage(yellowRobot_img, at_y, null);
			
			g.setColor(Color.red);
			g.fillOval((int)ball.getPosition().getX()-5, (int)ball.getPosition().getY()-5, 11, 11);
			
			label.getGraphics().drawImage(frameImage, 0, 0, frameImage.getWidth(), frameImage.getHeight(), null);
			Simulator.worldState.setUpdatedTime();
		}
	}
	
}
