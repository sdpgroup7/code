package uk.ac.ed.inf.sdp2012.group7.vision;

public class VisionTools {
	
	public static int cmToPixels(float cm){
		float width = (float) Vision.worldState.getPitch().getPitchWidth();
		int pixelWidth = Vision.worldState.getPitch().getWidth();
		float pixel = pixelWidth * cm / width;
		return (int) pixel;
	}
	
	public static float pixelsToCM(double pixelValue){
		float width = (float) Vision.worldState.getPitch().getPitchWidth();
		int pixelWidth = Vision.worldState.getPitch().getWidth();
		float cm = (float)(width * pixelValue / (double)pixelWidth);
		return cm;	
	}
	
	public static float pixelsToCM(int pixelValue){
		return pixelsToCM((double)pixelValue);	
	}
	
    public static double convertAngle(double a){
    	
    	if(a < 0){
    		a = (2.0*Math.PI) + a;
    	}    	
    	a += (2*Math.PI);
    	a = a - (3*Math.PI/2);
    	a = a % (2*Math.PI);
    	return a;
    	
    }
    
    public static double convertAngleBack(double a){
    	a = a + (Math.PI * 2);
    	a = a - (Math.PI / 2);
    	if(a > Math.PI){
    		a = (2 * Math.PI) - a;
    	} else {
    		a = -a;
    	}
    	a = a % (Math.PI * 2);
    	return a;
    }
}
