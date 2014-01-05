/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources.core;

import communications.Subsocket;
import communications.SubsocketManager;
import communications.listeners.ConnectionListener;
import communications.listeners.DataListener;
import communications.listeners.SubsocketListener;

/**
 *
 * @author laptop
 */
public abstract class Primitive extends Node implements
        SubsocketListener, DataListener, ConnectionListener {

    private SubsocketManager manager;
    private Subsocket subsocket;

    @Override
    protected abstract String getExtCode();

    public Primitive(Node parrent, String tag, SubsocketManager manager) {                      
        super(parrent, tag, manager);
        this.manager = manager;

        manager.addSubsocketListener(this);
        manager.addConnectionListener(this);
        int i = manager.lookUpString(this.getPath());
        if (i != -1) {
            SubsocketAdded(i);
        }
    }

    protected final void sendData(byte[] b) {
        if (subsocket != null) {
            subsocket.sendData(b);
        }
    }

    @Override
    public final synchronized void SubsocketAdded(int subsocket) {
        if (manager.getTag(subsocket).matches(getPath())) {
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
