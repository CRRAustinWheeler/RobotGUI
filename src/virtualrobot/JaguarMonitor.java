/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrobot;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author laptop
 */
class JaguarMonitor implements Runnable {

    Vector jaguars;
    DataStreamingModule dataStreamingModule;
    int hz;

    JaguarMonitor(DataStreamingModule dataStreamingModule, int hz) {
        jaguars = new Vector();
        this.hz = hz;
        this.dataStreamingModule = dataStreamingModule;
        Thread thread = new Thread(this);
        thread.start();
    }

    JaguarMonitor(DataStreamingModule dataStreamingModule) {
        this(dataStreamingModule, 12);
    }

    void setHz(int hz) {
        this.hz = hz;
    }

    void addJaguar(Jaguar jaguar) {
        jaguars.add(jaguar);
    }

    @Override
    public void run() {
        /*
         * Stream Names
         * 
         * Voltage Output: "CANJAGUAROV"
         * Current: "CANJAGUARI"
         * Voltage From Battery: "CANJAGUARIV"
         * Tempature: "CANJAGUART"
         */
        while (true) {

            for (int i = 0; i < jaguars.size(); i++) {
                Jaguar j = ((Jaguar) jaguars.get(i));
                dataStreamingModule.sendPacket(j.getBusVoltage(), "CANJAGUARIV"+j.getID());
                dataStreamingModule.sendPacket(j.getOutputVoltage(), "CANJAGUAROV"+j.getID());
                dataStreamingModule.sendPacket(j.getOutputCurrent(), "CANJAGUARI"+j.getID());
                dataStreamingModule.sendPacket(j.getTemperature(), "CANJAGUART"+j.getID());
                dataStreamingModule.sendPacket(j.getX(), "CANJAGUARX"+j.getID());
            }
            try {
                Thread.sleep((long) (1000 / hz));
            } catch (InterruptedException ex) {
                Logger.getLogger(JaguarMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}