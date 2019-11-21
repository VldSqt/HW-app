package com.os.hardwaremonitor.managers;

import com.os.hardwaremonitor.models.components.*;
import com.os.hardwaremonitor.models.sensors.Fan;
import com.os.hardwaremonitor.models.sensors.Load;
import com.os.hardwaremonitor.models.sensors.Sensors;
import com.os.hardwaremonitor.models.sensors.Temperature;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SensorsManager {

    protected boolean debugMode = false;
    private static final String LABEL = "Label";
    private static final String TEMP = "Temp";
    private static final String FAN = "Fan";
    private static final String LOAD = "Load";
    private static final String CPU = "CPU";
    private static final String GPU = "GPU";
    private static final String DISK = "DISK";
    private static final String MOBO = "MOBO";

    public SensorsManager debugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    protected abstract String getSensorsData();

    //gets each component from raw sensorsdata and adds them to separate lists
    public Components getComponents() {
        List<CPU> cpus = new ArrayList<>();
        List<GPU> gpus = new ArrayList<>();
        List<Disk> disks = new ArrayList<>();
        List<Mobo> mobos = new ArrayList<>();

        String normalizedSensorsData = getSensorsData();

        String[] componentsData = normalizedSensorsData.split("\\[COMPONENT\\]\\r?\\n");

        for(final String componentData : componentsData) {
            if(componentData.startsWith(CPU)) {
                cpus.add(getCPU(componentData));
            } else if (componentData.startsWith(GPU)) {
                gpus.add(getGPU(componentData));
            } else if (componentData.startsWith(DISK)) {
                disks.add(getDisk(componentData));
            } else if (componentData.startsWith(MOBO)) {
                mobos.add(getMobo(componentData));
            }
        }
        return new Components(cpus, gpus, disks, mobos);
    }

    private CPU getCPU(String cpuData) {
        return new CPU(getName(cpuData), getSensors(cpuData));
    }

    private GPU getGPU(String gpuData) {
        return new GPU(getName(gpuData), getSensors(gpuData));
    }

    private Disk getDisk(String diskData) {
        return new Disk(getName(diskData), getSensors(diskData));
    }

    private Mobo getMobo(String moboData) {
        return new Mobo(getName(moboData), getSensors(moboData));
    }

    //gets name of component from the raw string
    private static String getName(String componentData) {
        String name = null;
        String[] dataLines = componentData.split("\\r?\\n");
        for(final String dataLine: dataLines) {
            if(dataLine.startsWith(LABEL)) {
                name = dataLine.split(":")[1].trim();
                break;
            }
        }
        return name;
    }

    //gets all sensors for each component and formats their value accordingly
    //adds all found sensors to a Sensors.java object and constructs it
    private Sensors getSensors(String componentData) {
        List<Temperature> temperatures = new ArrayList<>();
        List<Fan> fans = new ArrayList<>();
        List<Load> loads = new ArrayList<>();
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        String[] dataLines = componentData.split("\\r?\\n");

        for(final String dataLine : dataLines) {
            try {
                if(dataLine.startsWith(TEMP)) {
                    String[] data = dataLine.split(":");
                    Temperature temperature = new Temperature(data[0].trim(),
                            data[1].trim().length() > 0 ? numberFormat.parse(data[1].trim()).doubleValue() : 0.0);
                    temperatures.add(temperature);
                } else if(dataLine.startsWith(FAN)) {
                    String[] data = dataLine.split(":");
                    Fan fan = new Fan(data[0].trim(),
                            data[1].trim().length() > 0 ? numberFormat.parse(data[1].trim()).doubleValue() : 0.0);
                    fans.add(fan);
                } else if(dataLine.startsWith(LOAD)) {
                    String[] data = dataLine.split(":");
                    Load load = new Load(data[0].trim(),
                            data[1].trim().length() > 0 ? numberFormat.parse(data[1].trim()).doubleValue() : 0.0);
                    loads.add(load);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new Sensors(temperatures, fans, loads);
    }
}

