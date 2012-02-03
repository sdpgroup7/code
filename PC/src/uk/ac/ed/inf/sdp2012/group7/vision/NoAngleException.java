package uk.ac.ed.inf.sdp2012.group7.vision;

public class NoAngleException extends Exception {
	
	private static final long serialVersionUID = 3179226598876051007L;

	public NoAngleException() {
		super();
		Vision.logger.error("NoAngleException was thrown!");
	}
	
	public NoAngleException(String message) {
		super(message);
		Vision.logger.error("NoAngleException: " + message);
	}
}
