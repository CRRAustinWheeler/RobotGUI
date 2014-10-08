package com.coderedrobotics.dashboard.api;

import java.awt.Component;
import java.io.Serializable;
import java.net.URL;

/**
 * Interface for the plugin classes.
 *
 * <p>
 * In order for a plugin to be successfully created by a developer, an Init
 * class must be created. This class <b>must</b> be named "Init.java", and be
 * located in the "plugin" package. Then, the developer needs to implement this
 * interface in the <code>Init</code> class. This requires many methods to be
 * overran, all of which are required for the plugin to operate correctly.
 * Please refer to those individual methods to learn what they do and how they
 * work. A sample implementation would be (with only the load and run methods,
 * and the rest omitted):
 *
 * <blockquote><pre>
 * package plugin;
 *
 * public class Init implements Plugin {
 *
 *      public void load() {
 *           //Loading Code
 *      }
 *
 *      public void run() {
 *           //Run Code
 *      }
 * }
 * </pre></blockquote>
 *
 * <p>
 * Other hooks for plugins may be found in the {@link
 * com.coderedrobotics.dashboard.PluginHooks
 * PluginHooks} class.
 *
 * @author Michael Spoehr
 *
 * @see #load()
 * @see #run()
 * @see #unload()
 * @see #createGUI()
 * @see #getGuiTabs()
 * @see #getSettingsGUI()
 * @see #pluginName()
 * @see #pluginVersion()
 * @see #pluginAuthor()
 * @see #pluginDescription()
 * @see #pluginURL()
 * @since Dash 2.0
 */
public interface Plugin extends Serializable {

    /**
     * Called when a plugin is first loaded. This method must return or the
     * Dashboard will hang.
     */
    public void init();

    /**
     * Main plugin thread. It is not necessary to create a new thread for a
     * plugin unless multiple threads are desired. The run() method is called
     * once and it doesn't matter if it returns or not. The Plugin API also has
     * Exception handling if the run() method throws an Exception.
     */
    public void run();

    /**
     * Called when the Dashboard is shutting down. This method must return or
     * the Dashboard will hang.
     */
    public void close();

    /**
     * Returns whether or not a plugin wants to create GUI tabs in the
     * Dashboard.
     *
     * @return create GUI tabs
     */
    public boolean createGUI();

    /**
     * Returns the GUI tabs to be created. Can't be null
     *
     * @return Array of GUI Tabs
     */
    public PluginGUITab[] getGUITabs();

    /**
     * Returns the GUI to display when the settings button is pressed while
     * viewing a plugin in the Installed Plugins tab.
     *
     * @return a JFrame to display when the settings button is pressed.
     * @since Dash 3.0
     */
    public Component getSettingsGUI();

    /**
     * The Plugin's name. It is recommended this this not be <code>null</code>.
     *
     * @return the plugin's name
     */
    public String pluginName();

    /**
     * The Plugin's version. It is recommended this this not be
     * <code>null</code>.
     *
     * @return the plugin's version
     */
    public double pluginVersion();

    /**
     * The Plugin's author. It is recommended this this not be
     * <code>null</code>.
     *
     * @return the plugin's author.
     */
    public String pluginAuthor();

    /**
     * The Plugin's description. It is recommended this this not be
     * <code>null</code>.
     *
     * @return the plugin's description.
     */
    public String pluginDescription();

    /**
     * The URL of the vendor's website. It is recommended this this not be
     * <code>null</code>.
     *
     * @return a URL
     */
    public URL pluginURL();
    
    /**
     * The unique ID of the plugin.  This ID must be unique across plugins of 
     * ALL vendors.  Therefore, it is recommended that a plugin's ID consists of:
     * teamnumber + teamname + pluginname.  You may add anything you wish to the end.
     * The ID is never displayed to the user, but is very important to the system.
     * Do not put spaces in the ID.  Do not use these characters: "/", "\", "^", 
     * or "~".  You may use dashes.
     * 
     * @return a String containing the plugin ID.
     */
    public String pluginID();
}
