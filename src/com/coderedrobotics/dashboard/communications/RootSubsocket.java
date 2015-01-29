package com.coderedrobotics.dashboard.communications;

/**
 * All Subsockets are a child of the RootSubsocket.
 *
 * @see Subsocket
 * @see RootRouteException
 * @author Michael Spoehr
 * @since Dash 3.0
 */
public class RootSubsocket extends Subsocket {

    Connection connection;

    public RootSubsocket(String nodeName, Connection connection) {
        super(nodeName, null);
        this.connection = connection;
    }

    /**
     * Get the parent of a Subsocket. The RootSubsocket doesn't have a parent,
     * so this will be null.
     *
     * @return The parent subsocket object, or null if the subsocket has no
     * parent.
     */
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
        switch (BindingManager.getBytesRequiredToTransmit()) {
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
}
