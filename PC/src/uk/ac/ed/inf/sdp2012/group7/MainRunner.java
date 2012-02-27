package uk.ac.ed.inf.sdp2012.group7;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.ui.ControlGUI;
import uk.ac.ed.inf.sdp2012.group7.vision.Simulator;
import uk.ac.ed.inf.sdp2012.group7.vision.Vision;


public class MainRunner {
	
	static public boolean simulator = false;

    /*
    Arbitrary class to give us a main method for testing the vision code
    */

    public static void main(String[] args){
        Logger.getLogger("com.intel.bluetooth").setLevel(Level.WARN);

	if (args.length > 0) {
		simulator = true;
		Simulator sim = new Simulator();
	} else {
        	Vision v = new Vision();
	}
        Strategy s = new Strategy();
        ControlGUI gui = new ControlGUI(s);
        gui.initGUI();

    }

}
