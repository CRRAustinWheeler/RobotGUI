/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package communications;

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

    public void close() throws IOException {
        inputStream.close();
    }
}