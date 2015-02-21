package com.coderedrobotics.dashboard.api.resources;

import com.coderedrobotics.dashboard.api.resources.listeners.RemoteBooleanListener;
import com.coderedrobotics.dashboard.communications.Connection;
import com.coderedrobotics.dashboard.communications.PrimitiveSerializer;
import com.coderedrobotics.dashboard.communications.Subsocket;
import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.listeners.ConnectionListener;
import com.coderedrobotics.dashboard.communications.listeners.SubsocketListener;
import com.coderedrobotics.dashboard.dashboard.Debug;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class RemoteBoolean implements SubsocketListener, ConnectionListener {

    private boolean val;
    private MODE mode;

    Subsocket subsocket;
    ArrayList<RemoteBooleanListener> listeners = new ArrayList<>();

    public enum MODE {

        REMOTE, LOCAL
    }

    private RemoteBoolean(String subsocketPath, MODE mode, boolean initialValue) throws InvalidRouteException {
        this.mode = mode;
        try {
            subsocket = Connection.getInstance().getRootSubsocket().enableMultiplexing().createNewRoute(subsocketPath);
            if (mode == MODE.REMOTE) {
                val = initialValue;
            }
            subsocket.addListener(this);
        } catch (NotMultiplexedException ex) {
            // really not possible, but fine java you win
            ex.printStackTrace();
        }
        Connection.addConnectionListener(this);
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

    private void updateValue(boolean value) {
        val = value;
        for (RemoteBooleanListener rbl : listeners) {
            rbl.update(value, this);
        }
    }

    public void addListener(RemoteBooleanListener rbl) {
        listeners.remove(rbl);
        listeners.add(rbl);
    }

    public void removeListener(RemoteBooleanListener rbl) {
        listeners.remove(rbl);
    }

    public void toggleValue() throws InvalidModeException {
        if (mode == MODE.REMOTE) {
            updateValue(!val);
            update();
        }
    }

    public void setValue(boolean value) throws InvalidModeException {
        if (mode == MODE.REMOTE) {
            updateValue(value);
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
            updateValue(PrimitiveSerializer.bytesToBoolean(data));
            Debug.println("BOOLEAN UPDATE: " + val, Debug.EXTENDED);
        }
    }

    public String getSubsocketPath() {
        return subsocket.mapCompleteRoute();
    }

    @Override
    public void connected() {
        if (mode == MODE.REMOTE) {
            update();
        }
    }

    @Override
    public void disconnected() {

    }

    public class InvalidModeException extends Exception {

    }
}
