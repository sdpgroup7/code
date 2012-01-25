import java.awt.image.BufferedImage;
import java.awt.Color;

public class NormaliseRGB{

    public BufferedImage normalise(BufferedImage image){
        for (int i = 0; i < 640; i++){
            for (int j = 0; j < 480; j++){
                Color rgb = new Color(image.getRGB(i,j));
                int a = rgb.getAlpha();
                int r = rgb.getRed();
                int g = rgb.getGreen();
                int b = rgb.getBlue();
                r = (r/(r+g+b))*255;
                g = (g/(r+g+b))*255;
                b = (b/(r+g+b))*255;
                image.setRGB(i,j,r*g*b);
            }
        }
        return image;
    }
}
