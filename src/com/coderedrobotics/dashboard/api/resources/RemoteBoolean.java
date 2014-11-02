package com.coderedrobotics.dashboard.api.resources;

import com.coderedrobotics.dashboard.communications.Connection;
import com.coderedrobotics.dashboard.communications.PrimitiveSerializer;
import com.coderedrobotics.dashboard.communications.Subsocket;
import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.listeners.SubsocketListener;

/**
 *
 * @author Michael
 */
public class RemoteBoolean implements SubsocketListener {

    private boolean val;
    private MODE mode;

    Subsocket subsocket;

    public enum MODE {

        REMOTE, LOCAL
    }

    private RemoteBoolean(String subsocketPath, MODE mode, boolean initialValue) throws InvalidRouteException {
        if (mode == MODE.REMOTE) {
            val = initialValue;
        }
        this.mode = mode;
        try {
            if (mode == MODE.LOCAL) {
                subsocket = Connection.getInstance().getRootSubsocket().enableMultiplexing().createNewRoute(subsocketPath);
            } else {
                subsocket = Connection.getInstance().getRootSubsocket().getSubsocket(subsocketPath);
                update();
            }
            subsocket.addListener(this);
        } catch (NotMultiplexedException ex) {
            // really not possible, but fine java you win
        }
    }
    
    public RemoteBoolean(String subsocketPath, boolean initialValue) throws InvalidRouteException {
        this(subsocketPath, MODE.REMOTE, initialValue);
    }

    public RemoteBoolean(String subsocketPath, MODE mode) throws InvalidRouteException {
        this(subsocketPath, mode, false);
    }

    public RemoteBoolean(String subsocketPath) throws InvalidRouteException {
        this(subsocketPath, MODE.REMOTE, false);
    }

    public void toggleValue() throws InvalidModeException {
        if (mode == MODE.REMOTE) {
            val = !val;
            update();
        }
    }

    public void setValue(boolean value) throws InvalidModeException {
        if (mode == MODE.REMOTE) {
            val = value;
            update();
        }
    }

    public boolean getValue() {
        return val;
    }

    private void update() {
        if (mode == MODE.REMOTE) {
            subsocket.sendData(PrimitiveSerializer.toByteArray(val));
        }
    }
    

    @Override
    public void incomingData(byte[] data, Subsocket subsocket) {
        if (subsocket == this.subsocket) {
            val = PrimitiveSerializer.bytesToBoolean(data);
        }
    }
    
    public String getSubsocketPath() {
        return subsocket.mapCompleteRoute();
    }

    public class InvalidModeException extends Exception {

    }
}
