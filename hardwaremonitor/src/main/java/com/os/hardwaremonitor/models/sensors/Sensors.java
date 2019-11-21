package com.os.hardwaremonitor.models.sensors;

import java.util.List;

public class Sensors {

    public final List<Temperature> temperatures;
    public final List<Fan> fans;
    public final List<Load> loads;

    public Sensors(List<Temperature> temperatures, List<Fan> fans, List<Load> loads) {
        this.temperatures = temperatures;
        this.fans = fans;
        this.loads = loads;
    }
}
