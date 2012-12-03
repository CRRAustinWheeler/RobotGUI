/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrobot;

import communications.CommunicationsThread;
import communications.DataStreamingModule;
import communications.SynchronizedRegisterArray;

/**
 *
 * @author laptop
 */
public class DashBoard {

    DataStreamingModule dataStreamingModule;
    Connection connection;
    CommunicationsThread communicationsThread;
    SynchronizedRegisterArray synchronizedRegisterArray;

    public DashBoard() {
        dataStreamingModule = new DataStreamingModule();
        connection = new Connection();
        synchronizedRegisterArray = new SynchronizedRegisterArray();
        communicationsThread = new CommunicationsThread(
                connection,
                synchronizedRegisterArray,
                dataStreamingModule);
    }

    public void streamPacket(double val, String name) {
        dataStreamingModule.sendPacket(val, name);
    }

    public double getRegister(String name) {
        return synchronizedRegisterArray.get(name);
    }

    public void setRegister(String name, double val) {
        synchronizedRegisterArray.setRegister(name, val);
    }
}
