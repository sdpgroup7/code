package uk.ac.ed.inf.sdp2012.group7.vision;
import java.awt.event.ActionListener;

import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/** 
 * The main class used to run the vision system. Creates the control
 * GUI, and initialises the image processing.
 * 
 * @author s0840449
 */
public class Vision {
    private static ControlGUI thresholdsGUI;
    public static WorldState worldState;
    public static final Logger logger = Logger.getLogger(Vision.class);
    public static final boolean TESTING = true;
    
    
    /**
     * The main method for the class. Creates the control
     * GUI, and initialises the image processing.
     * 
     * @param args        Program arguments. Not used.
     */
    
    public Vision(ActionListener strategyListener){
        
    }

    public Vision() {
        
        BasicConfigurator.configure();
        
        if(TESTING){
        	Vision.logger.info("Vision System Start in Testing Mode");
        } else {
        	Vision.logger.info("Vision System Started");
        }
        //Vision.logger.debug("Sample debug message");
        //Vision.logger.info("Sample info message");
        //Vision.logger.warn("Sample warn message");
        //Vision.logger.error("Sample error message");
        //Vision.logger.fatal("Sample fatal message");
        worldState = new WorldState();
        ThresholdsState thresholdsState = new ThresholdsState();

        /* Default to main pitch. */
        /* Default values for the main vision window. */
        String videoDevice = "/dev/video0";
        int width = 640; //these dont actually appear to do anything?
        int height = 480;
        int channel = 0;
        int videoStandard = V4L4JConstants.STANDARD_PAL;
        int compressionQuality = 80;

        try {

            /* Create the Control GUI for threshold setting/etc. */
            thresholdsGUI = new ControlGUI(thresholdsState, worldState);
            thresholdsGUI.initGUI();

            /* Create a new Vision object to serve the main vision window. */
            new VisionFeed(videoDevice, width, height, channel, videoStandard, compressionQuality, thresholdsGUI);
            Vision.logger.info("Vision System Initialised");
        } catch (V4L4JException e) {
        	Vision.logger.fatal("V4L4JException: " + e.getMessage());
        } catch (Exception e) {
        	Vision.logger.fatal("Exception: " + e.getMessage());
        }
    }

    public WorldState getWorldState(){
        //used for debugging purposes
        return Vision.worldState;
    }
}
