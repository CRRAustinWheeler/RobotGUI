/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import communications.SubsocketManager;
import resources.core.GenericListener;
import resources.core.Node;

/**
 *
 * @author laptop
 */
public class DoubleStream extends Node implements GenericListener {

    private GenericListener listener;
    SynchronizedDouble stream;
    SynchronizedDouble dataTransmissionEnabled;//bool
    SynchronizedDouble maxHZ;//int

    public DoubleStream(Node folder, String path, SubsocketManager manager) {
        this(null, folder, path, manager);
    }

    public DoubleStream(
            GenericListener listener,
            Node node,
            String tag,
            SubsocketManager manager) {
        super(node, tag, manager);
        this.listener = listener;
        dataTransmissionEnabled
                = new SynchronizedDouble(this, "transEnabled", manager);
        maxHZ = new SynchronizedDouble(this, "HZcap", manager);
        stream = new SynchronizedDouble(this, "stream", manager, this);
    }

    @Override
    protected String getExtCode() {
        return "stream";
    }

    public void setListener(GenericListener listener) {
        this.listener = listener;
    }

    public void sendDouble(double d) {//this thing needs work
        if (dataTransmissionEnabled.get() == 1d) {
            stream.setRemote(d);
        }
    }

    public void setHZFilter(int hz) {
        maxHZ.setRemote(hz);
    }

    public void disableHZFilter() {
        setHZFilter(0);
    }

    public void disableIncomingData() {
        dataTransmissionEnabled.setRemote(0);
    }

    public void enableIncomingData() {
        dataTransmissionEnabled.setRemote(1);
    }

    public double get() {
        return stream.get();
    }

    @Override
    public void pushData(Node node, Object data) {
        Object[] objects = {data, System.currentTimeMillis()};
        listener.pushData(this, objects);
    }
}
/* * * * * Notes * * * * * *\
 *                         *
 * Remote filter settings  *
 *   hz filter             *
 *   enable/disable filter *
 *                         *
 * TODO: time sync         *
 * * * * * * * * * * * * * */
