package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class Simulator {
	private static ControlGUI thresholdsGUI;
	public static WorldState worldState;
	public static final Logger logger = Logger.getLogger(Simulator.class);
	public static final boolean TESTING = false;
	public static BufferedImage backgroundImage;


	/**
	 * The main method for the class. Creates the control
	 * GUI, and initialises the image processing.
	 *
	 * @param args Program arguments. Not used.
	 */

	public Simulator(ActionListener strategyListener){
		Logger.getLogger("com.intel.bluetooth").setLevel(Level.WARN);

		Logger.getRootLogger().setLevel(Level.WARN);
		logger.setLevel(Level.WARN);
	}

	public Simulator() {

		BasicConfigurator.configure();

		if(TESTING){
			logger.info("Simulator System Start in Testing Mode");
		} else {
			logger.info("Simulator System Started");
		}
		try {
			//For some reason, if this is anywhere else it fails to load the stream.
			backgroundImage = ImageIO.read(new File("testData/.background.png"));
			logger.info("Loaded background image.");
		} catch (Exception e) {
			logger.fatal("Failed to load backgroundImage");
		} finally {
			if(backgroundImage == null){
				logger.fatal("Background Image is null. Program ending.");
				//System.exit(0);
			}
		}

		worldState = new WorldState();
		ThresholdsState thresholdsState = new ThresholdsState();

		/* Default to main pitch. */
		/* Default values for the main vision window. */
		int width = 640;
		int height = 480;

		try {

			/* Create the Control GUI for threshold setting/etc. */
			thresholdsGUI = new ControlGUI(thresholdsState, worldState);
			thresholdsGUI.initGUI();

			/* Create a new Vision object to serve the main vision window. */
//			new VisionFeed(videoDevice, width, height, channel, videoStandard, compressionQuality, thresholdsGUI);
			new SimulatorFeed(thresholdsGUI);
			logger.info("Simulator System Initialised");

		} catch (Exception e) {
			logger.fatal("Exception: " + e.getMessage());
		}
	}

	public WorldState getWorldState(){
		//used for debugging purposes
		return worldState;
	}
}
