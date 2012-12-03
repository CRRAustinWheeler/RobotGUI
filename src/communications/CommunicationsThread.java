/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author laptop
 */
public class CommunicationsThread implements Runnable {

    Thread thread;
    SimpleSock sock;
    SynchronizedRegisterArray synchronizedRegisterArray;
    DataStreamingModule dataStreamingModule;

    public CommunicationsThread(SimpleSock sock,
            SynchronizedRegisterArray synchronizedRegisterArray,
            DataStreamingModule dataStreamingModule) {
        this.sock = sock;
        this.synchronizedRegisterArray = synchronizedRegisterArray;
        this.dataStreamingModule = dataStreamingModule;
        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        while (true) {
            try {
                if (sock.isServer()) {
                    sendRegisterArrayUpdates(sock,
                            synchronizedRegisterArray.exchangeUpdates(
                            new Vector()));
                    sendStreamUpdates(sock,
                            dataStreamingModule.exchangeUpdates(
                            new Vector()));
                    sock.flush();
                }
                while (true) {
                    Vector arrayUpdates = getRegisterArrayUpdates(sock);
                    Vector streamUpdates = getStreamUpdates(sock);
                    arrayUpdates = synchronizedRegisterArray.
                            exchangeUpdates(arrayUpdates);
                    streamUpdates = dataStreamingModule.
                            exchangeUpdates(streamUpdates);
                    sendRegisterArrayUpdates(sock, arrayUpdates);
                    sendStreamUpdates(sock, streamUpdates);
                    sock.flush();
                }
            } catch (ConnectionResetException cre) {
                synchronizedRegisterArray.resynchronize();
            }
        }
    }

    private Vector getRegisterArrayUpdates(SimpleSock sock)
            throws ConnectionResetException {
        int capacity = sock.readInt();
        Vector vector = new Vector(capacity);
        for (int i = 0; i < capacity; i++) {
            vector.addElement(
                    new Register(sock.readString(), sock.readDouble()));
        }
        return vector;
    }

    private void sendRegisterArrayUpdates(SimpleSock sock, Vector updates)
            throws ConnectionResetException {
        sock.writeInt(updates.size());
        for (int i = 0; i < updates.size(); i++) {
            sock.writeString(((Register) updates.elementAt(i)).name);
            sock.writeDouble(((Register) updates.elementAt(i)).val);
        }
    }

    private Vector getStreamUpdates(SimpleSock sock)
            throws ConnectionResetException {
        int capacity = sock.readInt();
        Vector vector = new Vector(capacity);
        for (int i = 0; i < capacity; i++) {
            vector.addElement(
                    new Packet(
                    sock.readDouble(),
                    sock.readString(),
                    sock.readLong() + System.currentTimeMillis()));
        }
        return vector;
    }

    private void sendStreamUpdates(SimpleSock sock, Vector updates)
            throws ConnectionResetException {
        sock.writeInt(updates.size());
        for (int i = 0; i < updates.size(); i++) {
            sock.writeDouble(((Packet) updates.elementAt(i)).val);
            sock.writeString(((Packet) updates.elementAt(i)).name);
            sock.writeLong(((Packet) updates.elementAt(i)).time
                    - System.currentTimeMillis());
        }
    }
}
