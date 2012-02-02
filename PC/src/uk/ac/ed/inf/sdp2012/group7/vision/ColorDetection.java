package uk.ac.ed.inf.sdp2012.group7.vision;
import java.awt.Color;

public class ColorDetection{


    private ThresholdsState thresholdsState;

    public ColorDetection(ThresholdsState ts){
        this.thresholdsState = ts;
    }


    /**
     * Determines if a pixel is part of the blue T, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the blue T),
     *                      false otherwise.
     */
    public boolean isBlue(Color color) {
        return color.getRed() <= thresholdsState.getBlue_r_high() && color.getRed() >= thresholdsState.getBlue_r_low() &&
        color.getGreen() <= thresholdsState.getBlue_g_high() && color.getGreen() >= thresholdsState.getBlue_g_low() &&
        color.getBlue() <= thresholdsState.getBlue_b_high() && color.getBlue() >= thresholdsState.getBlue_b_low();
    }

    /**
     * Determines if a pixel is part of the yellow T, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the yellow T),
     *                      false otherwise.
     */

    public boolean isYellow(Color colour) {
        return colour.getRed() <= thresholdsState.getYellow_r_high() &&  colour.getRed() >= thresholdsState.getYellow_r_low() &&
        colour.getGreen() <= thresholdsState.getYellow_g_high() && colour.getGreen() >= thresholdsState.getYellow_g_low() &&
        colour.getBlue() <= thresholdsState.getYellow_b_high() && colour.getBlue() >= thresholdsState.getYellow_b_low();
    }

    /**
     * Determines if a pixel is part of the ball, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of the ball),
     *                      false otherwise.
     */

    public boolean isBall(Color colour) {
        return colour.getRed() <= thresholdsState.getBall_r_high() &&  colour.getRed() >= thresholdsState.getBall_r_low() &&
        colour.getGreen() <= thresholdsState.getBall_g_high() && colour.getGreen() >= thresholdsState.getBall_g_low() &&
        colour.getBlue() <= thresholdsState.getBall_b_high() && colour.getBlue() >= thresholdsState.getBall_b_low();
    }

    /**
     * Determines if a pixel is part of either grey circle, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of a grey circle),
     *                      false otherwise.
     */
    public boolean isGrey(Color colour) {
        return colour.getRed() <= thresholdsState.getGrey_r_high() &&  colour.getRed() >= thresholdsState.getGrey_r_low() &&
        colour.getGreen() <= thresholdsState.getGrey_g_high() && colour.getGreen() >= thresholdsState.getGrey_g_low() &&
        colour.getBlue() <= thresholdsState.getGrey_b_high() && colour.getBlue() >= thresholdsState.getGrey_b_low();

    }

    /**
     * Determines if a pixel is part of either green plate, based on input RGB colors
     * and hsv values.
     *
     * @param color         The RGB colors for the pixel.
     * @param hsbvals       The HSV values for the pixel.
     *
     * @return              True if the RGB and HSV values are within the defined
     *                      thresholds (and thus the pixel is part of a green plate),
     *                      false otherwise.
     */

    public boolean isGreen(Color colour) {
        return colour.getRed() <= thresholdsState.getGreen_r_high() &&  colour.getRed() >= thresholdsState.getGreen_r_low() &&
        colour.getGreen() <= thresholdsState.getGreen_g_high() && colour.getGreen() >= thresholdsState.getGreen_g_low() &&
        colour.getBlue() <= thresholdsState.getGreen_b_high() && colour.getBlue() >= thresholdsState.getGreen_b_low();

    }

}
