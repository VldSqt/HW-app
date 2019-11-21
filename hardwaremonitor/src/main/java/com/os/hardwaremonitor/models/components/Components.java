package com.os.hardwaremonitor.models.components;

import java.util.List;

/**
 * class for object comprising list of total components
 */
public class Components {

    public final List<CPU> cpus;
    public final List<GPU> gpus;
    public final List<Disk> disks;
    public final List<Mobo> mobos;

    public Components(List<CPU> cpus, List<GPU> gpus, List<Disk> disks, List<Mobo> mobos) {
        this.cpus = cpus;
        this.gpus = gpus;
        this.disks = disks;
        this.mobos = mobos;
    }
}
