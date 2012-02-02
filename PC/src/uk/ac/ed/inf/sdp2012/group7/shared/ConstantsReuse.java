package uk.ac.ed.inf.sdp2012.group7.shared;

public class ConstantsReuse {
	private ConstantsReuse() {
	}

	public static final String ROBOT_NAME = "Hacktar";
	public static final String ROBOT_MAC = "00:16:53:08:9F:AF";

    public static enum OpCodes {
        DO_NOTHING,
        FORWARDS,
        BACKWARDS,
        BACKWARDS_SLIGHTLY,
        STOP,
        CHANGE_SPEED,
        KICK,
        ROTATE,
        ARC,
        STEER_WITH_RATIO,
        BEEP,
        CELEBRATE,
        FORWARDS_WITH_DISTANCE,
        QUIT
    }

}
