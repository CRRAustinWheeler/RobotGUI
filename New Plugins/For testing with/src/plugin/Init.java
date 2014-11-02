package plugin;

import com.coderedrobotics.dashboard.api.Plugin;
import com.coderedrobotics.dashboard.api.PluginGUITab;
import com.coderedrobotics.dashboard.api.resources.RemoteBoolean;
import com.coderedrobotics.dashboard.communications.Connection;
import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael
 */
public class Init implements Plugin {

    RemoteBoolean b;
    boolean wegood;
    
    GUI gui;

    @Override
    public void init() {
        gui = new GUI();
    }

    @Override
    public void run() {
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public Component getSettingsGUI() {
        return null;
    }

    @Override
    public boolean createGUI() {
        return true;
    }

    @Override
    public PluginGUITab[] getGUITabs() {
        PluginGUITab[] pgt = new PluginGUITab[1];
        pgt[0] = new PluginGUITab("RemoteVarTest", gui, true);
        return pgt;
    }

    @Override
    public String pluginName() {
        return "Does Nothing 3";
    }

    @Override
    public double pluginVersion() {
        return 2;
    }

    @Override
    public String pluginAuthor() {
        return "Code Red Robotics";
    }

    @Override
    public String pluginDescription() {
        return "This plugin does absolutely nothing.";
    }

    @Override
    public URL pluginURL() {
        try {
            return new URL("http://www.coderedrobotics.com/");
        } catch (MalformedURLException ex) {
            Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public String pluginID() {
        return "2771CodeRedRobotics-DoesNothing3";
    }
}
