package uk.ac.ed.inf.sdp2012.group7.vision;

/**
 * Stores the states of the various thresholds.
 * 
 * @author s0840449
 *
 */
 

public class ThresholdsState {
	
	/* Ball. */
	private int ball_r_low;
	private int ball_r_high;
	private int ball_g_low;
	private int ball_g_high;
	private int ball_b_low;
	private int ball_b_high;

	
	/* Blue Robot. */
	private int blue_r_low;
	private int blue_r_high;
	private int blue_g_low;
	private int blue_g_high;
	private int blue_b_low;
	private int blue_b_high;

	
	/* Yellow Robot. */
	private int yellow_r_low;
	private int yellow_r_high;
	private int yellow_g_low;
	private int yellow_g_high;
	private int yellow_b_low;
	private int yellow_b_high;

	
	/* Grey Circle. */
	private int grey_r_low;
	private int grey_r_high;
	private int grey_g_low;
	private int grey_g_high;
	private int grey_b_low;
	private int grey_b_high;

	
	/* Green plates */
	private int green_r_low;
	private int green_r_high;
	private int green_g_low;
	private int green_g_high;
	private int green_b_low;
	private int green_b_high;

	/* Debug flags. */
	private boolean ball_debug;
	private boolean blue_debug;
	private boolean yellow_debug;
	private boolean grey_debug;
	private boolean green_debug;
	
	/**
	 * Default constructor.
	 */
	public ThresholdsState() {
	}

	public int getBall_r_low() {
		return ball_r_low;
	}

	public void setBall_r_low(int ballRLow) {
		ball_r_low = ballRLow;
	}

	public int getBall_r_high() {
		return ball_r_high;
	}

	public void setBall_r_high(int ballRHigh) {
		ball_r_high = ballRHigh;
	}

	public int getBall_g_low() {
		return ball_g_low;
	}

	public void setBall_g_low(int ballGLow) {
		ball_g_low = ballGLow;
	}

	public int getBall_g_high() {
		return ball_g_high;
	}

	public void setBall_g_high(int ballGHigh) {
		ball_g_high = ballGHigh;
	}

	public int getBall_b_low() {
		return ball_b_low;
	}

	public void setBall_b_low(int ballBLow) {
		ball_b_low = ballBLow;
	}

	public int getBall_b_high() {
		return ball_b_high;
	}

	public void setBall_b_high(int ballBHigh) {
		ball_b_high = ballBHigh;
	}


	public int getBlue_r_low() {
		return blue_r_low;
	}

	public void setBlue_r_low(int blueRLow) {
		blue_r_low = blueRLow;
	}

	public int getBlue_r_high() {
		return blue_r_high;
	}

	public void setBlue_r_high(int blueRHigh) {
		blue_r_high = blueRHigh;
	}

	public int getBlue_g_low() {
		return blue_g_low;
	}

	public void setBlue_g_low(int blueGLow) {
		blue_g_low = blueGLow;
	}

	public int getBlue_g_high() {
		return blue_g_high;
	}

	public void setBlue_g_high(int blueGHigh) {
		blue_g_high = blueGHigh;
	}

	public int getBlue_b_low() {
		return blue_b_low;
	}

	public void setBlue_b_low(int blueBLow) {
		blue_b_low = blueBLow;
	}

	public int getBlue_b_high() {
		return blue_b_high;
	}

	public void setBlue_b_high(int blueBHigh) {
		blue_b_high = blueBHigh;
	}

	public int getYellow_r_low() {
		return yellow_r_low;
	}

	public void setYellow_r_low(int yellowRLow) {
		yellow_r_low = yellowRLow;
	}

	public int getYellow_r_high() {
		return yellow_r_high;
	}

	public void setYellow_r_high(int yellowRHigh) {
		yellow_r_high = yellowRHigh;
	}

	public int getYellow_g_low() {
		return yellow_g_low;
	}

	public void setYellow_g_low(int yellowGLow) {
		yellow_g_low = yellowGLow;
	}

	public int getYellow_g_high() {
		return yellow_g_high;
	}

	public void setYellow_g_high(int yellowGHigh) {
		yellow_g_high = yellowGHigh;
	}

	public int getYellow_b_low() {
		return yellow_b_low;
	}

	public void setYellow_b_low(int yellowBLow) {
		yellow_b_low = yellowBLow;
	}

	public int getYellow_b_high() {
		return yellow_b_high;
	}

	public void setYellow_b_high(int yellowBHigh) {
		yellow_b_high = yellowBHigh;
	}

	public boolean isBall_debug() {
		return ball_debug;
	}

	public void setBall_debug(boolean ballDebug) {
		ball_debug = ballDebug;
	}

	public boolean isBlue_debug() {
		return blue_debug;
	}

	public void setBlue_debug(boolean blueDebug) {
		blue_debug = blueDebug;
	}

	public boolean isYellow_debug() {
		return yellow_debug;
	}

	public void setYellow_debug(boolean yellowDebug) {
		yellow_debug = yellowDebug;
	}

	public int getGrey_r_low() {
		return grey_r_low;
	}

	public void setGrey_r_low(int greyRLow) {
		grey_r_low = greyRLow;
	}

	public int getGrey_r_high() {
		return grey_r_high;
	}

	public void setGrey_r_high(int greyRHigh) {
		grey_r_high = greyRHigh;
	}

	public int getGrey_g_low() {
		return grey_g_low;
	}

	public void setGrey_g_low(int greyGLow) {
		grey_g_low = greyGLow;
	}

	public int getGrey_g_high() {
		return grey_g_high;
	}

	public void setGrey_g_high(int greyGHigh) {
		grey_g_high = greyGHigh;
	}

	public int getGrey_b_low() {
		return grey_b_low;
	}

	public void setGrey_b_low(int greyBLow) {
		grey_b_low = greyBLow;
	}

	public int getGrey_b_high() {
		return grey_b_high;
	}

	public void setGrey_b_high(int greyBHigh) {
		grey_b_high = greyBHigh;
	}

	public boolean isGrey_debug() {
		return grey_debug;
	}

	public void setGrey_debug(boolean greyDebug) {
		grey_debug = greyDebug;
	}

	/**
	 * @return the green_r_low
	 */
	public int getGreen_r_low() {
		return green_r_low;
	}

	public void setGreen_r_low(int greenRLow) {
		green_r_low = greenRLow;
	}

	public int getGreen_r_high() {
		return green_r_high;
	}

	public void setGreen_r_high(int greenRHigh) {
		green_r_high = greenRHigh;
	}

	public int getGreen_g_low() {
		return green_g_low;
	}

	public void setGreen_g_low(int greenGLow) {
		green_g_low = greenGLow;
	}

	public int getGreen_g_high() {
		return green_g_high;
	}

	public void setGreen_g_high(int greenGHigh) {
		green_g_high = greenGHigh;
	}

	public int getGreen_b_low() {
		return green_b_low;
	}

	public void setGreen_b_low(int greenBLow) {
		green_b_low = greenBLow;
	}

	public int getGreen_b_high() {
		return green_b_high;
	}

	public void setGreen_b_high(int greenBHigh) {
		green_b_high = greenBHigh;
	}

	public boolean isGreen_debug() {
		return green_debug;
	}

	public void setGreen_debug(boolean greenDebug) {
		green_debug = greenDebug;
	}

}
