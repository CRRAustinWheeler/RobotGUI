/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions;

import communications.Subsocket;
import communications.SubsocketManager;
import communications.listeners.ConnectionListener;
import communications.listeners.DataListener;
import communications.listeners.SubsocketListener;

/**
 *
 * @author laptop
 */
public abstract class Extension implements
        SubsocketListener, DataListener, ConnectionListener{

    private String tag;
    private SubsocketManager manager;
    private Subsocket subsocket;

    protected abstract String getExtCode();

    public Extension(String tag, SubsocketManager manager) {
        this.tag = tag + "." + getExtCode();
        this.manager = manager;

        manager.addSubsocketListener(this);
        int i = manager.lookUpString(tag);
        if (i != -1) {
            SubsocketAdded(i);
        }
        manager.addConnectionListener(this);
    }

    protected final void sendData(byte[] b) {
        if (subsocket != null) {
            subsocket.sendData(b);
        }
    }

    @Override
    public final synchronized void SubsocketAdded(int subsocket) {
        if (manager.getTag(subsocket).matches(tag)) {
            this.subsocket = manager.getSubsocket(subsocket);
            this.subsocket.dataListener = this;
            manager.removeSubsocketListener(this);
            connected();
        }
    }

    @Override
    public void disconnected() {
    }

    @Override
    public void connected() {
    }
}
