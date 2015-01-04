/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coderedrobotics.dashboard.communications;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author austin
 */
public class NonblockingOutputBuffer {

    private final OutputStream outputStream;
    private Buffer buffer;

    public NonblockingOutputBuffer(OutputStream outputStream) {
        if (outputStream == null) {
            throw new NullPointerException();
        }
        this.outputStream = outputStream;
        buffer = new Buffer();
    }

    public synchronized void writeByte(byte b) {
        buffer.addByte(b);
    }

    public void flush() throws IOException {
        Buffer buffer;
        synchronized (this) {
            buffer = this.buffer;
            this.buffer = new Buffer();
        }
        synchronized (outputStream) {
            for (int i = 0; i < buffer.currentBlock; i++) {
                buffer.totalBytesInBuffer -= buffer.buffer[i].length;
                outputStream.write(buffer.buffer[i]);
            }
            outputStream.write(buffer.buffer[buffer.currentBlock], 0,
                    buffer.totalBytesInBuffer);
            outputStream.flush();
        }
    }

    public void close() throws IOException {
        flush();
        synchronized (outputStream) {
            outputStream.close();
        }
    }

    private class Buffer {

        private int freeSpace = 256;
        int totalBytesInBuffer = 0;
        int currentBlockSize = 256;
        private int currentBlock = 0;
        byte[][] buffer = new byte[32][];

        public Buffer() {
            buffer[0] = new byte[256];
        }

        void addByte(byte b) {
            if (freeSpace == 0) {
                makeLonger();
            }
            buffer[currentBlock][currentBlockSize - freeSpace] = b;
            totalBytesInBuffer++;
            freeSpace--;
        }

        void makeLonger() {
            currentBlockSize += currentBlockSize;
            currentBlock++;
            freeSpace = currentBlockSize;
            buffer[currentBlock] = new byte[currentBlockSize];
        }
    }
}
