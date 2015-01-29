package com.coderedrobotics.dashboard.communications;

import java.util.ArrayList;
import com.coderedrobotics.dashboard.communications.listeners.MultiplexingListener;

/**
 * Alerts MultiplexingListeners to a change in the network tree. Events: Route
 * added, Route removed, Multiplexing Enabled on any route, Multiplexing
 * Disabled on any route. Any object can implement the MultiplexingListener
 * interface, call {@link MultiplexingAlerts#addListener(com.coderedrobotics.dashboard.communications.listeners.MultiplexingListener)}, and receive MultiplexingAlerts.
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class MultiplexingAlerts {

    private static final ArrayList<MultiplexingListener> listeners = new ArrayList<>();
    private static MultiplexingListener connection;

    /**
     * Listen for Multiplexing Alerts.
     *
     * @param listener a listener to receive Multiplexing Alerts.
     */
    public static void addListener(MultiplexingListener listener) {
        listeners.remove(listener);
        listeners.add(listener);
    }

    /**
     * Stop listening for Multiplexing Alerts.
     *
     * @param listener a listener to no longer receive Multiplexing Alerts.
     */
    public static void removeListener(MultiplexingListener listener) {
        listeners.remove(listener);
    }

    static void alertRouteAdded(String route, boolean alertConnectionObject) {
        for (MultiplexingListener listener : listeners) {
            listener.routeAdded(route);
        }
        if (alertConnectionObject && connection != null) {
            connection.routeAdded(route);
        }
    }

    static void alertRouteRemoved(String route, boolean alertConnectionObject) {
        for (MultiplexingListener listener : listeners) {
            listener.routeRemoved(route);
        }
        if (alertConnectionObject && connection != null) {
            connection.routeRemoved(route);
        }
    }

    static void alertMultiplexingEnabled(String route, boolean alertConnectionObject) {
        for (MultiplexingListener listener : listeners) {
            listener.multiplexingEnabled(route);
        }
        if (alertConnectionObject && connection != null) {
            connection.multiplexingEnabled(route);
        }
    }

    static void alertMultiplexingDisabled(String route, boolean alertConnectionObject) {
        for (MultiplexingListener listener : listeners) {
            listener.multiplexingDisabled(route);
        }
        if (alertConnectionObject && connection != null) {
            connection.multiplexingDisabled(route);
        }
    }
    
    static void addConnection(MultiplexingListener listener) {
        connection = listener;
    }
}
