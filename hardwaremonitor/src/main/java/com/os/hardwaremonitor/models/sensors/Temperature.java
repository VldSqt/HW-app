package com.os.hardwaremonitor.models.sensors;

public class Temperature {

    public final String name;
    public final Double value;

    public Temperature(String name, Double value) {
        this.name = name;
        this.value = value;
    }
}
