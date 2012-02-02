package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;


public class BluetoothCommunication implements CommunicationInterface {

	private OutputStream os;
    private InputStream is;

    NXTComm nxtComm;
    NXTInfo info;
	
    public BluetoothCommunication(NXTComm nxtComm, NXTInfo info)
    {
    	this.nxtComm = nxtComm;
    	this.info = info;
    }
    
    
    
	public int recieveFromRobot() {
		try {
            int rec = is.read();
            return rec;
        } catch (IOException ex) {
            System.out.println("Error receiving from robot");
            System.out.println(ex.toString());
            return -1;
        }
	}

	public void sendToRobot(int command) {
	    try {
	    	byte[] bytes = ByteBuffer.allocate(4).putInt(command).array();
	        os.write(bytes);
	        os.flush();
	    } catch (IOException ex) {
	        System.out.println("Error sending command to robot");
	        System.out.println(ex.toString());
	    }
	}
	
	public void openConnection() throws IOException{
		
        try {
        	nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			nxtComm.open(info);
		} catch (NXTCommException e) {
			throw new IOException("Failed to connect " + e.toString());
		}

        os = nxtComm.getOutputStream();
        is = nxtComm.getInputStream();
	}
	
	public void closeConnection()
	{
		try {
			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	
	
}
