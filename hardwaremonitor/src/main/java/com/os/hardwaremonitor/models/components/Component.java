package com.os.hardwaremonitor.models.components;

import com.os.hardwaremonitor.models.sensors.Sensors;

/**
 * abstract class for general component
 */
public abstract class Component {

    public final String name;
    public final Sensors sensors;

    Component(String name, Sensors sensors) {
        this.name = name;
        this.sensors = sensors;
    }
}
