package com.coderedrobotics.dashboard.communications;

/**
 *
 * @author Austin
 */
public interface SimpleSock {
    public long readLong() throws ConnectionResetException;
    public int readInt() throws ConnectionResetException;
    public double readDouble() throws ConnectionResetException;
    public char readChar() throws ConnectionResetException;
    public byte readByte() throws ConnectionResetException;
    public String readString() throws ConnectionResetException;
    public void writeLong(long l) throws ConnectionResetException;
    public void writeInt(int i) throws ConnectionResetException;
    public void writeDouble(double d) throws ConnectionResetException;
    public void writeChar(char c) throws ConnectionResetException;
    public void writeByte(byte b) throws ConnectionResetException;
    public void writeString(String b) throws ConnectionResetException;
    public void flush() throws ConnectionResetException;
    public boolean isServer();
}
