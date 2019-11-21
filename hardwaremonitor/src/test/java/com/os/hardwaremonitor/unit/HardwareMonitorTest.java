package com.os.hardwaremonitor.unit;

import com.os.hardwaremonitor.HardwareMonitor;
import com.os.hardwaremonitor.models.components.CPU;
import com.os.hardwaremonitor.models.components.Disk;
import com.os.hardwaremonitor.models.components.GPU;
import com.os.hardwaremonitor.models.sensors.Fan;
import com.os.hardwaremonitor.models.sensors.Temperature;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HardwareMonitorTest {

    private Logger logger = LoggerFactory.getLogger(HardwareMonitorTest.class);

    private static final String TESTSET_DIR = "testsets/normalized/";

    private static final String TESTSET_1 = TESTSET_DIR + "testset_1.jsensor";
    private static final String TESTSET_2 = TESTSET_DIR + "testset_2.jsensor";
    private static final String TESTSET_3 = TESTSET_DIR + "testset_3.jsensor";

    private static final List<String> TESTSET_LIST = Arrays.asList(TESTSET_1, TESTSET_2, TESTSET_3);

    public HardwareMonitorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
    }

    @After
    public void tearDown() {
    }

    private HardwareMonitor getJSensorsStub(String testset) throws IOException {
        Map<String, String> config = new HashMap<String, String>();

        config.put("testMode", "STUB");

        InputStream is = HardwareMonitorTest.class.getClassLoader().getResourceAsStream(testset);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            config.put("stubContent", sb.toString());
        } finally {
            br.close();
        }

        return HardwareMonitor.get.config(config);
    }

    /**
     * Test Cpu information
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCpu() throws Exception {
        logger.info("Testing CPU sensors");

        for (final String testset : TESTSET_LIST) {
            // Get CPU component
            List<CPU> cpus = getJSensorsStub(testset).components().cpus;

            assertNotNull("Cannot recover CPU data", cpus);
            assertTrue("No CPUs found", cpus.size() > 0);

            CPU cpu = cpus.get(0);
            assertNotNull("Cannot recover CPU data", cpu);

            assertNotNull("No CPU name", cpu.name);
            logger.info("CPU name: " + cpu.name);

            // Test temperature sensors (in C)
            for (final Temperature temp : cpu.sensors.temperatures) {
                assertNotNull("Temperature should not be null", temp);
                assertNotNull("Temperature value should not be null", temp.value);
                assertTrue("Temperature value should be greater than 0, but was " + temp.value, temp.value > 0);
                assertTrue("Temperature value should be lower than 120, but was " + temp.value, temp.value < 120);
                logger.info("Temperature: " + temp.value);
            }

            // Test Fan speed sensors (in RPM)
            for (final Fan fan : cpu.sensors.fans) {
                assertNotNull("Fan should not be null", fan);
                assertNotNull("Fan RPM should not be null", fan.value);
                assertTrue("Fan RPM should be greater than 0, but was " + fan.value, fan.value > 0);
                assertTrue("Fan RPM value should be lower than 5000, but was " + fan.value, fan.value < 5000);
                logger.info("Fan RPM: " + fan.value);
            }
        }
    }

    /**
     * Test Gpu information
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGpu() throws Exception {
        logger.info("Testing GPU sensors");

        for (final String testset : TESTSET_LIST) {
            // Get CPU component
            List<GPU> gpus = getJSensorsStub(testset).components().gpus;

            assertNotNull("Cannot recover GPU data", gpus);
            assertTrue("No GPUs found", gpus.size() > 0);

            GPU gpu = gpus.get(0);
            assertNotNull("Cannot recover GPU data", gpu);

            assertNotNull("No GPU name", gpu.name);
            logger.info("GPU name: " + gpu.name);

            // Test temperature sensors (in C)
            for (final Temperature temp : gpu.sensors.temperatures) {
                assertNotNull("Temperature should not be null", temp);
                assertNotNull("Temperature value should not be null", temp.value);
                assertTrue("Temperature value should be greater than 0, but was " + temp.value, temp.value > 0);
                assertTrue("Temperature value should be lower than 120, but was " + temp.value, temp.value < 120);
                logger.info("Temperature: " + temp.value);
            }

            // Test Fan speed sensors (in RPM)
            for (final Fan fan : gpu.sensors.fans) {
                assertNotNull("Fan should not be null", fan);
                assertNotNull("Fan RPM should not be null", fan.value);
                assertTrue("Fan RPM should be greater than 0, but was " + fan.value, fan.value > 0);
                assertTrue("Fan RPM value should be lower than 5000, but was " + fan.value, fan.value < 5000);
                logger.info("Fan RPM: " + fan.value);
            }
        }
    }

    /**
     * Test Disk information
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDisk() throws Exception {
        logger.info("Testing CPU sensors");

        for (final String testset : TESTSET_LIST) {
            // Get Disk component
            List<Disk> disks = getJSensorsStub(testset).components().disks;

            assertNotNull("Cannot recover Disk data", disks);
            assertTrue("No Disks found", disks.size() > 0);

            Disk disk = disks.get(0);
            assertNotNull("Cannot recover Disk data", disk);

            assertNotNull("No Disk name", disk.name);
            logger.info("Disk name: " + disk.name);

            // Test temperature sensors (in C)
            for (final Temperature temp : disk.sensors.temperatures) {
                assertNotNull("Temperature should not be null", temp);
                assertNotNull("Temperature value should not be null", temp.value);
                assertTrue("Temperature value should be greater than 0, but was " + temp.value, temp.value > 0);
                assertTrue("Temperature value should be lower than 120, but was " + temp.value, temp.value < 120);
                logger.info("Temperature: " + temp.value);
            }

            // Test Fan speed sensors (in RPM)
            for (final Fan fan : disk.sensors.fans) {
                assertNotNull("Fan should not be null", fan);
                assertNotNull("Fan RPM should not be null", fan.value);
                assertTrue("Fan RPM should be greater than 0, but was " + fan.value, fan.value > 0);
                assertTrue("Fan RPM value should be lower than 5000, but was " + fan.value, fan.value < 5000);
                logger.info("Fan RPM: " + fan.value);
            }
        }
    }
}
