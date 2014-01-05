/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

import communications.listeners.ConnectionListener;
import communications.listeners.SubsocketListener;
import java.util.ArrayList;

/**
 *
 * @author laptop
 */
public class SubsocketManager implements Runnable {

    private Thread thread;
    private String[] tags = new String[65536];
    private Subsocket[] subsockets = new Subsocket[65536];
    private int numberOfSubsockets = 0;
    private ArrayList<SubsocketListener> subsocketListeners =
            new ArrayList<SubsocketListener>();
    private ArrayList<ConnectionListener> connectionListeners =
            new ArrayList<ConnectionListener>();
    private ServerSock serverSock;
    private boolean connected = false;

    public SubsocketManager() {
        serverSock = new ServerSock(this);
        thread = new Thread(this);
        thread.start();
    }

    public int lookUpString(String tag) {
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].matches(tag)) {
                return i;
            }
        }
        return -1;
    }

    synchronized void sendData(int i, byte[] bytes) {
        if (connected) {

            if (i >= 0 && i < 65536 && bytes.length > 0 && bytes.length <= 65536) {
                serverSock.writeBytes(PrimitiveSerializer.toByteArray((char) i));
                serverSock.writeBytes(PrimitiveSerializer.toByteArray((char) bytes.length));
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

    public String getTag(int i) {
        return tags[i];
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

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }

            serverSock.flush();

            try {
                while (serverSock.isDataReady()) {
                    byte[] b = {serverSock.readByte(), serverSock.readByte()};
                    int i = PrimitiveSerializer.bytesToChar(b);
                    b[0] = serverSock.readByte();
                    b[1] = serverSock.readByte();
                    byte[] bytes = new byte[((int) PrimitiveSerializer.bytesToChar(b)) + 1];
                    for (int j = 0; j < bytes.length; j++) {
                        bytes[j] = serverSock.readByte();
                    }
                    if (i == 65535) {
                        subsockets[numberOfSubsockets] =
                                new Subsocket(this, numberOfSubsockets);
                        tags[numberOfSubsockets] =
                                PrimitiveSerializer.bytesToString(bytes);
                        for (SubsocketListener subsocketListener : subsocketListeners) {
                            subsocketListener.SubsocketAdded(numberOfSubsockets);
                        }
                        numberOfSubsockets++;
                    } else {
                        subsockets[i].dataListener.pushData(bytes);
                    }
                }
            } catch (ConnectionResetException ex) {
            }
        }
    }
}
