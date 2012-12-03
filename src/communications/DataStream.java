/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

import java.util.Vector;

/**
 *
 * @author laptop
 */
public class DataStream {

    private Vector vpackets;
    private Packet[] apackets;
    private boolean arrayIsCurrent;

    DataStream(Packet packet) {
        vpackets = new Vector();
        vpackets.addElement(packet);
        arrayIsCurrent = false;
        refreshArray();
    }

    synchronized void addPacket(Packet packet) {
        if (packet.name.equals(getName())) {
            vpackets.insertElementAt(packet, 0);
            arrayIsCurrent = false;
        } else {
            throw new NullPointerException("wrong name");
        }
    }

    private void refreshArray() {
        apackets = new Packet[vpackets.size()];
        for (int i = 0; i < vpackets.size(); i++) {
            apackets[i] = (Packet) vpackets.elementAt(i);
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
        return (Packet) vpackets.elementAt(0);
    }

    public synchronized Packet[] getPackets(int num) {
        Packet[] result = new Packet[num];
        for (int i = num - 1; i >= 0; i--) {
            Packet p = null;
            int j = vpackets.size() - num;
            if (i + j > -1) {
                result[i] = (Packet) vpackets.elementAt(i + j);
            } else {
                result[i] = null;
            }
        }
        return result;
    }

    public synchronized String getName() {
        return apackets[0].name;
    }
}