/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

import java.util.ArrayList;

/**
 *
 * @author laptop
 */
public class SubsocketManager {

    private String[] tags = new String[65536];
    private Subsocket[] subsockets = new Subsocket[65536];
    private ArrayList<SubsocketListener> subsocketListeners =
            new ArrayList<SubsocketListener>();
    private ArrayList<ConnectionListener> connectionListeners =
            new ArrayList<ConnectionListener>();
    private ServerSock serverSock;
    private boolean connected = false;

    public SubsocketManager() {
        serverSock = new ServerSock(this);
    }

    int lookUpString(String tag) {
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].matches(tag)) {
                return i;
            }
        }
        return -1;
    }

    synchronized void sendData(int i, byte[] bytes) {
        if (connected) {
            if (i >= 0 && i < 65536 && bytes.length > 0 && bytes.length <= 256) {
                serverSock.writeBytes(PrimitiveSerializer.toByteArray((char) i));
                serverSock.writeByte((byte) (bytes.length - 129));
                for (byte b : bytes) {
                    serverSock.writeByte(b);
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    void notifyDisconnected() {
        connected = false;
        for (ConnectionListener connectionListener : connectionListeners) {
            connectionListener.disconnected();
        }
    }

    void notifyConnected() {
        connected = true;
        for (ConnectionListener connectionListener : connectionListeners) {
            connectionListener.connected();
        }
    }

    public String[] getTags() {
        return tags.clone();
    }

    public Subsocket getSubsocket(int i) {
        return subsockets[i];
    }

    public void addSubsocketListener(SubsocketListener listener) {
        subsocketListeners.remove(listener);
        subsocketListeners.add(listener);
    }

    public void removeSubsocketListener(SubsocketListener listener) {
        subsocketListeners.remove(listener);
    }

    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
        connectionListeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }
}
