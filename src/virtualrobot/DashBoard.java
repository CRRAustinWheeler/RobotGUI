/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrobot;

/**
 *
 * @author laptop
 */
public class DashBoard {

    DataStreamingModule dataStreamingModule;
    Connection connection;
    CommunicationsThread communicationsThread;
    SynchronizedRegisterArray synchronizedRegisterArray;
    JaguarMonitor jaguarMonitor;

    public DashBoard() {
        dataStreamingModule = new DataStreamingModule();
        jaguarMonitor = new JaguarMonitor(dataStreamingModule);
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

    public void monitorJaguar(Jaguar j) {
        jaguarMonitor.addJaguar(j);
    }
}