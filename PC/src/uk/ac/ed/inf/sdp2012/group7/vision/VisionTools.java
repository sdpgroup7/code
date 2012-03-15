package uk.ac.ed.inf.sdp2012.group7.vision;

import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

public class VisionTools {
	
	/**
	 * @author Dale Myers
	 */
	
	private static WorldState worldState = WorldState.getInstance();
	//TODO: Test ALL of these. Comment to confirm
	
	/**
	 * Convert between cm and pixels
	 * 
	 * @param cm The number of cm to convert
	 * @return Number of pixels
	 */
	public static int cmToPixels(float cm){
		float width = (float)(worldState.getPitch().getRightBuffer() - worldState.getPitch().getLeftBuffer());
		float pixel = ((width/244f)*cm);
		return (int) pixel;
	}
	
	/**
	 * Convert between pixels and cm
	 * 
	 * @param pixelValue Number of pixels to convert
	 * @return Number of cms
	 */
	public static float pixelsToCM(double pixelValue){
		float width = (float)(worldState.getPitch().getRightBuffer() - worldState.getPitch().getLeftBuffer());
		float cm = (float)((244f/width)*pixelValue);
		return cm;	
	}
	
	public static float pixelsToCM(int pixelValue){
		return pixelsToCM((double)pixelValue);	
	}
	
	/**
	 * Nothing
	 * 
	 * Used to convert from output of atan2 to the agreed upon system
	 * Current system is for vision to just use atan2 output and other systems
	 * to do what they want with that.
	 * 
	 * @param a The output of atan2
	 * @return a
	 */
    public static double convertAngle(double a){
    	/*
    	if(a < 0){
    		a = (2.0*Math.PI) + a;
    	}    	
    	a += (2*Math.PI);
    	a = a - (3*Math.PI/2);
    	a = a % (2*Math.PI);
    	*/
    	return a;
    	
    }
    
    /**
     * Presumably not needed anymore now we are just using atan2
     * 
     * @param a The output of convertAngle
     * @return a
     */
    public static double convertAngleBack(double a){
    	/*
    	a = a + (Math.PI * 2);
    	a = a - (Math.PI / 2);
    	if(a > Math.PI){
    		a = (2 * Math.PI) - a;
    	} else {
    		a = -a;
    	}
    	a = a % (Math.PI * 2);
    	*/
    	return a;
    }
}
