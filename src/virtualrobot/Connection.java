/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrobot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import share.ConnectionResetException;
import share.SimpleSock;

public class Connection implements SimpleSock {

    private Socket connection;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public Connection() {
        connection = null;
    }

    private void reconnect() {
        boolean retry = true;
        while (retry) {
            retry = false;
            try {
                if (dataInputStream != null) {//close if open
                    dataInputStream.close();
                }
                if (dataOutputStream != null) {//close if open
                    dataOutputStream.close();
                }
                if (connection != null) {//close if open
                    connection.close();
                }
            } catch (IOException ex) {
            }
            connection = null;
            while (connection == null) {//keeps trying to connect
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                try {
                    connection = new Socket(Inet4Address.getByName("localhost"), 1180);
                } catch (IOException ex) {
                    connection = null;
                    try {
                        System.out.println("server down @ "+Inet4Address.getByName("localhost").getHostAddress());
                    } catch (UnknownHostException ex1) {
                        Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
            //setup the reader and writer objects
            try {
                dataOutputStream = new DataOutputStream(
                        new BufferedOutputStream(
                        connection.getOutputStream()));
                dataInputStream = new DataInputStream(
                        new BufferedInputStream(
                        connection.getInputStream()));
            } catch (IOException ex) {
                retry = true;
            }
        }
    }

    public synchronized long readLong()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return dataInputStream.readLong();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public synchronized int readInt()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return dataInputStream.readInt();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public synchronized double readDouble()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return dataInputStream.readDouble();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public synchronized char readChar()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return dataInputStream.readChar();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }

    }

    public synchronized byte readByte()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            return dataInputStream.readByte();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public synchronized void writeLong(long l)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            dataOutputStream.writeLong(l);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public synchronized void writeInt(int i)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            dataOutputStream.writeInt(i);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public synchronized void writeDouble(double d)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            dataOutputStream.writeDouble(d);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public synchronized void writeChar(char c)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            dataOutputStream.writeChar(c);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public synchronized void writeByte(byte b)
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            dataOutputStream.writeByte(b);
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

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

    public synchronized void writeString(String s)
            throws ConnectionResetException {
        writeInt(s.length());
        for (int i = 0; i < s.length(); i++) {
            writeChar(s.charAt(i));
        }
    }

    public synchronized void flush()
            throws ConnectionResetException {
        if (connection == null) {
            reconnect();
        }
        try {
            dataOutputStream.flush();
        } catch (IOException ex) {
            reconnect();
            throw new ConnectionResetException();
        }
    }

    public boolean isServer() {
        return false;
    }
}