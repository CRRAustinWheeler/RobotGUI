/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import communications.PrimitiveSerializer;
import communications.SubsocketManager;

/**
 *
 * @author laptop
 */
public class SynchronizedDouble extends Primitive {

    private double d, remote;

    public SynchronizedDouble(String tag, SubsocketManager manager) {
        super(tag, manager);
    }

    @Override
    protected String getExtCode() {
        return "sdv";
    }

    public void setRemote(double val) {
        remote = val;
        flush();
    }

    @Override
    public void pushData(byte[] b) {
        d = PrimitiveSerializer.bytesToDouble(b);
    }

    private void flush() {
        sendData(PrimitiveSerializer.toByteArray(remote));
    }

    @Override
    public void connected() {
        flush();
    }
}
