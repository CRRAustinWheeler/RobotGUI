package com.coderedrobotics.dashboard.communications.listeners;

/**
 * Objects implementing the ConnectionListener interface can be alerted when the
 * network is connected or disconnected.
 *
 * @author Michael Spoehr
 * @see
 * com.coderedrobotics.dashboard.communications.Connection#addConnectionListener(com.coderedrobotics.dashboard.communications.listeners.ConnectionListener)
 * @see
 * com.coderedrobotics.dashboard.communications.Connection#removeConnectionListener(com.coderedrobotics.dashboard.communications.listeners.ConnectionListener)
 * @since Dash 3.0
 */
public interface ConnectionListener {

    /**
     * Called when the network becomes connected.
     */
    public void connected();

    /**
     * Called when the network becomes disconnected.
     */
    public void disconnected();
}
