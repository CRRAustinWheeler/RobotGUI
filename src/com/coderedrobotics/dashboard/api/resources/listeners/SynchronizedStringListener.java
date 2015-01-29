package com.coderedrobotics.dashboard.api.resources.listeners;

import com.coderedrobotics.dashboard.api.resources.SynchronizedString;

/**
 *
 * @author Michael
 */
public interface SynchronizedStringListener {

    /**
     * Called when the value of the SynchronizedString is changed.
     *
     * @param value new value
     * @param sender the SynchronizedString that sent the update
     */
    public void update(String value, SynchronizedString sender);
}
