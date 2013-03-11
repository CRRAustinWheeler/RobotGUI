/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotgui;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author laptop
 */
public class InputStreamBuffer {

    InputStream inputStream;
    byte[] buffer;
    int bufferSize;
    int dataSizeInBuffer;
    int currentReadPosition;

    public InputStreamBuffer(InputStream inputStream, int bufferSize) {
        if (inputStream != null) {
            this.inputStream = inputStream;
            this.bufferSize = bufferSize;
            buffer = new byte[bufferSize];
        } else {
            throw new NullPointerException();
        }
    }

    public InputStreamBuffer(InputStream inputStream) {
        this(inputStream, 262144);//256k buffer
    }

    private byte getByteFromBuffer() throws IOException {
        while (true) {
            if (currentReadPosition != dataSizeInBuffer) {
                return buffer[currentReadPosition++];
            }
            fillBuffer();
        }
    }

    private byte[] getBytesFromBuffer(int numberOfBytes) throws IOException {
        byte[] b = new byte[numberOfBytes];
        for (int i = 0; i < numberOfBytes; i++) {
            b[i] = getByteFromBuffer();
        }
        return b;
    }

    private void fillBuffer() throws IOException {
        currentReadPosition = 0;
        dataSizeInBuffer = 0;
        dataSizeInBuffer = inputStream.read(buffer);
    }

    public byte readByte() throws IOException {
        return getByteFromBuffer();
    }

    public short readShort() throws IOException {
        return PrimitiveSerializer.bytesToShort(getBytesFromBuffer(2));
    }

    public int readInt() throws IOException {
        return PrimitiveSerializer.bytesToInt(getBytesFromBuffer(4));
    }

    public long readLong() throws IOException {
        return PrimitiveSerializer.bytesToLong(getBytesFromBuffer(8));
    }

    public float readFloat() throws IOException {
        return PrimitiveSerializer.bytesToFloat(getBytesFromBuffer(4));
    }

    public double readDouble() throws IOException {
        return PrimitiveSerializer.bytesToDouble(getBytesFromBuffer(8));
    }

    public char readChar() throws IOException {
        return PrimitiveSerializer.bytesToChar(getBytesFromBuffer(2));
    }

    public String readString() throws IOException {
        String s = "";
        int length = readInt();
        for (int i = 0; i < length; i++) {
            s = s + readChar();
        }
        return s;
    }

    public boolean readBoolaen() throws IOException {
        return PrimitiveSerializer.bytesToBoolean(getBytesFromBuffer(1));
    }

    public void close() throws IOException {
        inputStream.close();
    }
}