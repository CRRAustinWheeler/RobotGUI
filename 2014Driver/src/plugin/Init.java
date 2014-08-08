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

    Driver driver;
    Settings settings;

    @Override
    public void init() {
        driver = new Driver();
        settings = new Settings();
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
        return settings;
    }

    @Override
    public boolean createGUI() {
        return true;
    }

    @Override
    public PluginGUITab[] getGUITabs() {
        PluginGUITab[] gui = {new PluginGUITab("Driver", driver, true)};
        return gui;
    }

    @Override
    public String pluginName() {
        return "Operator HUD";
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
        return "The Operator HUD Plugin is custom made by Code Red Robotics "
                + "every year to fit the current game, and our robot design.  "
                + "It relays important information and graphs to our Robot "
                + "Operators on the field.  Even though it is written to fit "
                + "the needs of our robot, The Operator HUD plugin comes bund"
                + "led with the Dashboard essentials pack, to reference as an "
                + "example if desired.  ";
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
}
