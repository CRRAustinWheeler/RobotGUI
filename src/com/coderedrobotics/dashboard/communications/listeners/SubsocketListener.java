package com.coderedrobotics.dashboard.communications.listeners;

import com.coderedrobotics.dashboard.communications.Subsocket;

/**
 * Objects that implement the SubsocketListener interface can listen to incoming
 * data of a given Subsocket.
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 * @see Subsocket#addListener(com.coderedrobotics.dashboard.communications.listeners.SubsocketListener) 
 * @see Subsocket#removeListener(com.coderedrobotics.dashboard.communications.listeners.SubsocketListener) 
 */
public interface SubsocketListener {

    /**
     * Called when data is received from a Subsocket.
     *
     * @param data the data that was received
     * @param subsocket the Subsocket object that the data was received on.
     */
    public void incomingData(byte[] data, Subsocket subsocket);
}
