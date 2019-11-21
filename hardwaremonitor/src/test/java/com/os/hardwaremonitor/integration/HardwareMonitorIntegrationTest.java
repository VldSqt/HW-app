package com.os.hardwaremonitor.integration;

import com.os.hardwaremonitor.HardwareMonitor;
import com.os.hardwaremonitor.models.components.Components;
import org.junit.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HardwareMonitorIntegrationTest {

    public HardwareMonitorIntegrationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /*
     * Make a real call and check that components are retrieved
     */
    @Test
    public void testJSensorsRealReturn() {
        Map<String, String> config = new HashMap<>();

        config.put("testMode", "REAL");

        Components components = HardwareMonitor.get.config(config).components();
        assertNotNull("Components is null", components);

        assertTrue("Components lists are not initialised",
                components.cpus != null && components.gpus != null && components.disks != null);
    }
}
