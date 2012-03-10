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
 * Output is similar to unix ping(8) utility.
 * 
 * @author s0927919
 *
 */

public class TestConnection {


	private static int count = 10000;
	private static int packet_size = 4; /* between 1 and 4, count must fit inside */

	public static void main(String[] args) throws Exception {
		NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		NXTInfo info = new NXTInfo(NXTCommFactory.BLUETOOTH,ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC);
		nxtComm.open(info);
		OutputStream os = new OutputStream(nxtComm.getOutputStream());
		InputStream is = new InputStream(nxtComm.getInputStream());

		long[] rtts = new long[count];
		long rtt_sum = 0;
		
		long rtt_min = java.lang.Long.MAX_VALUE;
		long rtt_max = 0;
		long rtt_avg = 0; 
		long rtt_mdev = 0;

		System.out.printf("PING %s (%s) %d bytes of data.\n", ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC, packet_size);

		byte packet[] = new byte[packet_size];
		byte reply[] = new byte[packet_size];

		ByteBuffer reply_int = ByteBuffer.allocate(4);
		String mis_seq = new String;
		int mis_seq_count = 0;

		/* Ping */
		for (int seq = 0; seq < count; ++seq) {
			/* copy seq to packet */
			for (int c = 4 - packet_size; c < 4; ++c)
				packet[c] = seq & (0xFF000000 >> c*8);

			long sendTime = System.nanoTime();
			os.write(packet);
			os.flush();
			int recv_size = is.read(reply);
			long recvTime = System.nanoTime();
			long rtt = recvTime - sendTime;
			reply_int.put(reply, 4-recv_size, recv_size);
			mis_seq = "";
			if (!Arrays.equals(packet, reply)) {
				mis_seq = " BAD_SEQ";
				++mis_seq_count;
			}
			System.out.printf("%d byte from %s (%s) seq_send=%d seq_recv=%d time=%.1f ms%s\n", recv_size, ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC, seq, reply_int.getInt(), (float)rtt/1000000, mis_seq);
			rtts[i] = rtt;
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
