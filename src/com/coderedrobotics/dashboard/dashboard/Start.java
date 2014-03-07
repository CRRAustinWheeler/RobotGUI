package com.coderedrobotics.dashboard.dashboard;

import com.coderedrobotics.dashboard.api.Plugin;
import com.coderedrobotics.dashboard.api.PluginGuiTab;
import com.coderedrobotics.dashboard.communications.ServerSock;
import com.coderedrobotics.dashboard.communications.CommunicationsThread;
import com.coderedrobotics.dashboard.communications.DataStreamingModule;
import com.coderedrobotics.dashboard.communications.SynchronizedRegisterArray;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * This class contains the main method, and loads plugins into the program.
 *
 * @author Michael
 */
class Start {

    static ArrayList<Plugin> plugins;

    private static MainGUI gui;
    private static LoadingScreen loading;

    static DataStreamingModule dataStreamingModule;
    static ServerSock serverSock;
    static CommunicationsThread communicationsThread;
    static SynchronizedRegisterArray synchronizedRegisterArray;

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

        loading = new LoadingScreen();
        loading.setVisible(true);

        Debug.println("[API] Starting Dashboard...", Debug.STANDARD);

        plugins = new ArrayList<>();
        gui = new MainGUI();

        Debug.println("[API] Setting up security and loading plugins...", Debug.STANDARD);
        Start main = new Start();
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
    public Start() {
        loading.setProgress(5);
        init();
        loading.setProgress(20);
        loadPlugins();
        loading.setProgress(100);
    }

    private void loadPlugins() {
        Policy.setPolicy(new PluginPolicy());
        System.setSecurityManager(new SecurityManager());
        File dir = new File("plugins");
        if (dir.exists() && dir.isDirectory()) {
            String[] files = dir.list();
            int completedPlugins = 0;
            for (String file : files) {
                try {
                    if (!file.endsWith(".jar")) {
                        continue;
                    }

                    File jarFile = new File("plugins" + File.separator + file);
                    ClassLoader loader = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()});
                    final Plugin plugin = (Plugin) loader.loadClass("plugin.Init").newInstance();
                    try {
                        plugin.load(dataStreamingModule, synchronizedRegisterArray);
                    } catch (Exception e) {
                        Debug.println("[API] An error occured loading plugin: " + plugin.pluginName(), Debug.WARNING);
                        Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, e);
                        JOptionPane.showMessageDialog(gui, "An error occured while loading the plugin: " + plugin.pluginName(), "Load Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    plugins.add(plugin);

                    if (plugin.createGUI()) {
                        PluginGuiTab[] tabs = plugin.getGuiTabs();
                        for (PluginGuiTab tab : tabs) {
                            if (tab.display) {
                                gui.addPluginGUI(tab.title, tab.tab);
                            }
                        }
                    }

                    gui.pinfo.addPlugin(plugin);
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

                    Debug.println("[API] Loaded plugin: " + plugin.pluginName()
                            + " with " + plugin.getGuiTabs().length + " GUI tabs.", Debug.STANDARD);
                } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    System.err.println(ex);
                }
                completedPlugins++;
                loading.setProgress(20 + (int) (80 * completedPlugins / files.length));
            }

            gui.pinfo.list.setSelectedIndex(0);

            if (plugins.isEmpty()) {
                JOptionPane.showMessageDialog(gui, "No plugins were found.  Please verify that there a"
                        + "re valid plugins located in " + dir.getAbsolutePath() + ".", "Load Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            
            for (Plugin plugin : plugins){
                if ("Operator HUD".equals(plugin.pluginName())) {
                    gui.setOpenTab(plugins.indexOf(plugin) + 1);
                }
            }

        } else {
            dir.mkdirs();
            JOptionPane.showMessageDialog(gui, "No plugins were found.  Please verify that there a"
                    + "re valid plugins located in the\"plugins\" folder.", "Load Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void init() {
//        new virtualrobot.VirtualRobot();
        dataStreamingModule = new DataStreamingModule();
        serverSock = new ServerSock();
        synchronizedRegisterArray = new SynchronizedRegisterArray();
        communicationsThread = new CommunicationsThread(
                serverSock,
                synchronizedRegisterArray,
                dataStreamingModule);
    }

    /**
     * Calls the Unload method in a plugins Init.java file. Displays a
     * JOptionPane if exceptions are caught.
     */
    static void unloadPlugins() {
        Debug.println("[API] Unloading Plugins...", Debug.STANDARD);
        for (Plugin plugin : plugins) {
            try {
                plugin.unload();
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
}
