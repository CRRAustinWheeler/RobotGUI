package com.coderedrobotics.dashboard.dashboard;

import com.coderedrobotics.dashboard.api.Plugin;
import com.coderedrobotics.dashboard.api.PluginGUITab;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import com.coderedrobotics.dashboard.communications.Connection;

/**
 * This class contains the main method, and loads plugins into the program.
 *
 * @author Michael
 */
class Start {

    static ArrayList<Plugin> plugins;

    private static MainGUI gui;
    private static LoadingScreen loading;

    /**
     * Program main entry point, called by the JVM.
     *
     * @param args The command-line arguments (ignored)
     * @throws Throwable if any error occurs
     */
    public static void main(String[] args) {
        Debug.println("[API] Launching Loading Screen...", Debug.STANDARD);

        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Start.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        loading = new LoadingScreen(); // loading JFrame
        loading.setVisible(true);

        Debug.println("[API] Starting Dashboard...", Debug.STANDARD);

        plugins = new ArrayList<>(); // plugin list
        gui = new MainGUI(); //JFrame

        Debug.println("[API] Setting up security and loading plugins...", Debug.STANDARD);
        Start main = new Start(); // Initialization Manager, will load plugins and start them.
        loading.setVisible(false);
        gui.setVisible(true);

        Debug.println("[API] Dashboard Successfully loaded.", Debug.STANDARD);
    }

    /**
     * Program Initialization, and plugin loading. Sets the Policy, installs a
     * SecurityManager and then loads the plugins located in the "plugins"
     * folder. This constructor has the capability to shut down the program if
     * no plugins are found, or the "plugins" directory wasn't found.
     *
     */
    private Start() {
        loading.setProgress(5);
        init(); // Setup the Connection Thread.
        loading.setProgress(20);
        loadPlugins(); // Load all of the plugins
        loading.setProgress(100);
    }

    private void loadPlugins() {

        //<editor-fold defaultstate="collapsed" desc="Setup Policy Manager">
        Policy.setPolicy(new PluginPolicy());
        System.setSecurityManager(new SecurityManager());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Setup Data">
        Data.loadInfo();
        Data data = Data.getInstance();
        ArrayList<String> idsToNotLoad = data.getPluginsToNOTLoad();

        if ("".equals(data.getRobotIP())) {
            data.setRobotIP("10.27.71.2");
        }

        Connection.getInstance().setAddress(data.getRobotIP(),
                "".equals(data.getCustomPort()) ? 1180 : Integer.parseInt(data.getCustomPort()));

        gui.setSnap(data.isSnapDash());

        PluginsInfo pinfo = gui.getOptionsContainer().getPluginsInfoPanel();
        Options optionsPanel = gui.getOptionsContainer().getOptionsPanel();

        optionsPanel.setRobotIP(data.getRobotIP());
        optionsPanel.setCustomPort(data.getCustomPort());
        optionsPanel.setSnap(data.isSnapDash());
        optionsPanel.setDriverMode(data.isDriverModeActivated());
        optionsPanel.setPluginsPath(data.getPluginsPath());
        //</editor-fold>

        // Go through all the .jar files in the "plugins" folder.
        File dir = new File(data.getPluginsPath());
        if (dir.exists() && dir.isDirectory()) {
            String[] files = dir.list();
            int completedPlugins = 0;
            for (String file : files) {
                try {
                    if (!file.endsWith(".jar")) {
                        continue; // End this loop of the for loop and move on
                    }

                    //<editor-fold defaultstate="collapsed" desc="Load plugin">
                    File jarFile = new File("plugins" + File.separator + file);
                    ClassLoader loader = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()});
                    final Plugin plugin = (Plugin) loader.loadClass("plugin.Init").newInstance();

                    plugins.add(plugin);
                    pinfo.addPlugin(plugin);
                    //</editor-fold>

                    //<editor-fold defaultstate="collapsed" desc="Plugin init(), run(), and GUI creation">
                    try {
                        if (!idsToNotLoad.contains(plugin.pluginID())) {
                            //<editor-fold defaultstate="collapsed" desc="Call Plugin.init()">
                            try {
                                plugin.init();
                            } catch (Exception ex) {
                                Debug.println("[API] An error occured loading plugin: " + plugin.pluginName(), Debug.WARNING);
                                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
                                JOptionPane.showMessageDialog(gui, "An error occured while loading the plugin: " + plugin.pluginName(), "Load Error", JOptionPane.ERROR_MESSAGE);
                                continue;
                            }
                            //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc="Create Plugin GUI Tabs">
                            if (plugin.createGUI()) {
                                PluginGUITab[] tabs = plugin.getGUITabs();
                                for (PluginGUITab tab : tabs) {
                                    if (tab.display) {
                                        gui.addPluginGUI(tab.title, tab.tab);
                                    }
                                }
                            }
                          //</editor-fold>

                            //<editor-fold defaultstate="collapsed" desc="Call Plugin.run() in new Thread">
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        plugin.run();
                                    } catch (Exception e) {
                                        Debug.println("[API] Plugin: " + plugin.pluginName()
                                                + "has encountered an error and may no longer function properly.  ", Debug.STANDARD);
                                        JOptionPane.showMessageDialog(gui, plugin.pluginName()
                                                + " has encountered an error and may no longer function properly.  \n"
                                                + "Please contact the plugin vendor.  \n\n" + e.fillInStackTrace(), "Plugin Exception", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            });
                            t.setName("Plugin Thread: " + plugin.pluginName());
                            t.start();
                            //</editor-fold>
                        }
                    } catch (IndexOutOfBoundsException ex) {

                    }
                    //</editor-fold>
                    try {
                        Debug.println("[API] Loaded plugin: " + plugin.pluginName()
                                + " with " + plugin.getGUITabs().length + " GUI tabs.", Debug.STANDARD);
                    } catch (NullPointerException ex) {
                        Debug.println("[API] Loaded plugin: " + plugin.pluginName()
                                + " with no GUI tabs.", Debug.STANDARD);
                    }
                } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    System.err.println(ex);
                }
                completedPlugins++;
                loading.setProgress(20 + (int) (80 * completedPlugins / files.length));
            }

            gui.addPluginGUI("Options", gui.getOptionsContainer());

            //<editor-fold defaultstate="collapsed" desc="Shutdown if we don't have plugins">
            if (plugins.isEmpty()) {
                loading.setVisible(false);
                JOptionPane.showMessageDialog(gui, "No plugins were found.  Please "
                        + "verify that there are valid plugins located in "
                        + dir.getAbsolutePath() + ".", "Load Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Set open tab">
            gui.setOpenTab(0);
            for (Plugin plugin : plugins) {
                if ("Operator HUD".equals(plugin.pluginName())) {
                    gui.setOpenTab(plugins.indexOf(plugin));
                }
            }
            //</editor-fold>

            data.setPlugins(plugins);
            pinfo.list.setSelectedIndex(0);

        } else {
            dir.mkdirs();
            loading.setVisible(false);
            JOptionPane.showMessageDialog(gui, "No plugins were found.  Please "
                    + "verify that there are valid plugins located in "
                    + dir.getAbsolutePath() + ".", "Load Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void init() {
        Connection.getInstance(); // Start the connection thread
    }

    /**
     * Calls the Unload method in a plugins Init.java file. Displays a
     * JOptionPane if exceptions are caught.
     */
    static void unloadPlugins() {
        Debug.println("[API] Unloading Plugins...", Debug.STANDARD);
        for (Plugin plugin : plugins) {
            try {
                plugin.close();
                Debug.println("[API] Unloaded plugin: " + plugin.pluginName(), Debug.STANDARD);
            } catch (Exception e) {
                Debug.println("[API] An error occured unloading plugin: " + plugin.pluginName(), Debug.STANDARD);
                JOptionPane.showMessageDialog(gui, "An error occured while unloading the plugin: " + plugin.pluginName(), "Unload Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        getPluginList();
    }

    /**
     * Returns the plugin list.
     *
     * @return Returns an ArrayList of Plugins.
     */
    static ArrayList getPluginList() {
        return plugins;
    }

    static Plugin getPlugin(String name) {
        for (Plugin plugin : plugins) {
            if (name.equals(plugin.pluginName())) {
                return plugin;
            }
        }
        return null;
    }
}
