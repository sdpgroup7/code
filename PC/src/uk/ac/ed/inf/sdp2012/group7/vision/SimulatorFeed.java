package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import net.phys2d.raw.strategies.QuadSpaceStrategy;

public class SimulatorFeed extends WindowAdapter {
	private JLabel label;
	private JFrame windowFrame;
	private int width = 640, height = 480;
	private BufferedImage frameImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
	
	private BufferedImage background;
	public boolean paused = false;
	int count = 0;
	
	private String simHost = "localhost";
	private int    simPort = 10002;

	private Socket       socket;
	private InputStream  is;
	
	private World world;
	
	/**
	 * Default constructor.
	 *
	 *
	 */
	public SimulatorFeed() {
		try {
			background = ImageIO.read(new File("simData/background.png"));
		} catch (IOException e) {
			Simulator.logger.fatal("Can't load background: "+e.toString());
		}
		

		/* Initialise the GUI that displays the video feed. */
		initGUI();
		initWorldSimulation();
		initFrameGenerator();
		Simulator.worldState.setClickingDone(true);
	}


	public BufferedImage getFrameImage(){
		return this.frameImage;
	}

	private Thread receiver = new Thread() {
		
		private int simAngleToNormal(int simAngle) {
			simAngle = simAngle - 90;
			if (simAngle < 0)
				return 360 + simAngle;
			return simAngle;
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
				Simulator.worldState.getYellowRobot().setPosition(buf[3], buf[4]);
				Simulator.worldState.getYellowRobot().setAngle(Math.toRadians(buf[5]));
				Simulator.worldState.getBall().addPosition(new Point(buf[6], buf[7]));
				
				frameImage.setData(background.getData());
				
				Graphics g = frameImage.getGraphics();
				g.setColor(Color.blue);
				g.fillRect(buf[0]-24, buf[1]-17, 47, 33);
				g.setColor(Color.yellow);
				g.fillRect(buf[3]-24, buf[4]-17, 47, 33);
				g.setColor(Color.red);
				g.fillOval(buf[6]-5, buf[7]-5, 11, 11);
								
				label.getGraphics().drawImage(frameImage, 0, 0, frameImage.getWidth(), frameImage.getHeight(), null);
			}
		}
		
	};
	
	/**
	 * Starts the frame generator
	 */
	
	private void initFrameGenerator() {
		
		receiver.run();
		
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
			Vision.logger.error("Failed to write image: " + e.getMessage());
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
		Vision.logger.info("Vision System Ending...");
		System.exit(0);
	}
}
