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

    private final ArrayList<String> pluginNames;
    private ArrayList<Boolean> load;
    private ArrayList<Boolean> driverModeBackup;
    private String robotIP = "10.27.71.2";
    private String customPort = "";
    private boolean snapDash = false;
    private boolean driverMode = false;

    private static Data data = null;

    Data() {
        load = new ArrayList<>();
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

    ArrayList<String> getPluginNames() {
        return pluginNames;
    }

    ArrayList<Boolean> getPluginsToLoad() {
        return load;
    }

    void setPluginLoadable(String name, boolean load) {
        for (String pluginName : pluginNames) {
            if (pluginName.equals(name)) {
                this.load.set(pluginNames.indexOf(name), load);
            }
        }
    }

    void activateDriverMode(boolean activated) {
        if (activated) {
            driverModeBackup = load;
            for (String plugin : pluginNames) {
                if (!"Operator HUD".equals(plugin)) {
                    load.set(pluginNames.indexOf(plugin), false);
                } else {
                    load.set(pluginNames.indexOf(plugin), true);
                }
            }
        } else {
            load = driverModeBackup;
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

    void writeMemory() {
        try (FileOutputStream fileOut = new FileOutputStream("plugins" + File.separator + "dashboard");
                ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(Data.getInstance());
            out.close();
            fileOut.close();
        } catch (IOException e) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    static void loadInfo() {
        File file = new File("plugins" + File.separator + "dashboard");
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

    synchronized void cleanUp(ArrayList<Plugin> plugins, ArrayList<Boolean> pluginsToLoad) {
        pluginNames.clear();
        for (Plugin plugin : plugins) {
            pluginNames.add(plugin.pluginName());
        }
        load = pluginsToLoad;
    }
}
