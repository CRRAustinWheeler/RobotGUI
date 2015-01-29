package com.coderedrobotics.dashboard.api.resources.listeners;

import com.coderedrobotics.dashboard.api.resources.SynchronizedBoolean;

/**
 *
 * @author Michael
 */
public interface SynchronizedBooleanListener {
    
    /**
     * Called when the value of the SynchronizedBoolean is changed.
     *
     * @param value new value
     * @param sender the SynchronizedBoolean that sent the update
     */
    public void update(boolean value, SynchronizedBoolean sender);
}
