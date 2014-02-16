package com.coderedrobotics.dashboard.communications;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Austin
 */
public class OutputStreamBuffer {

    OutputStream outputStream;
    byte[] buffer;
    int bufferSize;
    int currentWritePosition;

    public OutputStreamBuffer(OutputStream outputStream, int bufferSize) {
        if (outputStream != null) {
            this.outputStream = outputStream;
            this.bufferSize = bufferSize;
            buffer = new byte[bufferSize];
        } else {
            throw new NullPointerException();
        }
    }

    public OutputStreamBuffer(OutputStream outputStream) {
        this(outputStream, 262144);//256k buffer
    }

    private void putByteInBuffer(byte b) throws IOException {
        if (currentWritePosition != bufferSize) {
            buffer[currentWritePosition++] = b;
        } else {
            pushBuffer();
            buffer[currentWritePosition++] = b;
        }
    }

    private void putBytesInBuffer(byte[] b) throws IOException {
        for (int i = 0; i < b.length; i++) {
            putByteInBuffer(b[i]);
        }
    }

    private void pushBuffer() throws IOException {
        outputStream.write(buffer, 0, currentWritePosition);
        currentWritePosition = 0;
    }

    public void writeByte(byte b) throws IOException {
        putByteInBuffer(b);
    }

    public void writeShort(short s) throws IOException {
        putBytesInBuffer(PrimitiveSerializer.toByteArray(s));
    }

    public void writeInt(int i) throws IOException {
        putBytesInBuffer(PrimitiveSerializer.toByteArray(i));
    }

    public void writeLong(long l) throws IOException {
        putBytesInBuffer(PrimitiveSerializer.toByteArray(l));
    }

    public void writeFloat(float f) throws IOException {
        putBytesInBuffer(PrimitiveSerializer.toByteArray(f));
    }

    public void writeDouble(double d) throws IOException {
        putBytesInBuffer(PrimitiveSerializer.toByteArray(d));
    }

    public void writeChar(char c) throws IOException {
        putBytesInBuffer(PrimitiveSerializer.toByteArray(c));
    }

    public void writeString(String s) throws IOException {
        writeInt(s.length());
        for (int i = 0; i < s.length(); i++) {
            writeChar(s.charAt(i));
        }
    }

    public void writeBoolean(boolean b) throws IOException {
        putBytesInBuffer(PrimitiveSerializer.toByteArray(b));
    }

    public void flush() throws IOException {
        pushBuffer();
        outputStream.flush();
    }

    public void close() throws IOException {
        outputStream.close();
    }
}
