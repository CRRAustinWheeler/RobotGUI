/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotgui;

import communications.ConnectionResetException;
import communications.SimpleSock;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author laptop
 */
public class ServerSock implements SimpleSock {

    Thread thread;
    ServerSocket serverSocket;
    Socket connection;
    private InputStreamBuffer inputStreamBuffer;
    private OutputStreamBuffer outputStreamBuffer;

    public ServerSock() {
        try {
            serverSocket = new ServerSocket(1180);
        } catch (IOException ex) {
            System.exit(1180);
        }
    }

    private void reconnect() {
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
    }

    @Override
    public synchronized long readLong()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return inputStreamBuffer.readLong();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    @Override
    public synchronized int readInt()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return inputStreamBuffer.readInt();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    @Override
    public synchronized double readDouble()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return inputStreamBuffer.readDouble();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    @Override
    public synchronized char readChar()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return inputStreamBuffer.readChar();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }

    }

    @Override
    public synchronized byte readByte()
            throws ConnectionResetException {
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

    @Override
    public synchronized void writeLong(long l)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            outputStreamBuffer.writeLong(l);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    @Override
    public synchronized void writeInt(int i)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            outputStreamBuffer.writeInt(i);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    @Override
    public synchronized void writeDouble(double d)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            outputStreamBuffer.writeDouble(d);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    @Override
    public synchronized void writeChar(char c)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            outputStreamBuffer.writeChar(c);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    @Override
    public synchronized void writeByte(byte b)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            outputStreamBuffer.writeByte(b);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    @Override
    public synchronized String readString()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        int size = readInt();
        String result = "";
        for (int i = 0; i < size; i++) {
            result += readChar();
        }
        return result;
    }

    @Override
    public synchronized void writeString(String s)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        writeInt(s.length());
        for (int i = 0; i < s.length(); i++) {
            writeChar(s.charAt(i));
        }
    }

    @Override
    public synchronized void flush() throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            outputStreamBuffer.flush();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    @Override
    public boolean isServer() {
        return true;
    }
}