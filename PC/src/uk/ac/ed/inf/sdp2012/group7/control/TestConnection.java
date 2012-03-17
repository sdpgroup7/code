package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import uk.ac.ed.inf.sdp2012.group7.control.ConstantsReuse.OpCodes;


/**
 * 
 * Ping clone for NXT!
 * Output is similar to unix ping(8) utility.
 * 
 * @author s0927919
 *
 */

public class TestConnection {


	private static int count = 15000; /* at most INT_MAX */
	private static int packet_size = 4; /* between 1 and 8, count must fit inside	*/
	/* 1 -- byte
	 * 2 -- short
	 * 3 --
	 * 4 -- int
	 * 5 --
	 * 6 --
	 * 7 --
	 * 8 -- long */
	long seq;
	static long seq_mask = (long)0xFF00000000000000L;

	public static void main(String[] args) throws Exception {
		NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		NXTInfo info = new NXTInfo(NXTCommFactory.BLUETOOTH,ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC);
		nxtComm.open(info);
		OutputStream os = nxtComm.getOutputStream();
		InputStream is = nxtComm.getInputStream();

		long[] rtts = new long[count];
		long rtt_sum = 0;
		
		long rtt_min = java.lang.Long.MAX_VALUE;
		long rtt_max = 0;
		long rtt_avg = 0; 
		long rtt_mdev = 0; 

		System.out.printf("PING %s (%s) %d bytes of data.\n", ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC, packet_size);

		byte packet[] = new byte[packet_size];
		byte reply[] = new byte[packet_size];

		long reply_l = 0L;
		String mis_seq = new String();
		int mis_seq_count = 0;

		/* Ping */
		for (long seq = 0; seq < count; ++seq) {
			/* copy seq to packet */
			for (int c = 0; c < packet_size; ++c)
				packet[c] = (byte) (seq & (seq_mask >> c*8));

			long sendTime = System.nanoTime();
			os.write(packet);
			os.flush();
			int recv_size = is.read(reply);
			long recvTime = System.nanoTime();
			long rtt = recvTime - sendTime;

			mis_seq = "";
			if (!Arrays.equals(packet, reply)) {
				mis_seq = " BAD_SEQ";
				++mis_seq_count;
			}
			System.out.printf("%d bytes from %s (%s) seq_send=%d seq_recv=%d time=%.1f ms%s\n", recv_size, ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC, seq, 0, (float)rtt/1000000, mis_seq);
			rtts[(int)seq] = rtt;
		}

		nxtComm.close();
		
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
		System.out.printf("%d packets transmitted, %d received, %d%% packet loss, time %dms, %d bad seqs\n", count, count, 0, rtt_sum/1000000, mis_seq_count);
		System.out.printf("rtt min/avg/max/mdev = %.3f/%.3f/%.3f/%.3f ms\n", (float)rtt_min/1000000, (float)rtt_avg/1000000, (float)rtt_max/1000000, (float)rtt_mdev/1000000);
	}
}
