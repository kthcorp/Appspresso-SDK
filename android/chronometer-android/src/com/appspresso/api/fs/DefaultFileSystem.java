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
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;

/**
 * {@link java.io.File}을 추상화한 앱스프레소 가상 파일시스템({@link AxFileSystem}) 구현체.
 * 
 * @version 1.0
 * @see java.io.File
 * @see DefaultFile
 */
public class DefaultFileSystem implements AxFileSystem {
    private static Log L = AxLog.getLog(AxFileSystem.class);

    private final File rootDirectory;
    private DefaultFile rootAxFile;
    private Map<String, DefaultFile> cache; // key : native absolute path
    private boolean canRead;
    private boolean canWrite;

    public DefaultFileSystem(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMount(String prefix, Properties options) {
        if (!rootDirectory.isDirectory()) {
            if (L.isDebugEnabled()) {
                L.debug("The root is invalid.");
            }
            return;
        }

        rootAxFile = new DefaultFile(this, rootDirectory, prefix);
        cache = new WeakHashMap<String, DefaultFile>();
        setRead(/* rootAxFile.canRead() */true);
        setWrite(/* rootAxFile.canWrite() */true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUnmount() {
        cache.clear();
        setRead(false);
        setWrite(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AxFile getFile(String path) {
        if (!canRead) {
            if (L.isDebugEnabled()) {
                L.debug("This file system is not readable.");
            }
            return null;
        }

        File file = new File((File) rootAxFile.getPeer(), path);
        return createDefaultFile(file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AxFile getRoot() {
        if (!canRead) {
            if (L.isDebugEnabled()) {
                L.debug("This file system is not readable.");
            }
            return null;
        }

        return rootAxFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canRead() {
        return canRead;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canWrite() {
        return canWrite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toNativePath(String path) {
        // String rootPath = rootAxFile.getPath();
        //
        // // 앞뒤의 "/"만 제외해준다.
        // if(path.charAt(0) == File.separatorChar) path = path.substring(1);
        // if(path.charAt(path.length() - 1) == File.separatorChar) path =
        // path.substring(path.length() - 1);
        //
        // if(!path.startsWith(rootPath)) return null;
        // int index = rootPath.length();
        // if(path.charAt(index) != File.separatorChar) return null;
        // String subPath = path.substring(index);
        //
        // return new File(rootDirectory, subPath).getAbsolutePath();

        // The path is a relative path.
        return new File(rootDirectory, path).getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toVirtualPath(String path) {

        // String rootPath = null;
        // try {
        // // 항상 "/"가 제일 앞에 오는 형태로 반환하므로 이를 제외해 준다.
        // rootPath = rootDirectory.getAbsolutePath().substring(1);
        // } catch(IndexOutOfBoundsException e) {
        // // IndexOutOfBoundsException가 발생한다면 잘못된 File.
        // return null;
        // }
        //
        // // 앞뒤의 "/"만 제외해준다.
        // if(path.charAt(0) == File.separatorChar) path = path.substring(1);
        // if(path.charAt(path.length() - 1) == File.separatorChar) path =
        // path.substring(path.length() - 1);
        //
        // if(path.startsWith(rootPath)) return
        // path.substring(rootPath.length());
        //
        // return null;

        // The path is a native full path.
        String nativeRootPath = rootDirectory.getAbsolutePath().substring(1);

        // 앞뒤의 "/"만 제외해준다.
        try {
            if (nativeRootPath.charAt(0) == File.separatorChar)
                nativeRootPath = nativeRootPath.substring(1);
            if (nativeRootPath.charAt(nativeRootPath.length() - 1) == File.separatorChar)
                nativeRootPath = nativeRootPath.substring(0, nativeRootPath.length() - 1);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }

        // 앞뒤의 "/"만 제외해준다.
        try {
            if (path.charAt(0) == File.separatorChar) path = path.substring(1);
            if (path.charAt(path.length() - 1) == File.separatorChar)
                path = path.substring(0, path.length() - 1);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }

        String rootPath = rootAxFile.getPath();
        if (path.startsWith(nativeRootPath))
            return rootPath + path.substring(nativeRootPath.length());
        return null;
    }

    /**
     * 파일의 읽기가능 여부를 설정.
     * 
     * @param canRead 읽기가 가능하도록 설정하려면 {@literal true}, 아니면 {@literal false}
     * @since 1.0
     */
    protected void setRead(boolean canRead) {
        this.canRead = canRead;
    }

    /**
     * 파일의 쓰기가능 여부를 설정.
     * 
     * @param canWrite 쓰기가 가능하도록 설정하려면 {@literal true}, 아니면 {@literal false}
     * @since 1.0
     */
    protected void setWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    /**
     * File에 대한 DefaultFile을 생성하여 반환.
     * 
     * @param peer File 객체
     * @return File에 대한 DefaultFile
     * @since 1.0
     */
    protected synchronized DefaultFile createDefaultFile(File peer) {
        if (peer.equals(rootAxFile.getPeer())) return (DefaultFile) rootAxFile;

        String cacheKey = peer.getAbsolutePath();
        DefaultFile axFile = cache.get(cacheKey);
        if (null != axFile) return axFile;

        DefaultFile parent = createDefaultFile(peer.getParentFile()); // Warning!
                                                                      // Recursive
                                                                      // call.
        axFile = new DefaultFile(this, parent, peer);

        cache.put(cacheKey, axFile);
        return axFile;
    }
}
