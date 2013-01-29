/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.fs;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * *** INTERNAL USE ONLY ***
 * 
 * @version 1.0
 */
public class DefaultFileStream implements AxFileStream {
    private RandomAccessFile raFile;

    DefaultFileStream(RandomAccessFile randomAccessFile) {
        this.raFile = randomAccessFile;
    }

    @Override
    public void open() throws IOException {
        // Nothing to do.
    }

    @Override
    public synchronized void close() throws IOException {
        if (null == raFile) { return; }

        raFile.close();
    }

    @Override
    public synchronized boolean isEof() throws IOException {
        return raFile.length() == raFile.getFilePointer();
    }

    @Override
    public synchronized void seek(long position) throws IOException {
        raFile.seek(position);
    }

    @Override
    public long getPosition() throws IOException {
        return raFile.getFilePointer();
    }

    @Override
    public synchronized byte[] read(int size) throws IOException {
        byte[] buffer = new byte[size];

        int newSize = raFile.read(buffer);
        if (newSize != -1) {
            if (newSize != size) {
                byte[] newBuffer = new byte[newSize];
                System.arraycopy(buffer, 0, newBuffer, 0, newSize);
                buffer = newBuffer;
            }
        }
        else {
            buffer = new byte[0];
        }

        return buffer;
    }

    @Override
    public void write(byte[] data) throws IOException {
        raFile.write(data);
    }
}
