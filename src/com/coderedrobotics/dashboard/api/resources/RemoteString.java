package com.coderedrobotics.dashboard.api.resources;

import com.coderedrobotics.dashboard.api.resources.listeners.RemoteStringListener;
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
public class RemoteString implements SubsocketListener, ConnectionListener {

    private String val;
    private MODE mode;

    Subsocket subsocket;
    ArrayList<RemoteStringListener> listeners = new ArrayList<>();

    public enum MODE {

        REMOTE, LOCAL
    }

    private RemoteString(String subsocketPath, MODE mode, String initialValue) throws InvalidRouteException {
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
    
    public RemoteString(String subsocketPath, String initialValue) throws InvalidRouteException {
        this(subsocketPath, MODE.REMOTE, initialValue);
    }

    public RemoteString(String subsocketPath, MODE mode) throws InvalidRouteException {
        this(subsocketPath, mode, "");
    }

    public RemoteString(String subsocketPath) throws InvalidRouteException {
        this(subsocketPath, MODE.REMOTE, "");
    }

    private void updateValue(String value) {
        val = value;
        for (RemoteStringListener rsl : listeners) {
            rsl.update(value, this);
        }
    }

    public void addListener(RemoteStringListener rsl) {
        listeners.remove(rsl);
        listeners.add(rsl);
    }

    public void removeListener(RemoteStringListener rsl) {
        listeners.remove(rsl);
    }

    public void setValue(String value) throws InvalidModeException {
        if (mode == MODE.REMOTE) {
            updateValue(value);
            update();
        }
    }

    public String getValue() {
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
            updateValue(PrimitiveSerializer.bytesToString(data));
            Debug.println("STRING UPDATE: " + val, Debug.EXTENDED);
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
