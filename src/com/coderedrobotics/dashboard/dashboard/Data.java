package com.coderedrobotics.dashboard.dashboard;

import com.coderedrobotics.dashboard.api.Plugin;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
class Data implements Serializable {

    private ArrayList<String> pluginNames;
    private ArrayList<String> driverModeBackup;
    private ArrayList<String> dontLoadThesePluginIDs;
    private String robotIP = "10.27.71.2";
    private String customPort = "";
    private boolean snapDash = false;
    private boolean driverMode = false;
    private String pluginsPath= "plugins";

    private static Data data = null;

    Data() {
        dontLoadThesePluginIDs = new ArrayList<>();
        pluginNames = new ArrayList<>();
    }

    static Data getInstance() {
        if (data == null) {
            data = new Data();
        }
        return data;
    }

    static void setInstance(Data data) {
        Data.data = data;
    }
    
    void setPlugins(ArrayList<Plugin> plugins) {
        pluginNames.clear();
        for (Plugin plugin : plugins) {
            pluginNames.add(plugin.pluginName());
        }
    }

    ArrayList<String> getPluginsToNOTLoad() {
        return dontLoadThesePluginIDs;
    }

    synchronized void setLoadPlugin(String id, boolean load) {
        this.dontLoadThesePluginIDs.remove(id); // no duplicates, plus we might remove anyway
        if (!load) {
            this.dontLoadThesePluginIDs.add(id);
        }
    }

    void activateDriverMode(boolean activated) {
        if (activated) {
            driverModeBackup = (ArrayList<String>) (dontLoadThesePluginIDs.clone());
            dontLoadThesePluginIDs.clear();
            for (String name : pluginNames) {
                System.out.println("[DEBUG] DriverMode name: " + name);
                if (!"Operator HUD".equals(name)) {
                    dontLoadThesePluginIDs.add(Start.getPlugin(name).pluginID());
                    System.out.println("[DEBUG] DriverMode found non match: " + name);
                }
            }
        } else {
            dontLoadThesePluginIDs = (ArrayList<String>) (driverModeBackup.clone());
        }
        driverMode = activated;
    }
    
    boolean isDriverModeActivated() {
        return driverMode;
    }

    String getRobotIP() {
        return robotIP;
    }

    void setRobotIP(String robotIP) {
        this.robotIP = robotIP;
    }

    String getCustomPort() {
        return customPort;
    }

    void setCustomPort(String customPort) {
        this.customPort = customPort;
    }

    boolean isSnapDash() {
        return snapDash;
    }

    void setSnapDash(boolean snapDash) {
        this.snapDash = snapDash;
    }

    public String getPluginsPath() {
        return pluginsPath;
    }

    public void setPluginsPath(String pluginsPath) {
        this.pluginsPath = pluginsPath;
    }

    void writeMemory() {
        try (FileOutputStream fileOut = new FileOutputStream("dashboard");
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(Data.getInstance());
            out.close();
            fileOut.close();
        } catch (IOException e) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    static void loadInfo() {
        File file = new File("dashboard");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileInputStream fileIn = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileIn)) {
                Data.setInstance((Data) in.readObject());
            }
        } catch (InvalidClassException | EOFException ex) {
            try {
                file.delete();
                file.createNewFile();
                getInstance().writeMemory();
            } catch (IOException ex1) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
