package com.os.hardwaremonitor;

import com.os.hardwaremonitor.models.components.Components;
import com.os.hardwaremonitor.standalone.ConsoleOutput;
import com.os.hardwaremonitor.standalone.GUIOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public enum HardwareMonitor {
    get;
    private static final Logger LOGGER = LoggerFactory.getLogger(HardwareMonitor.class);
    final Map<String, String> baseConfig;
    private Map<String, String> usedConfig = null;

    HardwareMonitor() {
        // Load config from file
        baseConfig = SensorsConfig.getConfigMap();
    }

    /**
     * Updates default config (configuration on hardwaremonitor.properties) with a new one defined in GUIOutput and ConsoleOutput
     * @param config - maps that contains the new config values
     * @return {@link HardwareMonitor} instance
     */
    public HardwareMonitor config(Map<String, String> config) {
        // Initialize config if necessary
        if (this.usedConfig == null) {
            this.usedConfig = this.baseConfig;
        }

        // Override values
        for (final Map.Entry<String, String> entry : config.entrySet()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Overriding config entry %s, %s by %s", entry.getKey(),
                        this.usedConfig.get(entry.getKey()), entry.getValue()));
            }
            this.usedConfig.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Retrieve all sensors components values. The supported sensors types are:
     * Fan: fan speed<
     * Load: component load %
     * Temperature: temperature of sensor in C(Celsius) or F(Fahrenheit) depending on system settings
     */
    public Components components() {
        if (this.usedConfig == null) {
            this.usedConfig = new HashMap<>();
        }
        Components components = SensorsLocator.get.getComponents(this.usedConfig);

        // Reset config
        this.usedConfig = this.baseConfig;
        return components;
    }

    /**
     * Standalone entry point
     * Based on the argument given, the application will start either in GUI mode, debug mode or console output mode
     */
    public static void main(String[] args) {
        boolean guiMode = false;
        Map<String, String> overriddenConfig = new HashMap<>();
        for (final String arg : args) {
            if ("--debug".equals(arg)) {
                overriddenConfig.put("debugMode", "true");
            }
            if ("--gui".equals(arg)) {
                guiMode = true;
            }
        }

        if (guiMode) {
            GUIOutput.showOutput(overriddenConfig);
        } else {
            ConsoleOutput.showOutput(overriddenConfig);
        }
    }
}
