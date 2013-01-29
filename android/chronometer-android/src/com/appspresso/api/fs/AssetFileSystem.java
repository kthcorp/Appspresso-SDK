/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api.fs;

import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;
import android.content.res.AssetManager;
import com.appspresso.api.AxLog;

/**
 * 안드로이드의 asset 추상화한 앱스프레소 가상 파일시스템({@link AxFileSystem}) 구현체.
 * 
 * @version 1.0
 * @see AssetFile
 * @see android.content.res.AssetManager
 * @see android.content.res.AssetFileDescriptor
 */
public class AssetFileSystem implements AxFileSystem {
    private static Log L = AxLog.getLog(AxFileSystem.class);

    private final AssetManager assetManager;
    private final String rootAssetPath;
    private AssetFile rootAxFile;
    private boolean canRead;
    private Map<String, AssetFile> cache; // key : native path

    public AssetFileSystem(AssetManager assetManager, String rootAssetPath) {
        this.assetManager = assetManager;
        this.rootAssetPath = rootAssetPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMount(String prefix, Properties options) {
        if (FileSystemUtils.isEmptyPath(rootAssetPath)) {
            if (L.isDebugEnabled()) L.debug("The root cannot be mounted.");
            return;
        }

        AssetFilePeer peer = new AssetFilePeer(assetManager, rootAssetPath);
        if (!peer.exists()) {
            if (L.isDebugEnabled()) L.debug("The root is invalid.");
            return;
        }

        if (!peer.isDirectory()) {
            if (L.isDebugEnabled()) L.debug("The root is not a directory.");
            return;
        }

        // rootAxFile = new AssetFile(this, null /* no parent */, peer);
        rootAxFile = new AssetFile(this, peer, prefix);
        cache = new WeakHashMap<String, AssetFile>();
        canRead = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUnmount() {
        cache.clear();
        canRead = false;
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

        return createAssetFile(((AssetFilePeer) rootAxFile.getPeer()).createChildAssetPeer(path));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AxFile getRoot() {
        if (!canRead) {
            if (L.isWarnEnabled()) {
                L.warn("This file system is not readable.");
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
     * 
     * NOTE : 항상 {@literal false}를 턴
     */
    @Override
    public boolean canWrite() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * NOTE : 안드로이드 에서 asset 은 실제 경로를 갖지 않으므로, 항상 {@link UnsupportedOperationException} 예외 발생
     */
    @Override
    public String toNativePath(String path) {
        // asset은 물리적인 경로가 없음.
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * NOTE : 안드로이드 에서 asset 은 실제 경로를 갖지 않으므로, 항상 {@link UnsupportedOperationException} 예외 발생
     */
    @Override
    public String toVirtualPath(String path) {
        // asset은 물리적인 경로가 없음.
        throw new UnsupportedOperationException();
    }

    /**
     * AssetFilePeer에 대한 AssetFile을 생성하여 반환.
     * 
     * @param peer AssetFilePeer
     * @return AssetFilePeer에 대한 AssetFile
     */
    protected synchronized AssetFile createAssetFile(AssetFilePeer peer) {
        String assetPath = peer.getAssetPath();
        if (assetPath.equals(rootAssetPath)) return rootAxFile;

        String cacheKey = assetPath;
        AssetFile axFile = cache.get(cacheKey);
        if (null != axFile) return axFile;

        AssetFile parent = createAssetFile(peer.createParentAssetPeer()); // Warning!
                                                                          // Recursive
                                                                          // call.
        axFile = new AssetFile(this, parent, new AssetFilePeer(assetManager, assetPath));

        cache.put(cacheKey, axFile);
        return axFile;
    }
}
