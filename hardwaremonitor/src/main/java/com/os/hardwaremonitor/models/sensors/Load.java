package com.os.hardwaremonitor.models.sensors;

/**
 * load speed
 */
public class Load {

    public final String name;
    public final Double value;

    public Load(String name, Double value) {
        this.name = name;
        this.value = value;
    }
}
