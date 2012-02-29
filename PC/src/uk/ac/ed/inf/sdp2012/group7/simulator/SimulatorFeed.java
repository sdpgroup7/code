package uk.ac.ed.inf.sdp2012.group7.simulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.strategies.QuadSpaceStrategy;

public class SimulatorFeed extends WindowAdapter {
	private JLabel label;
	private JFrame windowFrame;
	private int width = 640, height = 480;
	
	private BufferedImage background;
	private BufferedImage blueRobot_img;
	private BufferedImage yellowRobot_img;
	public boolean paused = false;
	int count = 0;
	
	private Receiver receiver;
	
	private World world;
	private Body blueRobot;
	private Body yellowRobot;
	private Body ball;
	
	/**
	 * Default constructor.
	 *
	 *
	 */
	public SimulatorFeed() {
		try {
			background = ImageIO.read(new File("simData/background.png"));
			blueRobot_img = ImageIO.read(new File("simData/blue.png"));
			yellowRobot_img = ImageIO.read(new File("simData/yellow.png"));
		} catch (IOException e) {
			Simulator.logger.fatal("Can't load file: "+e.toString());
		}
		

		/* Initialise the GUI that displays the video feed. */
		Simulator.logger.info("Init GUI");
		initGUI();
		Simulator.logger.info("Init WorldSimulator");
		initWorldSimulation();
		Simulator.logger.info("Init FrameGenerator");
		initFrameGenerator();
		Simulator.logger.info("Set ClickingDone");
		Simulator.worldState.setClickingDone(true);
		Simulator.logger.info("Bye");

	}


    /**
	 * Starts the frame generator
	 */
	
	private void initFrameGenerator() {
		receiver = new Receiver();
		receiver.init(blueRobot, yellowRobot, ball, background, blueRobot_img, yellowRobot_img, label);
		receiver.start();
	}

	/**
	 * Creates the world
	 */
	private void initWorldSimulation() {
		/* Initialise world */
		world = new World(new Vector2f(0, 10), 10, new QuadSpaceStrategy(20, 5));
		world.clear();
		world.setGravity(0, 0);
		
		/* Build walls */
		Body topWall = new StaticBody("top_wall", new Box(640, 104));
		topWall.setPosition(0, 0);
		topWall.setRestitution(1);
		world.add(topWall);
		
		Body bottomWall = new StaticBody("bottom_wall", new Box(640, 104));
		bottomWall.setPosition(0, 384);
		bottomWall.setRestitution(1);
		world.add(bottomWall);
		
		/* Needs to be done this way because we want to leave a hole for the goals */
		Body leftWall_top = new StaticBody("left_top_wall", new Box(40, 196));
		leftWall_top.setPosition(0, 0);
		leftWall_top.setRestitution(1);
		world.add(leftWall_top);
		
		Body leftWall_bottom = new StaticBody("left_bottom_wall", new Box(40, 196));
		leftWall_bottom.setPosition(0, 337);
		leftWall_bottom.setRestitution(1);
		world.add(leftWall_bottom);
		
		Body rightWall_top = new StaticBody("right_top_wall", new Box(40, 196));
		rightWall_top.setPosition(607, 0);
		rightWall_top.setRestitution(1);
		world.add(rightWall_top);
		
		Body rightWall_bottom = new StaticBody("right_bottom_wall", new Box(40, 196));
		rightWall_bottom.setPosition(607, 337);
		rightWall_bottom.setRestitution(1);
		world.add(rightWall_bottom);
		
		/* Set up robots */
		blueRobot = new Body("blue_robot", new Box(47, 33), 10000000);
		blueRobot.setFriction(100);
		blueRobot.setRestitution(1);
		blueRobot.setDamping(2f);
		blueRobot.setRotatable(false);
		world.add(blueRobot);
		
		yellowRobot = new Body("yellow_robot", new Box(47, 33), 10000000);
		yellowRobot.setFriction(100);
		yellowRobot.setRestitution(1);
		yellowRobot.setDamping(2);
		yellowRobot.setRotatable(false);
		world.add(yellowRobot);
		
		
		/* Set up ball */
		ball = new Body("ball", new Circle(5), 5);
		ball.setDamping(0.005f);
		ball.setRestitution(0.8f);
		ball.setCanRest(true);
		ball.setPosition(320, 240);
		world.add(ball);
	}
	
	/**
	 * Creates the graphical interface components and initialises them
	 */
	private void initGUI() {
		windowFrame = new JFrame("Vision Window");
		label = new JLabel();
		windowFrame.getContentPane().add(label);
		windowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		windowFrame.addWindowListener(this);
		windowFrame.setVisible(true);
		windowFrame.setSize(width+5, height+25);
	}

	//useless, had to be included because of the MouseEvent interface


	//can output the buffered image to disk, can normalise if necessary
	public static void writeImage(BufferedImage image, String fn){
		try {
			File outputFile = new File(fn);
			ImageIO.write(image, "png", outputFile);
		} catch (Exception e) {
			Simulator.logger.error("Failed to write image: " + e.getMessage());
		}
	}

	/**
	 * Catches the window closing event, so that we can free up resources
	 * before exiting.
	 *
	 * @param e         The window closing event.
	 */
	@SuppressWarnings("deprecation")
	public void windowClosing(WindowEvent e) {
		/* Dispose of the various swing and v4l4j components. */
		receiver.stop();

		windowFrame.dispose();
		Simulator.logger.info("Vision System Ending...");
		System.exit(0);
	}
}
