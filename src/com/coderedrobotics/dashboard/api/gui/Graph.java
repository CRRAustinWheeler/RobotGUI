package com.coderedrobotics.dashboard.api.gui;

//import com.coderedrobotics.dashboard.communications.DataStream;
//import com.coderedrobotics.dashboard.communications.Packet;
import com.coderedrobotics.dashboard.communications.Connection;
import com.coderedrobotics.dashboard.communications.PrimitiveSerializer;
import com.coderedrobotics.dashboard.communications.Subsocket;
import com.coderedrobotics.dashboard.communications.exceptions.InvalidRouteException;
import com.coderedrobotics.dashboard.communications.exceptions.NotMultiplexedException;
import com.coderedrobotics.dashboard.communications.listeners.SubsocketListener;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Austin
 */
public class Graph extends javax.swing.JPanel implements Runnable {

    private double graphTime = -10000;
    private ArrayList<GStream> streams;
    private double hz = 16;
    private Thread thread;

    /**
     * Creates new form Graph
     */
    public Graph() {
        initComponents();
        streams = new ArrayList();
        thread = new Thread(this);
        thread.setName("Graphing Thread " + Math.abs(new Random().nextInt()));
        thread.start();
    }

    public synchronized void sethz(int hz) {
        this.hz = hz;
    }

    public synchronized void addStream(
            String subsocketPath, Color color,
            double center, double scale,
            boolean drawZero) {
        streams.add(new GStream(new DataStream(subsocketPath), center, scale, color, drawZero));
    }

    public synchronized String[] geStreams() {
        String[] names = new String[streams.size()];
        for (int i = 0; i < streams.size(); i++) {
            names[i] = streams.get(i).stream.dataStream.mapCompleteRoute();
        }
        return names;
    }

    public synchronized void remoGraphveStream(String stream) {
        for (int i = 0; i < streams.size(); i++) {
            if (streams.get(i).stream.dataStream.mapCompleteRoute().equals(stream)) {
                streams.remove(i);
            }
        }
    }

    public synchronized void removeAllStreams() {
        streams = new ArrayList();
    }

    public synchronized void setTime(long time) {
        graphTime = -Math.abs(time);
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color c;
        for (GStream gStream : streams) {
            if (gStream.drawZero) {
                c = new Color(
                        (gStream.color.getRed() + 2048) / 9,
                        (gStream.color.getGreen() + 2048) / 9,
                        (gStream.color.getBlue() + 2048) / 9);
                g.setColor(c);
                paintLine(
                        System.currentTimeMillis(),
                        gStream.center,
                        System.currentTimeMillis() + ((long) graphTime),
                        gStream.center, g);
            }
        }
        Packet oldPacket;
        for (GStream gStream : streams) {
            oldPacket = gStream.stream.getLastPacket();
            for (int i = 1; i < gStream.stream.getPackets().length
                    && oldPacket.time - System.currentTimeMillis()
                    > graphTime; i++) {
                g.setColor(gStream.color);
                paintLine(
                        oldPacket.time,
                        (oldPacket.val
                        * gStream.scale) + gStream.center,
                        gStream.stream.getPackets()[i].time,
                        (gStream.stream.getPackets()[i].val
                        * gStream.scale) + gStream.center, g);
                oldPacket = gStream.stream.getPackets()[i];
            }

        }
    }

    private void paintLine(
            long x1, double y1,
            long x2, double y2,
            Graphics g) {

        y1 = -y1 + 1;
        y2 = -y2 + 1;

        y1 = y1 * getHeight();
        y2 = y2 * getHeight();

        double xx1 = (((double) (x1 - System.currentTimeMillis()))
                * getWidth()) / graphTime;
        double xx2 = (((double) (x2 - System.currentTimeMillis()))
                * getWidth()) / graphTime;

        g.drawLine((int) xx1, (int) y1, (int) xx2, (int) y2);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep((int) (1000d * (1d / hz)));
            } catch (InterruptedException ex) {
            }
            if (isShowing()) {
                repaint();
            }
        }
    }

    private class GStream {

        private GStream(DataStream stream, double center,
                double scale, Color color, boolean drawZero) {
            this.stream = stream;
            this.center = center;
            this.scale = scale;
            this.color = color;
            this.drawZero = drawZero;
        }

        private GStream() {
        }
        DataStream stream;
        double center;
        double scale;
        Color color;
        boolean drawZero;
    }

    class Packet {

        final double val;
        final long time;

        public Packet(double val) {
            this.val = val;
            this.time = System.currentTimeMillis();
        }
    }

    class DataStream implements SubsocketListener {

        private ArrayList<Packet> vpackets;
        private Packet[] apackets;
        private boolean arrayIsCurrent;

        private Subsocket dataStream;

        DataStream(String subsocketPath) {
            try {
                Connection.getInstance().getRootSubsocket().enableMultiplexing().createNewRoute(subsocketPath).addListener(this);
                vpackets = new ArrayList<>();
                vpackets.add(new Packet(0));//TODO: remove
                arrayIsCurrent = false;
                refreshArray();
            } catch (NotMultiplexedException | InvalidRouteException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void incomingData(byte[] data, Subsocket sender) {
            addPacket(PrimitiveSerializer.bytesToDouble(data));
        }

        synchronized void addPacket(double val) {
            vpackets.add(0, new Packet(val));
            arrayIsCurrent = false;
        }

        private void refreshArray() {
            apackets = new Packet[vpackets.size()];
            for (int i = 0; i < vpackets.size(); i++) {
                apackets[i] = (Packet) vpackets.get(i);
            }
            arrayIsCurrent = true;
        }

        public synchronized Packet[] getPackets() {
            if (!arrayIsCurrent) {
                refreshArray();
            }
            return apackets;
        }

        public synchronized Packet getLastPacket() {
            return (Packet) vpackets.get(0);
        }

        public synchronized Packet[] getPackets(int num) {
            Packet[] result = new Packet[num];
            for (int i = num - 1; i >= 0; i--) {
                Packet p = null;
                int j = vpackets.size() - num;
                if (i + j > -1) {
                    result[i] = (Packet) vpackets.get(i + j);
                } else {
                    result[i] = null;
                }
            }
            return result;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
