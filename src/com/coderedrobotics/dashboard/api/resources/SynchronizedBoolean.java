package com.coderedrobotics.dashboard.api.resources;

import com.coderedrobotics.dashboard.api.resources.listeners.SynchronizedBooleanListener;
import com.coderedrobotics.dashboard.communications.Connection;
import com.coderedrobotics.dashboard.communications.PrimitiveSerializer;
import com.coderedrobotics.dashboard.communications.Subsocket;
import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.listeners.ConnectionListener;
import com.coderedrobotics.dashboard.communications.listeners.SubsocketListener;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Michael
 */
public class SynchronizedBoolean implements SubsocketListener, ConnectionListener {

    private boolean val;
    private boolean highPriority;
    private boolean setup = false;
    private boolean setupHP = false;
    private boolean HPwereSAME = false;
    private int thisSideInt;
    private boolean HPotherSIDEwins = false;
    private boolean setupIV = false;

    private int echosWaitingFor = 0;
    private boolean sentVariable;

    Subsocket subsocket;
    private ArrayList<SynchronizedBooleanListener> listeners = new ArrayList<>();

    @SuppressWarnings("LeakingThisInConstructor")
    public SynchronizedBoolean(String subsocketPath, boolean initialValue, boolean highPriority) throws InvalidRouteException {
        val = initialValue;
        this.highPriority = highPriority;
        try {
            subsocket = Connection.getInstance().getRootSubsocket().enableMultiplexing().createNewRoute(subsocketPath);
        } catch (NotMultiplexedException ex) {
            // really not possible, but java wins again
        }
        subsocket.addListener(this);
        Connection.addConnectionListener(this); // TODO: Remove?  
    }

    public SynchronizedBoolean(String subsocketPath, boolean initialValue) throws InvalidRouteException {
        this(subsocketPath, initialValue, false);
    }

    public SynchronizedBoolean(String subsocketPath) throws InvalidRouteException {
        this(subsocketPath, false);
    }

    public synchronized void toggleValue() {
        setValue(!val);
    }

    public synchronized void setValue(boolean value) {
        sentVariable = value;
        echosWaitingFor++;
        update();
    }

    public boolean getValue() {
        return val;
    }

    private void update() {
        subsocket.sendData(PrimitiveSerializer.toByteArray(sentVariable));
    }

    @Override
    public void incomingData(byte[] data, Subsocket subsocket) {
        if (subsocket == this.subsocket) {
            if (!setup) {
                if (!setupHP) {
                    if (!HPwereSAME) {
                        boolean otherSide = PrimitiveSerializer.bytesToBoolean(data);
                        if (otherSide == highPriority) {
                            HPwereSAME = true;
                            thisSideInt = new Random().nextInt();
                            subsocket.sendData(PrimitiveSerializer.toByteArray(thisSideInt));
                        } else {
                            setupHP = true;
                        }
                    } else {
                        if (!HPotherSIDEwins) {
                            int o = PrimitiveSerializer.bytesToInt(data);
                            if (thisSideInt > o) {
                                highPriority = new Random().nextBoolean();
                                subsocket.sendData(PrimitiveSerializer.toByteArray(!highPriority));
                                setupHP = true;
                            } else if (thisSideInt == o) { // IF THIS EVER HAPPENS....
                                thisSideInt = new Random().nextInt();
                                subsocket.sendData(PrimitiveSerializer.toByteArray(thisSideInt));
                            } else {
                                HPotherSIDEwins = true;
                            }
                        } else {
                            highPriority = PrimitiveSerializer.bytesToBoolean(data);
                            setupHP = true;
                        }
                    }
                    if (setupHP) {
                        subsocket.sendData(PrimitiveSerializer.toByteArray(val));
                    }
                    return;
                }
                if (!setupIV) {
                    boolean otherSideVal = PrimitiveSerializer.bytesToBoolean(data);
                    if (otherSideVal != val) {
                        if (!highPriority) {
                            updateValue(otherSideVal);
                        }
                    }
                    setupIV = true;
                    setup = true;
                }
            } else {
                if (echosWaitingFor == 0) {
                    updateValue(PrimitiveSerializer.bytesToBoolean(data));
                    subsocket.sendData(data);
                } else {
                    boolean response = PrimitiveSerializer.bytesToBoolean(data);
                    if (highPriority) {
                        if (sentVariable == response) {
                            updateValue(sentVariable);
                            echosWaitingFor--;
                        }
                    } else {
                        if (response != sentVariable) {
                            subsocket.sendData(PrimitiveSerializer.toByteArray(response));
                        }
                        updateValue(response);
                        echosWaitingFor--;
                    }
                }
            }
        }
    }

    private void updateValue(boolean value) {
        val = value;
        for (SynchronizedBooleanListener sbl : listeners) {
            sbl.update(value, this);
        }
    }

    public void addListener(SynchronizedBooleanListener sbl) {
        listeners.remove(sbl);
        listeners.add(sbl);
    }
    
    public void removeListener(SynchronizedBooleanListener sbl) {
        listeners.remove(sbl);
    }

    public String getSubsocketPath() {
        return subsocket.mapCompleteRoute();
    }

    @Override
    public void connected() {
        subsocket.sendData(PrimitiveSerializer.toByteArray(highPriority));
    }

    @Override
    public void disconnected() {
        setup = false;
        setupHP = false;
        setupIV = false;
        HPotherSIDEwins = false;
        HPwereSAME = false;
        echosWaitingFor = 0;
    }
}
