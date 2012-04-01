package uk.ac.ed.inf.sdp2012.group7;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import uk.ac.ed.inf.sdp2012.group7.simulator.Simulator;
import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.ui.ControlGUI;
import uk.ac.ed.inf.sdp2012.group7.vision.Vision;


public class MainRunner {
	
	static public boolean simulator = false;

    /*
    Arbitrary class to give us a main method for testing the vision code
    */

    public static void main(String[] args) throws IOException, ClassNotFoundException{
        Logger.getLogger("com.intel.bluetooth").setLevel(Level.WARN);
        if (args.length > 0) {
        	simulator = true;
        	new Simulator();
        } else {
        	new Vision();
        }
		  Simulator.logger.setLevel(Level.WARN);
        Strategy s = new Strategy();
        ControlGUI gui = new ControlGUI(s);
        gui.initGUI();

    }

}
