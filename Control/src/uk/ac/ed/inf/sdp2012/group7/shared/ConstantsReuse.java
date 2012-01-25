package uk.ac.ed.inf.sdp2012.group7.shared;

public class ConstantsReuse {
	private ConstantsReuse() {
	}

	public static String ROBOT_NAME = "Hacktar";
	public static String ROBOT_MAC = "00:16:53:08:9F:AF";

    public enum OpCodes {
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
        CELEBRATE
    }

}
