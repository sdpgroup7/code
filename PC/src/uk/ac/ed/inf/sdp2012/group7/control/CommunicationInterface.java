package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.IOException;

public interface CommunicationInterface {
	public void sendToRobot(byte[] command);
	public int recieveFromRobot();
	public void openConnection() throws IOException;
	public void closeConnection();
}
