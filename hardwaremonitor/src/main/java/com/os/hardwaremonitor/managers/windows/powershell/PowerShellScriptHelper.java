package com.os.hardwaremonitor.managers.windows.powershell;

import com.os.hardwaremonitor.utilities.SensorsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class PowerShellScriptHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PowerShellOperations.class);
    private static final String LINE_BREAK = "\r\n";

    private PowerShellScriptHelper(){}

    //gets the OpenHardwareMonitorLib.dll file from the /lib/win/ path
    private static String dllImport() {
        return "[System.Reflection.Assembly]::LoadFile(\""
                + SensorsUtils.generateLibTmpPath("/lib/win/", "OpenHardwareMonitorLib.dll") + "\")" + " | Out-Null"
                + LINE_BREAK;
    }

    //constructs a new computer instance for generating the needed script used to get data from powershell
    private static String newComputerInstance() {
        return "$PC = New-Object OpenHardwareMonitor.Hardware.Computer" + LINE_BREAK +
                "$PC.MainboardEnabled = $true" + LINE_BREAK +
                "$PC.CPUEnabled = $true" + LINE_BREAK +
                "$PC.RAMEnabled = $true" + LINE_BREAK +
                "$PC.GPUEnabled = $true" + LINE_BREAK +
                "$PC.FanControllerEnabled = $true" + LINE_BREAK +
                "$PC.HDDEnabled = $true" + LINE_BREAK;
    }

    //constructs the powershell code for querying each component of the PC for their data
    private static String sensorsQueryLoop() {
        return "try" + LINE_BREAK +
                "{" + LINE_BREAK +
                "$PC.Open()" + LINE_BREAK +
                "}" + LINE_BREAK +
                "catch" + LINE_BREAK +
                "{" + LINE_BREAK +
                "$PC.Open()" + LINE_BREAK +
                "}" + LINE_BREAK +
                "ForEach ($hw in $PC.Hardware)" + LINE_BREAK +
                "{" + LINE_BREAK +
                "$hw" + LINE_BREAK +
                "$hw.Update()" + LINE_BREAK +
                "ForEach ($subhw in $hw.SubHardware)" + LINE_BREAK +
                "{" + LINE_BREAK +
                "$subhw.Update()" + LINE_BREAK +
                "ForEach ($sensor in $subhw.Sensors)" + LINE_BREAK +
                "{" + LINE_BREAK +
                "$sensor" + LINE_BREAK +
                "Write-Host \"\"" + LINE_BREAK +
                "}" + LINE_BREAK +
                "}" + LINE_BREAK +
                "ForEach ($sensor in $hw.Sensors)" + LINE_BREAK +
                "{" + LINE_BREAK +
                "$sensor" + LINE_BREAK +
                "Write-Host \"\"" + LINE_BREAK +
                "}" + LINE_BREAK +
                "}";
    }

    //generates the script for powershell in order to get the sensors data from it
    //the method creates a temporary file in which the big string in stored
    static String generateScript() {
        File tmpFile = null;
        FileWriter writer = null;
        String scriptPath = null;

        try {
            tmpFile = File.createTempFile("jsensors_" + new Date().getTime(), ".ps1");
            tmpFile.deleteOnExit();
            writer = new FileWriter(tmpFile);
            writer.write(getPowerShellScript());
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            LOGGER.error("Cannot create PowerShell script file", ex);
            return "Error";
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ioe) {
                LOGGER.warn("Error when finishing writing Powershell script file", ioe);
            }
        }
        if (tmpFile != null) {
            scriptPath = tmpFile.getAbsolutePath();
        }

        return scriptPath;
    }

    // constructs the final powershell script by using the dll file, a new pc instance and the query
    // used in generateScript
    private static String getPowerShellScript() {
        return dllImport() +
                newComputerInstance() +
                sensorsQueryLoop();
    }
}
