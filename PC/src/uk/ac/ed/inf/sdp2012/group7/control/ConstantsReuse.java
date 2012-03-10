package uk.ac.ed.inf.sdp2012.group7.control;

public interface ConstantsReuse {
	
	public static final String ROBOT_NAME = "Hacktar";
	public static final String ROBOT_MAC = "00:16:53:08:9F:AF";


	// NXT Opcodes
	public static enum OpCodes {
		DO_NOTHING,
		FORWARDS,
		BACKWARDS,
		BACKWARDS_WITH_DISTANCE,
		STOP,
		CHANGE_SPEED,
		ROTATE_LEFT,
		ROTATE_RIGHT,
		ROTATE_BLOCK_LEFT,
		ROTATE_BLOCK_RIGHT,
		ARC_LEFT,
		ARC_RIGHT,
		BEEP,
		FORWARDS_WITH_DISTANCE,
		START_MATCH,
		STOP_MATCH,
		BUMP_ON,
		BUMP_OFF,
		CONTINUE,
		QUIT
	}

}
