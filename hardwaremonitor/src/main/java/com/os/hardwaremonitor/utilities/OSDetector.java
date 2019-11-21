package com.os.hardwaremonitor.utilities;

//detects the operating system of the current device by querying the java.lang.System class
public class OSDetector {
    private static final String OS = System.getProperty("os.name").toLowerCase();

    OSDetector() {
    }

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public static boolean isSolaris() {
        return OS.contains("sunos");
    }
}