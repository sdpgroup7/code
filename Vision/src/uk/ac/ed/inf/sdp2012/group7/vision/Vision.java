package uk.ac.ed.inf.sdp2012.group7.vision;
import uk.ac.ed.inf.sdp2012.group7.vision.ui.ControlGUI;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

/** 
 * The main class used to run the vision system. Creates the control
 * GUI, and initialises the image processing.
 * 
 * @author s0840449
 */
public class Vision implements VisualMethods {
    private static ControlGUI thresholdsGUI;
    private static WorldState worldState;
    
    /**
     * The main method for the class. Creates the control
     * GUI, and initialises the image processing.
     * 
     * @param args        Program arguments. Not used.
     */
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

    public ObjectPosition getBallPosition(){
        //gets the position of the ball
        return new ObjectPosition(worldState.getBallX(),worldState.getBallY());
    }
    
    public ObjectPosition getOurPosition(){
        //gets the position of our robot (return center?)
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ObjectPosition getOpponentPosition(){
        //gets the position of our oponents robot
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ObjectPosition getOurOrientation(){
        //gets our orientation
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ObjectPosition getOpponentOrientation(){
        //gets our opponents orientation
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getDistanceToOpponent(){
        //gets the distance and direction to our opponent
        throw new UnsupportedOperationException("Not yet implemented");
    }


    //Priority 2 - Needed to have any sort of success

    public Vector2 getDistanceToSide(){
        //finds distance to nearest side (specified maybe?)
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getBallVelocity(){
        //gets the velocity of the ball
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getOurVelocity(){
        //gets our velocity
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getOpponentVelocity(){
        //gets our opponents velocity
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Vector2 getDistanceToBall(){
        //find distance to ball
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int whatDidWeHit(){
        //if you hit something we'll tell you what you hit, using a code 
        throw new UnsupportedOperationException("Not yet implemented");
    }



//Priority 3 - Could be useful

    public boolean didWeScore(){
        //Check whether the ball is in opponents goal (then victory celebration)
        throw new UnsupportedOperationException("Not yet implemented");
    }



}
