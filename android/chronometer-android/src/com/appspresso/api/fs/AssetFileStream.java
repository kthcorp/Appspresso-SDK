/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.fs;

import java.io.IOException;
import java.io.InputStream;

/**
 * *** INTERNAL USE ONLY ***
 * 
 * @version 1.0
 */
public class AssetFileStream implements AxFileStream {
    private final InputStream inputStream;
    private final long length;

    AssetFileStream(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.inputStream.mark(0);
        this.length = inputStream.available();
    }

    @Override
    public void open() throws IOException {
        // Nothing to do.
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public boolean isEof() throws IOException {
        return inputStream.available() < 1;
    }

    @Override
    public synchronized void seek(long position) throws IOException {
        inputStream.reset();
        inputStream.skip(position);
    }

    @Override
    public long getPosition() throws IOException {
        return length - inputStream.available();
    }

    @Override
    public synchronized byte[] read(int size) throws IOException {
        byte[] buffer = new byte[size];

        int newSize = inputStream.read(buffer);
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
        // Nothing to do.
    }
}
