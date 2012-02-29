#ifndef __opcodes_h__
#define __opcodes_h__

// Keep this in line with control/ConstantsReuse.java
enum opcodes {
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
};

#define INSTR_MASK 0x0000FFFF
#define ARG_SHIFT  >> 8

#endif
