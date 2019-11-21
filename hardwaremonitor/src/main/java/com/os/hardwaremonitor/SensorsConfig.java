package com.os.hardwaremonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

final class SensorsConfig {

    /**
     * loads the config file from the classpath
     * uploads the config file to the constructor in HardwareMonitor
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorsConfig.class);

    private static final String CONFIG_FILENAME = "hardwaremonitor.properties";
    private static Properties config;

    private SensorsConfig() {}

    private static Properties getConfig() {
        if (config == null) {
            config = new Properties();
            try {
                // load a properties file from class path, inside static method
                config.load(SensorsConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME));
            } catch (IOException ex) {
                LOGGER.error("Cannot load config file " + CONFIG_FILENAME, ex);
            }
        }
        return config;
    }

    /**
     *puts the configuration name and value in a HashMap in order to send it to the HardwareMonitor
     *constructor
     */
    static Map<String, String> getConfigMap() {
        Map<String, String> returnMap = new HashMap<>();
        Properties configProps = getConfig();

        for (final String propertyName : configProps.stringPropertyNames()) {
            returnMap.put(propertyName, configProps.getProperty(propertyName));
        }
        return returnMap;
    }
}

