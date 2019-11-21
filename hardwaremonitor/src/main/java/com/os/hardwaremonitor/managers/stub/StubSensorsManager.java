package com.os.hardwaremonitor.managers.stub;

import com.os.hardwaremonitor.managers.SensorsManager;

public class StubSensorsManager extends SensorsManager {

    private final String stubContent;

    public StubSensorsManager(String stubContent) {
        this.stubContent = stubContent;
    }

    @Override
    protected String getSensorsData() {
        return null;
    }
}
