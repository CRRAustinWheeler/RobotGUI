package com.coderedrobotics.dashboard.api.resources.listeners;

import com.coderedrobotics.dashboard.api.resources.RemoteString;

/**
 *
 * @author Michael
 */
public interface RemoteStringListener {

     /**
     * Called when the value of the RemoteString is updated.
     *
     * @param value new value
     * @param sender the RemoteString instance who sent the update
     */
    public void update(String value, RemoteString sender);
}
