package com.os.hardwaremonitor.managers.windows;

import com.os.hardwaremonitor.managers.SensorsManager;
import com.os.hardwaremonitor.managers.windows.powershell.PowerShellOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Windows implementation of SensorsManager that gets the sensors by using a
 * PowerShell script and parsing it into a normalized format.
 */
public class WindowsSensorsManager extends SensorsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsSensorsManager.class);

    private static final String LINE_BREAK = "\r\n";
    private static final String COMPONENT_SEPARATOR = "[COMPONENT]";

    //retrieves and normalizes sensors data obtained from powershell
    @Override
    public String getSensorsData() {
        String rawSensorsData = PowerShellOperations.getRawSensorsData();
        if (debugMode) {
            LOGGER.info("RawSensorData: " + rawSensorsData);
        }
        String normalizedSensorsData = normalizeSensorsData(rawSensorsData);
        if (debugMode) {
            LOGGER.info("NormalizeSensorData: " + normalizedSensorsData);
        }
        return normalizedSensorsData;
    }

    /**
     *normalizes sensors data by reading the raw data and splitting and
     *composing each label and value as a new line
     */

    private static String normalizeSensorsData(String rawSensorsData) {
        StringBuilder normalizedSensorsData = new StringBuilder();
        String[] dataLines = rawSensorsData.split("\\r?\\n");
        boolean readingHardLabel = false;
        boolean readingSensor = false;
        for (final String dataLine : dataLines) {
            if (!readingHardLabel && "HardwareType".equals(getKey(dataLine))) {
                String hardwareType = getValue(dataLine);
                if ("CPU".equals(hardwareType)) {
                    normalizedSensorsData.append(COMPONENT_SEPARATOR).append(LINE_BREAK);
                    normalizedSensorsData.append("CPU").append(LINE_BREAK);
                    readingHardLabel = true;
                } else if (hardwareType.toUpperCase().startsWith("GPU")) {
                    normalizedSensorsData.append(COMPONENT_SEPARATOR).append(LINE_BREAK);
                    normalizedSensorsData.append("GPU").append(LINE_BREAK);
                    readingHardLabel = true;
                } else if ("HDD".equals(hardwareType)) {
                    normalizedSensorsData.append(COMPONENT_SEPARATOR).append(LINE_BREAK);
                    normalizedSensorsData.append("DISK").append(LINE_BREAK);
                    readingHardLabel = true;
                } else if ("Mainboard".equals(hardwareType)) {
                    normalizedSensorsData.append(COMPONENT_SEPARATOR).append(LINE_BREAK);
                    normalizedSensorsData.append("MOBO").append(LINE_BREAK);
                    readingHardLabel = false;
                }
                continue;
            }
            if (readingHardLabel) {
                if ("Name".equals(getKey(dataLine))) {
                    normalizedSensorsData.append("Label: ").append(getValue(dataLine)).append(LINE_BREAK);
                    readingHardLabel = false;
                }
            } else {
                readingSensor = addSensorsData(readingSensor, dataLine, normalizedSensorsData);
            }
        }
        return normalizedSensorsData.toString();
    }

    //adds the normalized data to a stringbuilder
    private static boolean addSensorsData(boolean readingSensor, String dataLine, StringBuilder normalizedSensorsData) {
        if ("SensorType".equals(getKey(dataLine))) {
            String sensorType = getValue(dataLine);
            if ("Temperature".equals(sensorType)) {
                normalizedSensorsData.append("Temp ");
                return true;
            } else if ("Fan".equals(sensorType)) {
                normalizedSensorsData.append("Fan ");
                return true;
            } else if ("Load".equals(sensorType)) {
                normalizedSensorsData.append("Load ");
                return true;
            }
        }
        if (readingSensor) {
            if ("Name".equals(getKey(dataLine))) {
                normalizedSensorsData.append(getValue(dataLine)).append(": ");
                return true;
            } else if ("Value".equals(getKey(dataLine))) {
                normalizedSensorsData.append(getValue(dataLine)).append(LINE_BREAK);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    //gets the component label
    private static String getKey(String line) {
        return getData(line, 0);
    }

    //gets the component value
    private static String getValue(String line) {
        return getData(line, 1);
    }

    //gets data from each line in the sensorsdata for a component
    private static String getData(String line, final int index) {
        if (line.contains(":")) {
            return line.split(":", 2)[index].trim();
        }
        return "";
    }
}

