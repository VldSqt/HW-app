package com.os.hardwaremonitor.utilities;

import com.os.hardwaremonitor.managers.windows.powershell.PowerShellOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SensorsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PowerShellOperations.class);

    private SensorsUtils() {}

    public static String generateLibTmpPath(String libName) {
        return generateLibTmpPath("/", libName);
    }

    //generates temporary file for holding the helper library in case it
    // can be directly found inside the system by specifying the path and library name
    public static String generateLibTmpPath(String path, String libName) {
        InputStream in = SensorsUtils.class.getResourceAsStream(path + libName);
        File tempFile;
        try {
            tempFile = File.createTempFile(libName, "");
            byte[] buffer = new byte[1024];
            int read;
            FileOutputStream fos = new FileOutputStream(tempFile);
            while ((read = in.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            fos.close();
            in.close();
        } catch (IOException ex) {
            LOGGER.error("Cannot generate temporary file", ex);
            return "";
        }
        return tempFile.getAbsolutePath();
    }
}
