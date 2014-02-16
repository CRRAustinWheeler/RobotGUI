package com.coderedrobotics.dashboard.api;

import com.coderedrobotics.dashboard.communications.DataStreamingModule;
import com.coderedrobotics.dashboard.communications.SynchronizedRegisterArray;
import java.net.URL;

/**
 * Interface for the plugin classes.
 *
 * <p>In order for a plugin to be successfully created by a developer, an Init
 * class must be created. This class <b>must</b> be named "Init.java", and be
 * located in the "plugin" package. Then, the developer needs to implement this
 * interface in the
 * <code>Init</code> class. This requires many methods to be overran, all of
 * which are required for the plugin to operate correctly. Please refer to those
 * individual methods to learn what they do and how they work. A sample
 * implementation would be (with only the load and run methods, and the rest
 * omitted):
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
 * <p>Other hooks for plugins may be found in the {@link
 * com.coderedrobotics.dashboard.PluginHooks
 * PluginHooks} class.
 *
 * @author Michael
 *
 * @see #load()
 * @see #run()
 * @see #unload()
 * @see #createGUI()
 * @see #getGuiTabs()
 * @see #pluginName()
 * @see #pluginVersion()
 * @see #pluginAuthor()
 * @see #pluginDescription()
 * @see #pluginURL()
 *
 */
public interface Plugin {

    /*
     * 
     */
    public void load(DataStreamingModule dsm, SynchronizedRegisterArray sra);

    /*
     * 
     */
    public void run();

    /*
     * 
     */
    public void unload();

    /*
     * 
     */
    public boolean createGUI();

    /*
     * 
     */
    public PluginGuiTab[] getGuiTabs();

    /*
     * 
     */
    public String pluginName();

    /*
     * 
     */
    public double pluginVersion();

    /*
     * 
     */
    public String pluginAuthor();

    /*
     * 
     */
    public String pluginDescription();

    /*
     * 
     */
    public URL pluginURL();
}
