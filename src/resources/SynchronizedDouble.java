/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import communications.PrimitiveSerializer;
import communications.SubsocketManager;
import resources.core.GenericListener;
import resources.core.Node;
import resources.core.Primitive;

/**
 *
 * @author laptop
 */
public class SynchronizedDouble extends Primitive {

    private double d, remote;
    private GenericListener listener;

    public SynchronizedDouble(Node folder, String tag, SubsocketManager manager) { 
        super(folder, tag, manager);
    }
    
    public SynchronizedDouble(Node folder, String tag, SubsocketManager manager, GenericListener listener) { 
        super(folder, tag, manager);
        setListener(listener);
    }

    @Override
    protected String getExtCode() {
        return "sdv";
    }

    public void setRemote(double val) {
        remote = val;
        flush();
    }

    public void setListener(GenericListener listener) {
        this.listener = listener;
    }

    public double get() {
        return d;
    }

    @Override
    public void pushData(byte[] b) {
        d = PrimitiveSerializer.bytesToDouble(b);
        listener.pushData(this, d);
    }

    private void flush() {
        sendData(PrimitiveSerializer.toByteArray(remote));
    }

    @Override
    public void connected() {
        flush();
    }
}
