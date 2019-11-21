package com.os.hardwaremonitor.managers.unix.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class CFeature extends Structure {

    /**
     *used to implement additional features of structures
     *its type will be represented by CFeatureType interface
     */

    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String TYPE = "type";
    public static final String F_SUBF = "first_subfeature";
    public static final String PADDING = "padding";

    public String name;
    public int number;
    public int type;
    public int first_subfeature;
    public int padding;


    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(NAME, NUMBER, TYPE, F_SUBF, PADDING);
    }
}
