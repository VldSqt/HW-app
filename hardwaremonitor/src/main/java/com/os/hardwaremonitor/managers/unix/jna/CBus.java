package com.os.hardwaremonitor.managers.unix.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class CBus extends Structure {

    /**
     *communication system that transfers data between components inside a computer, or between computers
     *used to call native C in order to retrieve components
     */

    public static final String TYPE = "type";
    public static final String NUMBER = "nr";

    public short type;
    public short number;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(TYPE, NUMBER);
    }
}
