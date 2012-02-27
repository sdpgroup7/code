package uk.ac.ed.inf.sdp2012.group7;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;
<<<<<<< HEAD
import uk.ac.ed.inf.sdp2012.group7.ui.ControlGUI;
=======
import uk.ac.ed.inf.sdp2012.group7.vision.Simulator;
>>>>>>> simulator


public class MainRunner {

    /*
    Arbitrary class to give us a main method for testing the vision code
    */

		  public static boolean simulator = false;

    public static void main(String[] args){
    	Logger.getLogger("com.intel.bluetooth").setLevel(Level.WARN);
<<<<<<< HEAD
        ControlGUI gui = new ControlGUI();
        gui.initGUI();
    	Vision v = new Vision();
        //Strategy s = new Strategy(v,gui);
=======
    	
    	Vision v;
		
			if (args.length > 0) {
					  simulator = true;
					  Simulator s = new Simulator();
			} else
        		v = new Vision();

>>>>>>> simulator
    }

}
