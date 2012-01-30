package uk.ac.ed.inf.sdp2012.group7.vision;
import java.awt.event.ActionListener;

import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

/** 
 * The main class used to run the vision system. Creates the control
 * GUI, and initialises the image processing.
 * 
 * @author s0840449
 */
public class Vision {
    private static ControlGUI thresholdsGUI;
    private static WorldState worldState;
    
    
    /**
     * The main method for the class. Creates the control
     * GUI, and initialises the image processing.
     * 
     * @param args        Program arguments. Not used.
     */
    
    public Vision(ActionListener strategyListener){
    	
    }
    public Vision() {
        worldState = new WorldState();
        ThresholdsState thresholdsState = new ThresholdsState();

        /* Default to main pitch. */
        PitchConstants pitchConstants = new PitchConstants(0);
        /* Default values for the main vision window. */
        String videoDevice = "/dev/video0";
        int width = 640; //these dont actually appear to do anything?
        int height = 480;
        int channel = 0;
        int videoStandard = V4L4JConstants.STANDARD_PAL;
        int compressionQuality = 80;

        try {

            /* Create the Control GUI for threshold setting/etc. */
            thresholdsGUI = new ControlGUI(thresholdsState, worldState, pitchConstants);
            thresholdsGUI.initGUI();

            /* Create a new Vision object to serve the main vision window. */
            VisionFeed feed = new VisionFeed(videoDevice, width, height, channel, videoStandard, compressionQuality, thresholdsGUI, pitchConstants);
            //ObjectPosition o = getBallPosition(); //For testing
        } catch (V4L4JException e) {
            e.printStackTrace(); //TODO: ADD LOGGING!
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
