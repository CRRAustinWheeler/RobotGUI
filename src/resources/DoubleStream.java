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
public class DoubleStream extends Primitive {

    private Listener listener;
    private boolean dataTransmissionEnabled = false;
    private boolean compressionEnabled = false;
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
            dataTransmissionEnabled = (b[0] % 2) == 1;
            compressionEnabled = ((b[0] / 2) % 2) == 1;
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

    public void sendDouble(double d) {
        if (dataTransmissionEnabled) {
            if (compressionEnabled) {
                sendData(PrimitiveSerializer.toByteArray((float)d));
            } else {
                sendData(PrimitiveSerializer.toByteArray(d));
            }
        }
    }
    //<editor-fold defaultstate="collapsed" desc="remote settings management">
    private char remoteHZsettings = 0;
    private boolean remoteTransmissionEnabled = false;
    private boolean remoteCompressionEnabled = false;

    public void setHZFilter(char hz) {
        remoteHZsettings = hz;
        flushHZFilter();
    }

    public void disableHZFilter() {
        remoteHZsettings = 0;
        flushHZFilter();
    }

    public void disableIncomingData() {
        remoteTransmissionEnabled = false;
        flushEnabledAndCompressed();
    }

    public void enableIncomingData() {
        remoteTransmissionEnabled = true;
        flushEnabledAndCompressed();
    }

    public void disableRemoteCompression() {
        remoteCompressionEnabled = false;
        flushEnabledAndCompressed();
    }

    public void enableRemoteCompression() {
        remoteCompressionEnabled = true;
        flushEnabledAndCompressed();
    }

    private void flushEnabledAndCompressed() {
        sendData(PrimitiveSerializer.toByteArray(
                (byte) ((remoteTransmissionEnabled ? 1 : 0)
                + (remoteCompressionEnabled ? 2 : 0))));
    }

    private void flushHZFilter() {
        sendData(PrimitiveSerializer.toByteArray(remoteHZsettings));
    }

    @Override
    public void connected() {
        flushHZFilter();
        flushEnabledAndCompressed();
    }
    //</editor-fold>

    public interface Listener {

        public void pushDouble(double d);
    }
}
/* * * * * Notes * * * * * *\
 *                         *
 * Remote filter settings  *
 *   hz filter             *
 *   float cast filter     *
 *   enable/disable filter *
 *                         *
 * TODO: time sync         *
\* * * * * * * * * * * * * */