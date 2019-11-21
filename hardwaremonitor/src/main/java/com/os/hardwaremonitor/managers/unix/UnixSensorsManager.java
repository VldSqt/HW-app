package com.os.hardwaremonitor.managers.unix;

import com.os.hardwaremonitor.managers.SensorsManager;
import com.os.hardwaremonitor.managers.unix.jna.CChip;
import com.os.hardwaremonitor.managers.unix.jna.CFeature;
import com.os.hardwaremonitor.managers.unix.jna.CSensors;
import com.os.hardwaremonitor.managers.unix.jna.CSubFeature;
import com.os.hardwaremonitor.utilities.SensorsUtils;
import com.sun.jna.Native;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UnixSensorsManager extends SensorsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnixSensorsManager.class);

    private static final String LINE_BREAK = "\n";
    private static final String SENSORS = "sensors";

    private final StringBuilder sensorsData = new StringBuilder();
    private final StringBuilder sensorsDebugData = new StringBuilder();

    /**
     *main method for retrieving normalized data from the dynamic jna library
     *makes use of the other private methods in this class to fully load the data
     */

    @Override
    public String getSensorsData() {
        CSensors cSensors = loadDynamicLibrary();
        if (cSensors == null) {
            LOGGER.error("Could not load sensors dynamic library");
            return "";
        }
        int init = initCSensors(cSensors);
        if (init != 0) {
            LOGGER.error("Cannot initialize sensors");
            return "";
        }
        String normalizedData;
        try {
            normalizedData = normalizeSensorsData(cSensors);
        } finally {
            cSensors.sensors_cleanup();
        }
        return normalizedData;
    }

    /**
     *loads the JNA library using the CSensors interface from the system
     *if the library cannot be found in the system, it will look for it at /lib/linux/ and get it from there
     */
    private CSensors loadDynamicLibrary() {
        Object jnaProxy;
        try {
            jnaProxy = Native.loadLibrary(SENSORS, CSensors.class);
        } catch (UnsatisfiedLinkError e) {
            LOGGER.info("Cannot find library in system; using embedded library...");
            try {
                String libraryPath = SensorsUtils.generateLibTmpPath("/lib/linux/", "libsensors.so.4.4.0");
                jnaProxy = Native.loadLibrary(libraryPath, CSensors.class);
                new File(libraryPath).delete();
            } catch(UnsatisfiedLinkError e2) {
                jnaProxy = null;
                LOGGER.error("Cannot find sensors dynamic library ", e2);
            }
        }
        return (CSensors) jnaProxy;
    }

    /**
     * initializes sensors using the sensors_init method from jna library
     */
    private static int initCSensors(CSensors cSensors) {
        return cSensors.sensors_init(null);
    }

    /**
     * adds data to sensorsData on a new line
     */
    private void addData(String data) {
        addData(data, true);
    }

    /**
     * adds data to sensorsData String appending the retrieved lines
     */
    private void addData(String data, boolean newLine) {
        String endLine = newLine ? LINE_BREAK : "";
        sensorsData.append(data).append(endLine);
        sensorsDebugData.append(data).append(endLine);
    }

    /**
     * appends the retrieved debug data to the sensorsDebugData stringbuilder
     */
    private void addDebugData(String debugData) {
        sensorsDebugData.append(debugData).append(LINE_BREAK);
    }

    /**
     *normalizes c-sensor retrieved data
     *for each found Chip inside the retrieved data, it appends the type, address, path, prefix and bus type
     *to the stringbuilder
     */
    private String normalizeSensorsData(CSensors cSensors) {
        List<CChip> chips = detectedChips(cSensors);
        for (final CChip chip : chips) {
            addData("[COMPONENT]");
            addDebugData(String.format("Type: %d", chip.bus.type));
            addDebugData(String.format("Address: %d", chip.address));
            addDebugData(String.format("Path: %s", chip.path));
            addDebugData(String.format("Prefix: %s", chip.prefix));
            if (chip.bus != null) {
                switch (chip.bus.type) {
                    case 1:
                        addData("CPU");
                        break;
                    case 2:
                        addData("GPU");
                        break;
                    case 4:
                    case 5:
                        addData("DISK");
                        break;
                    default:
                        addData("UNKNOWN");
                }
            }
            addData(String.format("Label: %s", cSensors.sensors_get_adapter_name(chip.bus)));
            List<CFeature> features = features(cSensors, chip);
            addFeatures(cSensors, chip, features);
        }
        if (debugMode) {
            LOGGER.info(sensorsDebugData.toString());
        }
        return sensorsData.toString();
    }

    /**
     *adds the found features and subfeatures to the data stringbuilder by getting their name and type
     */
    private void addFeatures(CSensors cSensors, CChip chip, List<CFeature> features) {
        for (final CFeature feature : features) {
            addDebugData(String.format("Feature type: %d", feature.type));
            addDebugData(String.format("Feature name: %s", feature.name));
            addDebugData(String.format("Feature label: %s", cSensors.sensors_get_label(chip, feature)));
            if (feature.name.startsWith("temp")) {
                addData(String.format("Temp %s:", cSensors.sensors_get_label(chip, feature)), false);
            } else if (feature.name.startsWith("fan")) {
                addData(String.format("Fan %s:", cSensors.sensors_get_label(chip, feature)), false);
            }
            List<CSubFeature> subFeatures = subFeatures(cSensors, chip, feature);
            addSubFeatures(cSensors, chip, subFeatures);
        }
    }

    private void addSubFeatures(CSensors cSensors, CChip chip, List<CSubFeature> subFeatures) {
        for (final CSubFeature subFeature : subFeatures) {
            addDebugData(String.format("SubFeature type: %d", subFeature.type));
            addDebugData(String.format("SubFeature name: %s", subFeature.name));
            double value = 0.0;
            DoubleByReference pValue = new DoubleByReference(value);
            if (cSensors.sensors_get_value(chip, subFeature.number, pValue) == 0) {
                addDebugData(String.format("SubFeature value: %s", pValue.getValue()));
                if (subFeature.name.endsWith("_input")) {
                    addData(String.format("%s", pValue.getValue()));
                    break;
                }
            } else {
                addData("Could not retrieve value");
            }
        }
    }

    /**
     * detects the chips by using the native jna library method sensors_get_detected_chips
     */
    private static List<CChip> detectedChips(CSensors cSensors) {
        List<CChip> detectedChips = new ArrayList<>();
        CChip foundChip;
        int numSensor = 0;
        while ((foundChip = cSensors.sensors_get_detected_chips(null, new IntByReference(numSensor))) != null) {
            detectedChips.add(foundChip);
            numSensor++;
        }
        return detectedChips;
    }

    /**
     * detects the fetures by using the native jna libary method sensors_get_features
     */
    private static List<CFeature> features(CSensors cSensors, CChip chip) {
        List<CFeature> features = new ArrayList<>();
        CFeature foundFeature;
        int numFeature = 0;
        while ((foundFeature = cSensors.sensors_get_features(chip, new IntByReference(numFeature))) != null) {
            features.add(foundFeature);
            numFeature++;
        }
        return features;
    }

    private static List<CSubFeature> subFeatures(CSensors cSensors, CChip chip, CFeature feature) {
        List<CSubFeature> subFeatures = new ArrayList<>();
        CSubFeature foundSubFeature;
        int numSubFeature = 0;
        while ((foundSubFeature = cSensors.sensors_get_all_subfeatures(chip, feature,
                new IntByReference(numSubFeature))) != null) {
            subFeatures.add(foundSubFeature);
            numSubFeature++;
        }
        return subFeatures;
    }
}
