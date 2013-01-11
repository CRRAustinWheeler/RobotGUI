/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrobot;

import java.util.Vector;

/**
 *
 * @author laptop
 */
public class DataStreamingModule {

    private Vector streams;
    private Vector updateQueue;
    private Vector DSMListeners;

    public DataStreamingModule() {
        streams = new Vector();
        updateQueue = new Vector();
        DSMListeners = new Vector();
    }

    public synchronized void addDSMListener(DSMListener listener) {
        DSMListeners.add(listener);
        if (streams.size() != 0) {
            listener.alertToNewStreams();
            listener.alertToDSMUpdates();
        }
    }

    public synchronized boolean removeDSMListener(DSMListener listener) {
        return DSMListeners.remove(listener);
    }

    public synchronized String[] getStreamNames() {
        String[] names = new String[streams.size()];
        for (int i = 0; i < streams.size(); i++) {
            names[i] = ((DataStream) streams.elementAt(i)).getName();
        }
        return names;
    }

    public synchronized DataStream getStream(String name) {
        for (int i = 0; i < streams.size(); i++) {
            if (((DataStream) streams.elementAt(i)).getName().equals(name)) {
                return (DataStream) streams.elementAt(i);
            }
        }
        return null;
    }

    synchronized void sendPacket(Packet packet) {
        if (updateQueue.size() > 2048) {
            return;
        }
        updateQueue.addElement(packet);
    }

    public synchronized void sendPacket(double val, String name) {
        sendPacket(new Packet(val, name, System.currentTimeMillis()));
    }

    synchronized Vector exchangeUpdates(Vector updates) {

        //init flag
        boolean newStream = false;

        //process updates
        for (int i = 0; i < updates.size(); i++) {

            //search for a prexisting stream
            DataStream stream = getStream(((Packet) updates.elementAt(i)).name);

            //if one is found...
            if (stream != null) {
                //...add the packet to it
                stream.addPacket((Packet) updates.elementAt(i));
            } else {//else...
                //...make a new stream
                streams.addElement(
                        new DataStream((Packet) updates.elementAt(i)));
                //flag the new stream
                newStream = true;
            }
        }

        //notify listeners
        for (int i = 0; i < DSMListeners.size(); i++) {
            ((DSMListener)DSMListeners.get(i)).alertToDSMUpdates();
            if (newStream) {
                ((DSMListener)DSMListeners.get(i)).alertToNewStreams();
            }
        }

        //return outgoing updates
        Vector v = updateQueue;
        updateQueue = new Vector();
        return v;
    }
}
