package com.os.hardwaremonitor.managers.unix.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class CSubFeature extends Structure {

    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String TYPE = "type";
    public static final String MAPPING = "mapping";
    public static final String FLAGS = "flags";

    public String name;
    public int number;
    public int type;
    public int mapping;
    public int flags;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(NAME, NUMBER, TYPE, MAPPING, FLAGS);
    }
}
