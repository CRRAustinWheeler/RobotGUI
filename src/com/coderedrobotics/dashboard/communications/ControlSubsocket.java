package com.coderedrobotics.dashboard.communications;

import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.exceptions.RootRouteException;

/**
 *
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class ControlSubsocket extends Subsocket {

    Connection connection;

    ControlSubsocket(String nodeName, Connection connection) {
        super(nodeName, null);
        this.connection = connection;
    }

    @Override
    public Subsocket getParent() {
        return null;
    }

    @Override
    void sendData(byte b) {
        connection.writeByte(b);
    }

    @Override
    public void sendData(byte[] bytes) {
        switch (IDFactory.getBytesRequiredToTransmit()) {
            case 1:
                sendData((byte) getID());
                break;
            case 2:
                for (byte b : PrimitiveSerializer.toByteArray((short) getID())) {
                    sendData(b);
                }
                break;
            case 4:
                for (byte b : PrimitiveSerializer.toByteArray(getID())) {
                    sendData(b);
                }
                break;
        }
        for (byte b : PrimitiveSerializer.toByteArray((char) bytes.length)) {
            sendData(b);
        }
        for (byte b : bytes) {
            sendData(b);
        }
    }

    /**
     * The ControlSubsocket is not multiplexable, so this method does nothing.
     *
     * @return the Control Subsocket.
     */
    @Override
    public Subsocket enableMultiplexing() {
        // Do nothing;
        return this;
    }

    /**
     * The ControlSubsocket is not multiplexable, so this method does nothing.
     *
     * @return the Control Subsocket.
     */
    @Override
    public synchronized Subsocket disableMultiplexing() {
        // Do nothing
        return this;
    }

    /**
     * Will always throw a NotMultiplexedException for the ControlSubsocket,
     * because it is not multiplexable.
     *
     * @param route doesn't matter
     * @return won't ever return
     * @throws NotMultiplexedException always
     * @throws InvalidRouteException never
     */
    @Override
    public synchronized Subsocket createNewRoute(String route) throws NotMultiplexedException, InvalidRouteException {
        throw new NotMultiplexedException();
    }

    /**
     * Will always throw a NotMultiplexedException for the ControlSubsocket,
     * because it is not multiplexable.
     *
     * @param route doesn't matter
     * @throws NotMultiplexedException always
     * @throws InvalidRouteException never
     * @throws RootRouteException never
     */
    @Override
    public synchronized void destroyRoute(String route) throws InvalidRouteException, NotMultiplexedException, RootRouteException {
        throw new NotMultiplexedException();
    }
}
