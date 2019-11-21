package com.os.hardwaremonitor;

import com.os.hardwaremonitor.managers.SensorsManager;
import com.os.hardwaremonitor.managers.stub.StubSensorsManager;
import com.os.hardwaremonitor.managers.unix.UnixSensorsManager;
import com.os.hardwaremonitor.managers.windows.WindowsSensorsManager;
import com.os.hardwaremonitor.models.components.Components;
import com.os.hardwaremonitor.utilities.OSDetector;

import java.util.Map;

enum SensorsLocator {
    get;

    /**
     * Used in HardwareMonitor.java to load the components of the device
     * @param config  - the configuration file as a key-value map
     * @return a Components object
     */
    Components getComponents(Map<String, String> config) {
        return getManager(config).getComponents();
    }

    /**
     * detects OS and loads specific SensorManager
     */
    private static SensorsManager getManager(Map<String, String> config) {
        boolean debugMode = false;
        if ("true".equals(config.get("debugMode"))) {
            debugMode = true;
        }
        if ("STUB".equals(config.get("testMode"))) {
            return new StubSensorsManager(config.get("stubContent")).debugMode(debugMode);
        }
        if (OSDetector.isWindows()) {
            return new WindowsSensorsManager().debugMode(debugMode);
        } else if (OSDetector.isUnix()) {
            return new UnixSensorsManager().debugMode(debugMode);
        }
        throw new UnsupportedOperationException("Sorry, but your Operating System is not supported by this hardware monitor.");
    }
}
