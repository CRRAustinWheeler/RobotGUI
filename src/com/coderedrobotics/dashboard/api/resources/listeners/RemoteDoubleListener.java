package com.coderedrobotics.dashboard.api.resources.listeners;

import com.coderedrobotics.dashboard.api.resources.RemoteDouble;

/**
 *
 * @author Michael
 */
public interface RemoteDoubleListener {
    
     /**
     * Called when the value of the RemoteDouble is updated.
     *
     * @param value new value
     * @param sender the RemoteDouble instance who sent the update
     */
    public void update(double value, RemoteDouble sender);
}
