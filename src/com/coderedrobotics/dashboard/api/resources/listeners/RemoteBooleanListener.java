package com.coderedrobotics.dashboard.api.resources.listeners;

import com.coderedrobotics.dashboard.api.resources.RemoteBoolean;

/**
 *
 * @author Michael
 */
public interface RemoteBooleanListener {

    /**
     * Called when the value of the RemoteBoolean is updated.
     *
     * @param value new value
     * @param sender the RemoteBoolean instance who sent the update
     */
    public void update(boolean value, RemoteBoolean sender);
}
