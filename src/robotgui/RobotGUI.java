/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotgui;

import communications.CommunicationsThread;
import communications.DataStreamingModule;
import communications.SynchronizedRegisterArray;

/**
 *
 * @author laptop
 */
public class RobotGUI {

    static MainWindow mainWindow;
    static DataStreamingModule dataStreamingModule;
    static ServerSock serverSock;
    static CommunicationsThread communicationsThread;
    static SynchronizedRegisterArray synchronizedRegisterArray;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new virtualrobot.VirtualRobot();
        dataStreamingModule = new DataStreamingModule();
        serverSock = new ServerSock();
        synchronizedRegisterArray = new SynchronizedRegisterArray();
        communicationsThread = new CommunicationsThread(
                serverSock,
                synchronizedRegisterArray,
                dataStreamingModule);
        mainWindow = new MainWindow();
        mainWindow.init(dataStreamingModule, synchronizedRegisterArray);
    }
}
