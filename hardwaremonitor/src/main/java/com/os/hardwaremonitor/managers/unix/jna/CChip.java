package com.os.hardwaremonitor.managers.unix.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class CChip extends Structure {

    /**
     *C-Chip - electrical connection on pins that are used
     *to select an "address" in the main memory and
     *another set of pins to read and write the data stored at that location.
     */

    public static final String PREFIX = "prefix";
    public static final String BUS = "bus";
    public static final String PATH = "path";
    public static final String ADDRESS = "addr";

    public String prefix;
    public CBus bus;
    public String path;
    public int address;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(PREFIX, BUS, PATH, ADDRESS);
    }
}
