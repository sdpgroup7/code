package uk.ac.ed.inf.sdp2012.group7.vision;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Stores the states of the various thresholds.
 * 
 * @author s0840449
 *
 */
 

public class ThresholdsState implements java.io.Serializable{
	
	
	private static ThresholdsState thresholdsState = null;
	
	/* Ball. */
	private int ball_r = 130;
	
	private int ball_g = 90;
	
	private int ball_b = 90;
	

	
	/* Blue Robot. */
	private int blue_r = 130;
	private int blue_g = 180;
	private int blue_b = 100;

	
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
	private int green_RG = 75;
	private int green_GB = 50;
	private int green_g = 125;
	
	/* Debug flags. */
	private boolean ball_debug;
	private boolean blue_debug;
	private boolean yellow_debug;
	private boolean grey_debug;
	private boolean green_debug;
	
	/**
	 * Default constructor.
	 */
	private ThresholdsState() {
		
	}
	
	public static ThresholdsState getInstance() throws IOException, ClassNotFoundException{
		if(thresholdsState == null){

			FileInputStream saveFile = new FileInputStream("saveFile.sav");
			ObjectInputStream restore = new ObjectInputStream(saveFile);
			thresholdsState =  (ThresholdsState) restore.readObject();
			//Load the saved ThresholdsState object
		}
		return thresholdsState;
	}

	public int getBall_r() {
		return ball_r;
	}


	public void setBall_r(int ballRHigh) {
		ball_r = ballRHigh;
	}

	public int getBall_g() {
		return ball_g;
	}

	public void setBall_g(int ballGLow) {
		ball_g = ballGLow;
	}


	public int getBall_b() {
		return ball_b;
	}

	public void setBall_b(int ballBLow) {
		ball_b = ballBLow;
	}



	public int getBlue_r() {
		return blue_r;
	}

	public void setBlue_r(int blueRLow) {
		blue_r = blueRLow;
	}

	public int getBlue_g() {
		return blue_g;
	}

	public void setBlue_g(int blueGLow) {
		blue_g = blueGLow;
	}

	public int getBlue_b() {
		return blue_b;
	}

	public void setBlue_b(int blueBLow) {
		blue_b = blueBLow;
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
	public int getGreen_RG() {
		return green_RG;
	}

	public void setGreen_RG(int greenRG) {
		green_RG = greenRG;
	}

	

	public int getGreen_g() {
		return green_g;
	}

	public void setGreen_g(int greenGLow) {
		green_g = greenGLow;
	}

	public int getGreen_GB() {
		return green_GB;
	}

	public void setGreen_GB(int greenBLow) {
		green_GB = greenBLow;
	}


	public boolean isGreen_debug() {
		return green_debug;
	}

	public void setGreen_debug(boolean greenDebug) {
		green_debug = greenDebug;
	}

}
