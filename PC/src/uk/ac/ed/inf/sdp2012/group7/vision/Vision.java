package uk.ac.ed.inf.sdp2012.group7.vision;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
* The main class used to run the vision system. Creates the control
* GUI, and initialises the image processing.
*
* @author s0840449
*/
public class Vision {
    public static WorldState worldState;
    public static final Logger logger = Logger.getLogger(Vision.class);
    public static boolean TESTING = false;
    public static BufferedImage backgroundImage;
    public static int testCount = 0;
    
    
    /**
* The main method for the class. Creates the control
* GUI, and initialises the image processing.
*
* @param args Program arguments. Not used.
*/
    
    public Vision(ActionListener strategyListener){
     Logger.getLogger("com.intel.bluetooth").setLevel(Level.WARN);
    
     Logger.getRootLogger().setLevel(Level.WARN);
        logger.setLevel(Level.WARN);
    }

    public Vision() {
        
        BasicConfigurator.configure();
        
        if(TESTING){
         Vision.logger.info("Vision System Start in Testing Mode");
        } else {
         Vision.logger.info("Vision System Started");
        }
        
        worldState = WorldState.getInstance();

        /* Default to main pitch. */
        /* Default values for the main vision window. */
        String videoDevice = "/dev/video0";
        int width = 640; //these dont actually appear to do anything?
        int height = 480;
        int channel = 0;
        int videoStandard = V4L4JConstants.STANDARD_PAL;
        int compressionQuality = 100;

        try {
            /* Create a new Vision object to serve the main vision window. */
            new VisionFeed(videoDevice, width, height, channel, videoStandard, compressionQuality);
            Vision.logger.info("Vision System Initialised");
        } catch (V4L4JException e) {
         Vision.logger.fatal("V4L4JException: " + e.getMessage());
        } catch (Exception e) {
         Vision.logger.fatal("Exception: " + e.getMessage());
        }
    }

}
