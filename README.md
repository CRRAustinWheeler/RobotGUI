CRR Custom Dashboard
====================

The Code Red Robotics Custom Dashboard is designed to replace the default dashboard that comes with the Driver Station.  Our Dashboard offers many different and helpful features.

Installation
------------

Installation is very minimal...  Simply download and run Dashboard3.0.jar.  A plugins folder will be created in the same directory.  You can specify a different folder from the Dashboard settings if you like.  The robot side code can be found on our GitHub page.

Features
--------

<h3>Plugins</h3>

Everything is completely modular with our dashboard.  Installing a plugin is as simple as dropping its jar into the plugins folder.

**FEATURES**:
  * Easily integrate a GUI into the dash.  You can create multiple tabs, each with custom titles.
  * Use the brand new network protocol that ships with v3.0.
  * Offer a settings page that shows up in the Options settings of the Dashboard,
  * Plugin vendors can offer information like name, website, and description that show up in the settings menu
  * Plugins get notifications on load, run, and unload.  This allows for custom close actions.

**PLUGIN MANAGEMENT**:
  * Turning on/off plugins is easy, and can be done from within the dash.
  * Enable Driver Mode, which only loads plugins important for driving the robot.
  * Options for plugins are integrated into the settings menu, to avoid confusion.
  * Notifications when plugins throw an unhandled exception.
  * A progress bar is shown on plugin load time

<h3>Network Protocol</h3>

Dashboard 3.0 comes with a brand new backend for communication between the robot and the dash.  It was designed for functionality and efficiency.

**FEATURES**:
  * TCP Multiplexing over a single Socket connection
  * Create up to 2^32 - 2 Virtual Subsockets
  * Efficient Use of bandwidth
  * Complete support for hard disconnects
  * 100% reconnect support
  * Connect/Disconnect Notifications
  * Subsocket Create/Destroy Notifications
  * Create Subsockets in a tree structure
  * Subsockets map to human-readable paths, such as "root.controler1.data"
