package com.coderedrobotics.dashboard.api.resources;

import com.coderedrobotics.dashboard.api.resources.listeners.RemoteDoubleListener;
import com.coderedrobotics.dashboard.communications.Connection;
import com.coderedrobotics.dashboard.communications.PrimitiveSerializer;
import com.coderedrobotics.dashboard.communications.Subsocket;
import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.listeners.ConnectionListener;
import com.coderedrobotics.dashboard.communications.listeners.SubsocketListener;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class RemoteDouble implements SubsocketListener, ConnectionListener {

    private double val;
    private MODE mode;

    Subsocket subsocket;
    ArrayList<RemoteDoubleListener> listeners = new ArrayList<>();

    public enum MODE {

        REMOTE, LOCAL
    }

    private RemoteDouble(String subsocketPath, MODE mode, double initialValue) throws InvalidRouteException {
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
        
    public RemoteDouble(String subsocketPath, double initialValue) throws InvalidRouteException {
        this(subsocketPath, MODE.REMOTE, initialValue);
    }

    public RemoteDouble(String subsocketPath, MODE mode) throws InvalidRouteException {
        this(subsocketPath, mode, 0);
    }

    public RemoteDouble(String subsocketPath) throws InvalidRouteException {
        this(subsocketPath, MODE.REMOTE, 0);
    }

    private void updateValue(double value) {
        val = value;
        for (RemoteDoubleListener rdl : listeners) {
            rdl.update(value, this);
        }
    }

    public void addListener(RemoteDoubleListener rdl) {
        listeners.remove(rdl);
        listeners.add(rdl);
    }

    public void removeListener(RemoteDoubleListener rdl) {
        listeners.remove(rdl);
    }

    public void setValue(double value) throws InvalidModeException {
        if (mode == MODE.REMOTE) {
            updateValue(value);
            update();
        }
    }

    public double getValue() {
        return val;
    }

    private void update() {
        if (mode == MODE.REMOTE) {
            subsocket.sendData(PrimitiveSerializer.toByteArray(val));
        }
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

    @Override
    public void incomingData(byte[] data, Subsocket subsocket) {
        if (subsocket == this.subsocket) {
            updateValue(PrimitiveSerializer.bytesToDouble(data));
        }
    }

    public String getSubsocketPath() {
        return subsocket.mapCompleteRoute();
    }

    public class InvalidModeException extends Exception {

    }
}
