package uk.ac.ed.inf.sdp2012.group7;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.simulator.Simulator;
import uk.ac.ed.inf.sdp2012.group7.strategy.StrategyOld;
import uk.ac.ed.inf.sdp2012.group7.ui.ControlGUI;
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
        	new Simulator();
        } else {
        	new Vision();
        }
        StrategyOld s = new StrategyOld();
        ControlGUI gui = new ControlGUI(s);
        gui.initGUI();

    }

}
