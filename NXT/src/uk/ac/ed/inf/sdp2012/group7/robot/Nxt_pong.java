package uk.ac.ed.inf.sdp2012.group7.robot;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

/**
 * Simple NXT bluetooth pong
 */
public class Nxt_pong implements Runnable {

	public static int packet_size = 4

	public static void main(String[] args) {				
		try {
			LCD.clear();
			LCD.drawString("PONG Waiting for connection...", 0, 0);
			NXTConnection connection = Bluetooth.waitForConnection();
			InputStream is = connection.openInputStream();
			OutputStream os = connection.openOutputStream();
			Sound.beep();
			LCD.drawString("Connected!", 0, 1);
			byte[] packet = new byte[packet_size];
			while (true) {
				is.read(packet);
				os.write(packet);
				os.flush();
			}
			is.close();
			os.close();
			connection.close();
			LCD.drawString("Pong finished.", 0, 2);
		} catch (Exception e) {
			LCD.drawString("EXCEPTION!", 0, 3);
			System.err.println(e.getMessage());
		}
	}
}
