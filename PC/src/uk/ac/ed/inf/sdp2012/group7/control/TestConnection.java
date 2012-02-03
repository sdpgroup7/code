package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.IOException;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

import java.io.*;


public class TestConnection {
	public static void main(String[] args) throws NXTCommException, IOException, Exception {
		NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
	    NXTInfo info = new NXTInfo(NXTCommFactory.BLUETOOTH,ConstantsReuse.ROBOT_NAME, ConstantsReuse.ROBOT_MAC);
	    nxtComm.open(info);
	    DataOutputStream dos = new DataOutputStream(nxtComm.getOutputStream());
    	dos.writeInt(100);
    	dos.flush();
    	System.out.println("done");
	}
}
