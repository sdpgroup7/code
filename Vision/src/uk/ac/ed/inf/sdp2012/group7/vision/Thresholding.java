import java.awt.Color;
import java.awt.image.BufferedImage;


public class Thresholding {

    private final Color black= new Color(0,0,0);
    private final Color white = new Color(255,255,255);
    private Color c;
	int GB;// green - blue
	int RG; // red - green
	int RGthresh;
    
    
	   public BufferedImage getThresh(BufferedImage img, int width, int height) { // Method to get thresholded image 

	    	BufferedImage threshed = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
	    	


	    	
	    	for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					c = new Color(img.getRGB(i,j));
					GB = Math.abs((c.getBlue() - c.getGreen()));
					RG = Math.abs((c.getRed() - c.getGreen()));
					if( (c.getRed()>140) &&  (c.getBlue() <=110) &&  (c.getGreen() <= 110) && GB < 35){
						threshed.setRGB(i, j, black.getRGB()); //Red Ball
					}
					else if( RG < 35 &&  (c.getBlue() <=150) && (c.getRed() > 140)  && (c.getRed() > 140)   ){
						threshed.setRGB(i, j, black.getRGB()); // Yellow robot
					}
					else if( (c.getRed() <= 120) && (c.getBlue()>100)  && (c.getGreen()>140) && (c.getGreen() <= 165)){
						threshed.setRGB(i, j, black.getRGB()); // Blue robot 
					}
					else{
						threshed.setRGB(i, j, white.getRGB());
					}
				}
			}
	    	return threshed;
	    }
}
