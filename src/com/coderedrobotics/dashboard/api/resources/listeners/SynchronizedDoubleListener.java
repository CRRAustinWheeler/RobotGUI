package com.coderedrobotics.dashboard.api.resources.listeners;

import com.coderedrobotics.dashboard.api.resources.SynchronizedDouble;

/**
 *
 * @author Michael
 */
public interface SynchronizedDoubleListener {

    /**
     * Called when the value of the SynchronizedDouble is changed.
     *
     * @param value new value
     * @param sender the SynchronizedDouble that sent the update
     */
    public void update(double value, SynchronizedDouble sender);
}
