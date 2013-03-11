/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotgui;

import communications.ConnectionResetException;
import communications.SimpleSock;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author laptop
 */
public class ServerSock1 implements SimpleSock {
    
    Thread thread;
    ServerSocket serverSocket;
    Socket connection;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    
    public ServerSock1() {
        try {
            serverSocket = new ServerSocket(1180);
        } catch (IOException ex) {
            System.exit(1180);
        }
    }
    
    public void purgeConnection() {
        if (connection != null) {
            try {
                System.out.println("purging connecting");
                connection.close();
            } catch (IOException ex) {
            }
            connection = null;
        }
    }
    
    private void reconnect() {
        boolean retry = true;
        while (retry) {
            retry = false;
            try {
                if (bufferedReader != null) {//close if open
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {//close if open
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
            }
            purgeConnection();
            while (connection == null) {//keeps trying to connect
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                try {
                    connection = serverSocket.accept();
                    System.out.println("connection accepted");
                } catch (IOException ex) {
                    purgeConnection();
                }
            }
            //setup the reader and writer objects
            try {
                bufferedReader =
                        new BufferedReader(
                        new InputStreamReader(
                        connection.getInputStream()));
                bufferedWriter =
                        new BufferedWriter(
                        new OutputStreamWriter(
                        connection.getOutputStream()));
                bufferedWriter.write("\n\n\n");
                bufferedWriter.flush();
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
            return Long.parseLong(bufferedReader.readLine());
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
            return Integer.parseInt(bufferedReader.readLine());
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
            return Double.parseDouble(bufferedReader.readLine());
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
            return (char) bufferedReader.read();
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
            return Byte.parseByte(bufferedReader.readLine());
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
            bufferedWriter.write(new Long(l).toString());
            bufferedWriter.newLine();
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
            bufferedWriter.write(new Integer(i).toString()+"\n");
            //bufferedWriter.newLine();
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
            bufferedWriter.write(new Double(d).toString());
            bufferedWriter.newLine();
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
            bufferedWriter.write((int) c);
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
            bufferedWriter.write(new Byte(b).toString());
            bufferedWriter.newLine();
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
            bufferedWriter.flush();
            System.out.println("flushed");
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
