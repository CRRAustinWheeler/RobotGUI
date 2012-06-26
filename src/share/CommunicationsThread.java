/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package share;

import java.util.Vector;

/**
 *
 * @author laptop
 */
public class CommunicationsThread implements Runnable {

    Thread thread;
    SimpleSock sock;
    SynchronizedRegisterArray synchronizedRegisterArray;

    public CommunicationsThread(SimpleSock sock,
            SynchronizedRegisterArray synchronizedRegisterArray) {
        thread = new Thread(this);
        thread.start();
        this.sock = sock;
        this.synchronizedRegisterArray = synchronizedRegisterArray;
    }

    public void run() {
        while (true) {
            try {
                if (sock.isServer()) {
                    sendRegisterArrayUpdates(sock,
                            synchronizedRegisterArray.exchangeUpdates(
                            new Vector()));
                }
                while (true) {
                    sendRegisterArrayUpdates(sock, 
                            synchronizedRegisterArray.exchangeUpdates(
                            getRegisterArrayUpdates(sock)));
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
}
