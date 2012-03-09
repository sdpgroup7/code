package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

import java.io.*;

import uk.ac.ed.inf.sdp2012.group7.control.ConstantsReuse.OpCodes;


/**
 * 
 * Ping clone for NXT!
 * Output is the same as unix ping(8) utility.
 * 
 * @author s0927919
 *
 */

public class TestConnection {


	/** How many times to ping */
	private static int count = 10000;
	/** Command to send */
	private static byte cmd = (byte) OpCodes.DO_NOTHING.ordinal();


	public static void main(String[] args) throws NXTCommException, IOException, Exception {
		NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		NXTInfo info = new NXTInfo(NXTCommFactory.BLUETOOTH,ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC);
		nxtComm.open(info);
		DataOutputStream os = new DataOutputStream(nxtComm.getOutputStream());
		DataInputStream is = new DataInputStream(nxtComm.getInputStream());

		long[] rtts = new long[count];
		long rtt_sum = 0;
		
		long rtt_min = java.lang.Long.MAX_VALUE;
		long rtt_max = 0;
		long rtt_avg = 0; 
		long rtt_mdev = 0;

		System.out.printf("PING %s (%s) %d bytes of data.\n", ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC, 4);

		/* Ping */
		for (int i = 0; i < count; ++i) {
			byte[] command = new byte[4];
			command[0] = 0;
			command[1] = cmd;
			command[2] = 0;
			command[3] = 0;
			
			long sendTime = System.nanoTime();
			os.write(command);
			os.flush();
			is.read();
			long recvTime = System.nanoTime();
			long rtt = recvTime - sendTime;
			System.out.printf("%d byte from %s (%s) seq=%d ttl=-1 time=%.1f ms\n", 1, ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC, i, (float)rtt/1000000);
			rtts[i] = rtt;
		}
		
		/* Stats */
		for (int i = 1; i < count; ++i) {
			if (rtts[i] < rtt_min)
				rtt_min = rtts[i];
			if (rtts[i] > rtt_max)
				rtt_max = rtts[i];
			rtt_sum += rtts[i];
		}
		rtt_avg = rtt_sum/count-1;
		for (int i = 1; i < count; ++i) {
			rtt_mdev += (rtts[i]-rtt_avg)*(rtts[i]-rtt_avg); /* Don't use Math.pow, it converts everything to double! */
		}
		rtt_mdev = (long) Math.sqrt(rtt_mdev/count-1);

		System.out.printf("\n--- %s ping statistics ---\n", ConstantsReuse.ROBOT_NAME);
		System.out.printf("%d packets transmitted, %d received, %d%% packet loss, time %dms\n", count, count, 0, rtt_sum/1000000);
		System.out.printf("rtt min/avg/max/mdev = %.3f/%.3f/%.3f/%.3f ms\n", (float)rtt_min/1000000, (float)rtt_avg/1000000, (float)rtt_max/1000000, (float)rtt_mdev/1000000);
	}
}
