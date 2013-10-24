/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import sun.net.ConnectionResetException;

/**
 *
 * @author laptop
 */
public class ServerSock {

    ServerSocket serverSocket;
    Socket connection;
    private InputStreamBuffer inputStreamBuffer;
    private OutputStreamBuffer outputStreamBuffer;
    private SubsocketManager manager;

    public ServerSock(SubsocketManager manager) {
        this.manager = manager;
        try {
            serverSocket = new ServerSocket(1180);
        } catch (IOException ex) {
            System.exit(1180);
        }
    }

    private void reconnect() {
        manager.notifyDisconnected();
        
        boolean retry = true;
        while (retry) {
            retry = false;
            try {
                if (inputStreamBuffer != null) {//close if open
                    inputStreamBuffer.close();
                }
                if (outputStreamBuffer != null) {//close if open
                    outputStreamBuffer.close();
                }
                if (connection != null) {//close if open
                    connection.close();
                }
            } catch (IOException ex) {
            }
            connection = null;
            while (connection == null) {//keeps trying to connect
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                try {
                    System.out.println("waiting for connection");
                    connection = serverSocket.accept();
                } catch (IOException ex) {
                    connection = null;
                    System.out.println("server down");
                }
            }
            //setup the reader and writer objects
            try {
                outputStreamBuffer =
                        new OutputStreamBuffer(connection.getOutputStream());
                inputStreamBuffer =
                        new InputStreamBuffer(connection.getInputStream());
            } catch (IOException ex) {
                retry = true;
            }
        }
        manager.notifyConnected();
    }

    public synchronized byte readByte() throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return inputStreamBuffer.readByte();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public void writeByte(byte b) {
        outputStreamBuffer.writeByte(b);
    }

    public void writeBytes(byte[] toByteArray) {
        for (byte b : toByteArray) {
            writeByte(b);
        }
    }

    public void flush() {
        if (connection == null) {
            reconnect();
        }
        try {
            outputStreamBuffer.flush();
        } catch (IOException ex) {
            reconnect();

        }
    }

    public boolean isDataReady() {
        return inputStreamBuffer.isDataReady();
    }
}