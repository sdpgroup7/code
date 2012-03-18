#ifndef __opcodes_h__
#define __opcodes_h__

// Keep this in line with control/ConstantsReuse.java
enum opcodes {
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
};

#define INSTR_SHIFT  >> 16
#define ARG_MASK        0x0000FFFF
#define KICKER_SHIFT >> 24

#endif
