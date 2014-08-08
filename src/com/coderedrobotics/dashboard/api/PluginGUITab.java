package com.coderedrobotics.dashboard.api;

import javax.swing.JComponent;

/**
 * Class that passes a JComponent, title, and display boolean to the Dashboard
 * plugin loader to be displayed as a tab in the GUI. Multiple tabs may be added
 * by passing an array of these. The attributes of a tab such as the title, the
 * component displayed, and whether or not the tab should be displayed is
 * determined by the variables passed into the constructor of this class.
 *
 * @author Michael Spoehr
 * @since Dash 2.0
 */
public class PluginGUITab {

    public String title;
    public boolean display = true;
    public JComponent tab;

    /**
     * Create a new PluginGUITab
     * 
     * @param title The title that is displayed on a GUI tab.
     * @param tab A JComponent that is added to a GUI tab (such as a JPanel).
     * @param display If <code>true</code>, tab will appear.
     * If <code>false</code>, it will not appear.
     */
    public PluginGUITab(String title, JComponent tab, boolean display) {
        this.title = title;
        this.tab = tab;
        this.display = display;
    }
}
