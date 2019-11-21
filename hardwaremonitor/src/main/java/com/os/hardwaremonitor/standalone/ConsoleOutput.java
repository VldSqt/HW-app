package com.os.hardwaremonitor.standalone;

import com.os.hardwaremonitor.HardwareMonitor;
import com.os.hardwaremonitor.models.components.*;
import com.os.hardwaremonitor.models.sensors.Fan;
import com.os.hardwaremonitor.models.sensors.Load;
import com.os.hardwaremonitor.models.sensors.Temperature;

import java.util.List;
import java.util.Map;

public class ConsoleOutput {

    //generates the console output if the mode is consoleMode
    //gets components from HardwareMonitor class via the config file
    //adds each component to their specific list
    //reads their name and value and prints it to the console
    public static void showOutput(Map<String, String> config) {
        System.out.println("Scanning sensors data...");

        Components components = HardwareMonitor.get.config(config).components();

        List<CPU> cpus = components.cpus;
        if (cpus != null) {
            for (final CPU cpu : cpus) {
                System.out.println("Found CPU component: " + cpu.name);
                readComponent(cpu);
            }
        }

        List<GPU> gpus = components.gpus;
        if (gpus != null) {
            for (final GPU gpu : gpus) {
                System.out.println("Found GPU component: " + gpu.name);
                readComponent(gpu);
            }
        }

        List<Disk> disks = components.disks;
        if (disks != null) {
            for (final Disk disk : disks) {
                System.out.println("Found disk component: " + disk.name);
                readComponent(disk);
            }
        }
    }

    // Read component values in standalone mode
    private static void readComponent(Component component) {
        if (component.sensors != null) {
            System.out.println("Sensors: ");

            List<Temperature> temps = component.sensors.temperatures;
            for (final Temperature temp : temps) {
                System.out.println(temp.name + ": " + temp.value + " C");
            }

            List<Fan> fans = component.sensors.fans;
            for (final Fan fan : fans) {
                System.out.println(fan.name + ": " + fan.value + " RPM");
            }

            List<Load> loads = component.sensors.loads;
            for (final Load load : loads) {
                System.out.println(load.name + ": " + load.value);
            }
        }
    }
}
