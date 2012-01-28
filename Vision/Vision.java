import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
import java.awt.color.*;

/** 
 * The main class used to run the vision system. Creates the control
 * GUI, and initialises the image processing.
 * 
 * @author s0840449
 */
public class Vision {
    private static ControlGUI thresholdsGUI;
    
    /**
     * The main method for the class. Creates the control
     * GUI, and initialises the image processing.
     * 
     * @param args        Program arguments. Not used.
     */
    public Vision() {
        WorldState worldState = new WorldState();
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
            VisionFeed feed = new VisionFeed(videoDevice, width, height, channel, videoStandard,
                    compressionQuality, worldState, thresholdsState, pitchConstants,thresholdsGUI);
            
           
            
            
            
        } catch (V4L4JException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
