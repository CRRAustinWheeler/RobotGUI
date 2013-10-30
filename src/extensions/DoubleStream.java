/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions;

import communications.PrimitiveSerializer;
import communications.SubsocketManager;

/**
 *
 * @author laptop
 */
public class DoubleStream extends Extension {

    private Listener listener;
    private boolean dataTransmissionEnabled = false;
    private int maxHZ = 0;

    public DoubleStream(String tag, SubsocketManager manager) {
        super(tag, manager);
    }

    @Override
    protected String getExtCode() {
        return "dbl";
    }

    @Override
    public void pushData(byte[] b) {
        if (b.length == 1) {
            dataTransmissionEnabled = PrimitiveSerializer.bytesToBoolean(b);
        } else if (b.length == 2) {
            maxHZ = PrimitiveSerializer.bytesToChar(b);
        } else if (b.length == 4) {
            if (listener != null) {
                listener.pushDouble(PrimitiveSerializer.bytesToFloat(b));
            }
        } else if (b.length == 8) {
            if (listener != null) {
                listener.pushDouble(PrimitiveSerializer.bytesToDouble(b));
            }
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setHZFilter(char hz) {
        sendData(PrimitiveSerializer.toByteArray(hz));
    }

    public void disableHZFilter() {
        sendData(PrimitiveSerializer.toByteArray(0));
    }

    public void disableIncomingData() {
        sendData(PrimitiveSerializer.toByteArray(false));
    }

    public void enableIncomingData() {
        sendData(PrimitiveSerializer.toByteArray(true));
    }

    public void sendDouble(double d) {
        sendData(PrimitiveSerializer.toByteArray(d));
    }

    public interface Listener {

        public void pushDouble(double d);
    }
}
