/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.fs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;

/**
 * 안드로이드의 asset을 추상화한 앱스프레소 가상 파일({@link AxFile}) 구현체.
 * 
 * @version 1.0
 * @see AssetFileSystem
 * @see android.content.res.AssetManager
 * @see android.content.res.AssetFileDescriptor
 */
public class AssetFile implements AxFile {
    private static final Log L = AxLog.getLog(AxFile.class);

    private final AssetFileSystem fileSystem;
    private final AssetFile parent;
    private final AssetFilePeer peer;
    private AxFileStream fileStream;
    private String name;

    AssetFile(AssetFileSystem fileSystem, AssetFile parent, AssetFilePeer peer) {
        this.fileSystem = fileSystem;
        this.parent = parent;
        this.peer = peer;
    }

    AssetFile(AssetFileSystem fileSystem, AssetFilePeer peer, String name) {
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
    public String getPath() {
        return (parent != null) ? parent.getPath() + File.separator + peer.getName() : name;
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
    public boolean isFile() {
        return peer.isFile();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean isDirectory() {
        return peer.isDirectory();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public long getLength() {
        return peer.getLength();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * NOTE : 항상 0을 반환.
     */
    @Override
    public long getCreated() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * NOTE : 항상 0을 반환.
     */
    @Override
    public long getModified() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean exists() {
        return peer.exists();
    }

    /**
     * {@inheritDoc}
     * 
     * @Override
     */
    public boolean canRead() {
        return exists();
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean canWrite() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public AxFile[] listFiles(AxFileFilter filter) {
        if (!peer.isDirectory()) return null;

        String[] nameArray = null;
        try {
            nameArray = peer.list();
        }
        catch (IOException e) {
            return null;
        }

        int length = nameArray.length;
        AxFile temp;

        if (null == filter) {
            AxFile[] axFileArray = new AxFile[length];
            for (int i = 0; i < length; i++) {
                axFileArray[i] =
                        fileSystem.createAssetFile(peer.createChildAssetPeer(nameArray[i]));
            }
            return axFileArray;
        }
        else {
            ArrayList<AxFile> fileList = new ArrayList<AxFile>();
            for (int i = 0; i < length; i++) {
                temp = fileSystem.createAssetFile(peer.createChildAssetPeer(nameArray[i]));
                if (filter.acceptFile(temp)) fileList.add(temp);
            }
            return fileList.toArray(new AxFile[fileList.size()]);
        }
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
     * 파일을 읽기 전용 모드로 연다.
     * 
     * @throws IOException 파일을 열 수 없음.
     * @since 1.0
     */
    @Override
    public void open() throws IOException {
        open(MODE_READ);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * NOTE : 읽기 모드만 가능
     * </p>
     */
    @Override
    public void open(int mode) throws IOException {
        if (fileStream != null) throw new IOException("The stream already was opened.");
        if (!peer.isFile()) throw new IOException("It is an invalid file.");

        switch (mode) {
            case MODE_READ:
                fileStream = new AssetFileStream(peer.createInputStream());
                break;
            default:
                throw new IOException("It is an unsupported mode.");
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
            if (L.isErrorEnabled()) {
                L.error("An unknown error has occurred.", e);
            }
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
        if (fileStream == null) throw new IOException("The stream was not opened.");
        throw new IOException( /* TODO Error message */);
    }
}
