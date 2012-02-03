package uk.ac.ed.inf.sdp2012.group7;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.vision.Vision;

public class MainRunner {

    /*
    Arbitrary class to give us a main method for testing the vision code
    */

    public static void main(String[] args){
    	Logger.getLogger("com.intel.bluetooth").setLevel(Level.WARN);
        Vision v = new Vision();
        /*while(true){
            System.out.println("Ball Position: " + v.getWorldState().getBallPosition().toString());
            try{
                Thread.sleep(100);
            } catch (Exception e) {}
        }*/
    }

}
