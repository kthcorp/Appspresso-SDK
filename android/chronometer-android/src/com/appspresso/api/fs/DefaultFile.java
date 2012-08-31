/*
 * Appspresso
 * 
 * Copyright (c) 2011 KT Hitel Corp.
 * 
 * This source is subject to Appspresso license terms. Please see http://appspresso.com/ for more
 * information.
 * 
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package com.appspresso.api.fs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;

/**
 * {@link java.io.File}을 추상화한 앱스프레소 가상 파일({@link AxFile}) 구현체.
 * 
 * @version 1.0
 * @see java.io.File
 * @see DefaultFileSystem
 */
public class DefaultFile implements AxFile {
    private static final Log L = AxLog.getLog(AxFile.class);
    public static final int MODE_READ_WRITE = (MODE_READ | MODE_WRITE);

    private final DefaultFileSystem fileSystem;
    private final DefaultFile parent;
    private final File peer;
    private String name;
    private AxFileStream fileStream;

    DefaultFile(DefaultFileSystem fileSystem, DefaultFile parent, File peer) {
        this.fileSystem = fileSystem;
        this.parent = parent;
        this.peer = peer;
    }

    DefaultFile(DefaultFileSystem fileSystem, File peer, String name) {
        this(fileSystem, null, peer);
        this.name = name;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public String getName() {
        return (name == null) ? peer.getName() : name;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public AxFile getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public String getPath() {
        return (parent != null) ? parent.getPath() + File.separator + peer.getName() : name;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean isFile() {
        if (isHiddenFile(peer)) return false;
        return peer.isFile();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean isDirectory() {
        if (isHiddenFile(peer)) return false;
        return peer.isDirectory();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public long getLength() {
        return peer.length();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public long getCreated() {
        return peer.lastModified();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public long getModified() {
        return peer.lastModified();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean exists() {
        if (isHiddenFile(peer)) return false;
        return peer.exists();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean canRead() {
        if (isHiddenFile(peer)) return false;
        return peer.canRead();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean canWrite() {
        if (isHiddenFile(peer)) return false;
        return peer.canWrite();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public AxFile[] listFiles(AxFileFilter filter) {
        if (isHiddenFile(peer)) return null;

        String[] fileArray = peer.list();
        if (null == fileArray) return null;

        int length = fileArray.length;

        ArrayList<AxFile> axFileList = new ArrayList<AxFile>();
        File newFile;
        AxFile newAxFile;

        if (filter == null) {
            for (int i = 0; i < length; i++) {
                newFile = new File(peer, fileArray[i]);
                if (newFile.isHidden()) continue;

                newAxFile = fileSystem.createDefaultFile(newFile);
                axFileList.add(newAxFile);
            }
        }
        else {
            for (int i = 0; i < length; i++) {
                newFile = new File(peer, fileArray[i]);
                if (newFile.isHidden()) continue;

                newAxFile = fileSystem.createDefaultFile(newFile);
                if (filter.acceptFile(newAxFile)) axFileList.add(newAxFile);
            }
        }

        return axFileList.toArray(new AxFile[axFileList.size()]);
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public Object getPeer() {
        return peer;
    }

    /**
     * 읽고 쓰기가 가능한 상태 파일을 연다.
     * 
     * @throws IOException 파일을 열 수 없음.
     * @since 1.0
     */
    @Override
    public void open() throws IOException {
        open(MODE_READ | MODE_WRITE);
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE : 읽고 쓰기가 가능한 상태만 지원.
     * </p>
     */
    @Override
    public void open(int mode) throws IOException {
        if (fileStream != null) { throw new IOException("The stream already was opened."); }

        switch (mode) {
        // case MODE_READ :
        // // TODO
        // break;
        // case MODE_WRITE :
        // // TODO
        // break;
            case MODE_READ_WRITE:
                fileStream = new DefaultFileStream(new RandomAccessFile(peer, "rws"));
                break;
            default:
                throw new IOException("It's an unsupported mode.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public void close() throws IOException {
        if (fileStream == null) throw new IOException("The stream was not opened.");

        try {
            fileStream.close();
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) L.error("An unknown error has occurred.", e);
        }
        finally {
            fileStream = null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean isEof() throws IOException {
        try {
            return fileStream.isEof();
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public void seek(long position) throws IOException {
        try {
            fileStream.seek(position);
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public long getPosition() throws IOException {
        try {
            return fileStream.getPosition();
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public byte[] read(int size) throws IOException {
        try {
            return fileStream.read(size);
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public void write(byte[] data) throws IOException {
        try {
            fileStream.write(data);
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     */
    private boolean isHiddenFile(File file) {
        if (file.isHidden()) {
            if (L.isWarnEnabled()) {
                L.warn("It's the hidden file.");
            }
            return true;
        }
        return false;
    }
}
