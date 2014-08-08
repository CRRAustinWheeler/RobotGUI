package com.coderedrobotics.dashboard.communications.listeners;

/**
 * Objects implementing the MultiplexingListener interface can be alerted when a
 * Subsocket is added, removed, multiplexed, or de-multiplexed.
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 * @see com.coderedrobotics.dashboard.communications.MultiplexingAlerts
 */
public interface MultiplexingListener {

    /**
     * Called when a Subsocket becomes multiplexed.
     *
     * @param route the route on which the event took place
     */
    public void multiplexingEnabled(String route);

    /**
     * Called when a Subsocket is no longer multiplexed.
     *
     * @param route the route on which the event took place
     */
    public void multiplexingDisabled(String route);

    /**
     * Called when a new Subsocket is created.
     *
     * @param route the route of the new Subsocket
     */
    public void routeAdded(String route);

    /**
     * Called when a Subsocket is deleted.
     *
     * @param route the route of the deleted Subsocket.
     */
    public void routeRemoved(String route);
}
