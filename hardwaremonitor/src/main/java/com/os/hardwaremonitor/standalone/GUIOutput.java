package com.os.hardwaremonitor.standalone;

import com.os.hardwaremonitor.HardwareMonitor;
import com.os.hardwaremonitor.models.components.*;
import com.os.hardwaremonitor.models.components.Component;
import com.os.hardwaremonitor.models.sensors.Fan;
import com.os.hardwaremonitor.models.sensors.Load;
import com.os.hardwaremonitor.models.sensors.Temperature;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GUIOutput {

    //thread for displaying the GUI interface
    public static void showOutput(final Map<String, String> config) {
        EventQueue.invokeLater(() -> {
            JSensorsGUI gui = new GUIOutput().new JSensorsGUI(config);
            gui.setVisible(true);
        });
    }

    //the main GUI frame
    @SuppressWarnings("serial")
    class JSensorsGUI extends JFrame {
        private Map<String, String> config;
        private JTable table = new JTable();

        JSensorsGUI(Map<String, String> config) {
            this.config = config;
            initUI();
        }

        //initializes the GUI window
        private void initUI() {
            setTitle("Hardware Monitor");
            setSize(1200, 1000);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            table.setRowHeight(50);
            table.setFont(new Font("Arial", Font.BOLD, 20));
            new GuiUpdater(this).execute();
        }

        //gets the data from HardwareMonitor class and puts it in a table model
        private DefaultTableModel calculateModel() {
            DefaultTableModel model = new DefaultTableModel(new Object[] { "Sensor Name", "Value" }, 0);
            Components components = HardwareMonitor.get.config(this.config).components();
            java.util.List<CPU> cpus = components.cpus;

            if (cpus != null) {
                for (final CPU cpu : cpus) {
                    model.addRow(new String[] { "CPU component: " + cpu.name });
                    addComponent(cpu, model);
                }
            }

            java.util.List<GPU> gpus = components.gpus;
            if (gpus != null) {
                for (final GPU gpu : gpus) {
                    model.addRow(new String[] { "GPU component: " + gpu.name });
                    addComponent(gpu, model);
                }
            }

            java.util.List<Disk> disks = components.disks;
            if (disks != null) {
                for (final Disk disk : disks) {
                    model.addRow(new String[] { "Disk component: " + disk.name });
                    addComponent(disk, model);
                }
            }

            return model;
        }

        // Read component values in standalone mode
        private void addComponent(Component component, DefaultTableModel model) {
            if (component.sensors != null) {

                java.util.List<Temperature> temps = component.sensors.temperatures;
                for (final Temperature temp : temps) {
                    model.addRow(new String[] { temp.name + ": ", temp.value + " C" });
                }

                java.util.List<Fan> fans = component.sensors.fans;
                for (final Fan fan : fans) {
                    model.addRow(new String[] { fan.name + ": ", fan.value + " RPM" });
                }

                List<Load> loads = component.sensors.loads;
                String measureUnit;
                for (final Load load : loads) {
                    if(load.name.contains("Memory")) {
                        measureUnit = " bytes";
                    } else {
                        measureUnit = " processes/minute";
                    }
                    model.addRow(new String[] { load.name + ": ", load.value + measureUnit });
                }
            }

            model.addRow(new String[] {});
        }

        //updates the GUI window when the values change
        private class GuiUpdater extends SwingWorker<Void, Void> {
            GuiUpdater(JSensorsGUI jSensorsGUI) {
                JScrollPane scrollPane = new JScrollPane(table);
                table.setFillsViewportHeight(true);
                jSensorsGUI.add(scrollPane);
            }

            @Override
            protected Void doInBackground() throws Exception {
                while (true) {
                    table.setModel(calculateModel());
                    Thread.sleep(2000);
                }
            }
        }
    }
}
