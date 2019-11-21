package com.os.hardwaremonitor.managers.windows.powershell;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerShellOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(PowerShellOperations.class);

    private PowerShellOperations() {}

    //uses powershell to detect if user is administrator
    public static boolean isAdministrator() {
        String command = "([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] \"Administrator\")";
        return "true".equalsIgnoreCase(PowerShell.executeSingleCommand(command).getCommandOutput());
    }

    // tries to open a new powershell session and get the raw sensors data as a string
    public static String getRawSensorsData() {
        PowerShell powershell = null;
        String rawData = null;
        try {
            powershell = PowerShell.openSession();
            rawData = powershell.executeScript(PowerShellScriptHelper.generateScript()).getCommandOutput();
        } catch (PowerShellNotAvailableException ex) {
            LOGGER.error("Cannot find PowerShell in your system. Please install it.", ex);
        } finally {
            if (powershell != null) {
                powershell.close();
            }
        }
        return rawData;
    }
}
