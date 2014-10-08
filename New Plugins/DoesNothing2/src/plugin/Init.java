package plugin;

import com.coderedrobotics.dashboard.api.Plugin;
import com.coderedrobotics.dashboard.api.PluginGUITab;
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

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public void run() {
        // do nothing
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
        return false;
    }

    @Override
    public PluginGUITab[] getGUITabs() {
        return null;
    }

    @Override
    public String pluginName() {
        return "Does Nothing 2";
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
        return "2771CodeRedRobotics-DoesNothing2";
    }
}
