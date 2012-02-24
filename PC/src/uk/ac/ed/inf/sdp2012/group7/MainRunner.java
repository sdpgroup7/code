package uk.ac.ed.inf.sdp2012.group7;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;
import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.ui.ControlGUI;


public class MainRunner {

    /*
    Arbitrary class to give us a main method for testing the vision code
    */

    public static void main(String[] args){
    	Logger.getLogger("com.intel.bluetooth").setLevel(Level.WARN);
    	Vision v = new Vision();
    	Strategy s = new Strategy();
    	ControlGUI gui = new ControlGUI(s);
        gui.initGUI();
        
    }

}
