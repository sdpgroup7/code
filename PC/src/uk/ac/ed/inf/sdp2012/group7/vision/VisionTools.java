package uk.ac.ed.inf.sdp2012.group7.vision;

public class VisionTools {
	
	public int cmToPixels(float cm){
		float width = (float) Vision.worldState.getPitch().getPitchLength();
		int pixelWidth = Vision.worldState.getPitch().getWidth();
		float pixel = pixelWidth * cm / width;
		return (int) pixel;
	}
	
	public float pixelsToCM(int pixelValue){
		float width = (float) Vision.worldState.getPitch().getPitchLength();
		int pixelWidth = Vision.worldState.getPitch().getWidth();
		float cm = width * pixelValue / pixelWidth;
		return cm;	
	}
	
    public double convertAngle(double a){
    	
    	if(a < 0){
    		a = (2.0*Math.PI) + a;
    	}    	
    	a += (2*Math.PI);
    	a = a - (3*Math.PI/2);
    	a = a % (2*Math.PI);
    	return a;
    	
    }
}