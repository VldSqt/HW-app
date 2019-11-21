package com.os.hardwaremonitor.models.sensors;

/**
 * computer fans
 * value will be measured in RPM - revolutions per minute
 */
public class Fan {

    public final String name;
    public final Double value;

    public Fan(String name, Double value) {
        this.name = name;
        this.value = value;
    }
}
