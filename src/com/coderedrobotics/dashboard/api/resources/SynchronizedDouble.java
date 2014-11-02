package com.coderedrobotics.dashboard.api.resources;

import com.coderedrobotics.dashboard.communications.Connection;
import com.coderedrobotics.dashboard.communications.PrimitiveSerializer;
import com.coderedrobotics.dashboard.communications.Subsocket;
import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.listeners.SubsocketListener;
import java.util.Random;

/**
 *
 * @author Michael
 */
public class SynchronizedDouble implements SubsocketListener {

    private double val;
    private boolean highPriority;
    private boolean setup = false;
    private boolean setupHP = false;
    private boolean HPwereSAME = false;
    private int thisSideInt;
    private boolean HPotherSIDEwins = false;
    private boolean setupIV = false;

    private int echosWaitingFor = 0;
    private double sentVariable;

    Subsocket subsocket;

    @SuppressWarnings("LeakingThisInConstructor")
    public SynchronizedDouble(String subsocketPath, double initialValue, boolean highPriority) throws InvalidRouteException {
        val = initialValue;
        this.highPriority = highPriority;
        try {
            subsocket = Connection.getInstance().getRootSubsocket().enableMultiplexing().createNewRoute(subsocketPath);
        } catch (NotMultiplexedException ex) {
            // really not possible, but java wins again
        }
        subsocket.addListener(this);
        subsocket.sendData(PrimitiveSerializer.toByteArray(highPriority));
    }

    public SynchronizedDouble(String subsocketPath, double initialValue) throws InvalidRouteException {
        this(subsocketPath, initialValue, false);
    }

    public SynchronizedDouble(String subsocketPath) throws InvalidRouteException {
        this(subsocketPath, 0);
    }

    public synchronized void setValue(double value) {
        sentVariable = value;
        echosWaitingFor++;
        update();
    }

    public double getValue() {
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
                    double otherSideVal = PrimitiveSerializer.bytesToDouble(data);
                    if (otherSideVal != val) {
                        if (!highPriority) {
                            val = otherSideVal;
                        }
                    }                    
                    setupIV = true;
                    setup = true;
                }
            } else {
                if (echosWaitingFor == 0) {
                    val = PrimitiveSerializer.bytesToDouble(data);
                    subsocket.sendData(data);
                } else {
                    double response = PrimitiveSerializer.bytesToDouble(data);
                    if (highPriority) {
                        if (sentVariable == response) {
                            val = sentVariable;
                            echosWaitingFor--;
                        }
                    } else {
                        if (response != sentVariable) {
                            subsocket.sendData(PrimitiveSerializer.toByteArray(response));
                        }
                        val = response;
                        echosWaitingFor--;
                    }
                }
            }
        }
    }

    public String getSubsocketPath() {
        return subsocket.mapCompleteRoute();
    }
}
