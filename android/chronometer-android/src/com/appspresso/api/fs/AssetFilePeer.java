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

import java.io.IOException;
import java.io.InputStream;
import android.content.res.AssetManager;

/**
 * *** INTERNAL USE ONLY ***
 * 
 * @version 1.0
 */
public class AssetFilePeer {
    private final AssetManager assetManager;
    private final String assetPath;

    private boolean validated;
    private boolean directory;
    private boolean file;
    private int length;

    public AssetFilePeer(AssetManager assetManager, String assetPath) {
        this.assetManager = assetManager;
        this.assetPath = assetPath;

        // validated = false;
        // directory = false;
        // file = false;
        // length = 0;
    }

    /**
     * 현재 디렉토리의 하위 파일에 대한 AssetFilePeer를 반환.
     * 
     * @param name 하위 파일의 이름
     * @return 현재 디렉토리의 하위 파일의 AssetFilePeer
     * @since 1.0
     */
    public AssetFilePeer createChildAssetPeer(String name) {
        // return new AssetFilePeer(assetManager, assetPath + "/" + name);
        return new AssetFilePeer(assetManager, FileSystemUtils.mergePath(assetPath, name));
    }

    /**
     * 현재 파일의 부모 파일에 대한 AssetFilePeer를 반환.
     * 
     * @return 현재 디렉토리의 부모 파일의 AssetFilePeer
     * @since 1.0
     */
    public AssetFilePeer createParentAssetPeer() {
        String parentAssetPath = assetPath.substring(0, assetPath.lastIndexOf("/"));
        return new AssetFilePeer(assetManager, parentAssetPath);
    }

    /**
     * 현재 디렉토리에 대한 asset 경로를 반환.
     * 
     * @return asset 경로
     * @since 1.0
     */
    public String getAssetPath() {
        return assetPath;
    }

    /**
     * 파일의 이름을 반환.
     * 
     * @return 파일의 이름
     * @since 1.0
     */
    public String getName() {
        return FileSystemUtils.extractName(assetPath);
    }

    /**
     * 파일의 존재여부를 반환.
     * 
     * @return 존재하면 {@literal true}. 존재하지 않으면 {@literal false}
     * @since 1.0
     */
    public boolean exists() {
        validateAsset();
        return file || directory;
    }

    /**
     * 디렉토리인지 아닌지 식별.
     * 
     * @return 디렉토리면 {@literal true}, 디렉토리가 아니면 {@literal false}
     * @since 1.0
     */
    public boolean isDirectory() {
        validateAsset();
        return directory;
    }

    /**
     * 파일인지 아닌지 식별.
     * 
     * @return 파일이면 {@literal true}, 파일이 아니면 {@literal false}
     * @since 1.0
     */
    public boolean isFile() {
        validateAsset();
        return file;
    }

    /**
     * 현재 파일의 길이는 반환.
     * 
     * @return 파일의 길이. 파일이 아닐 경우에는 0.
     * @since 1.0
     */
    public int getLength() {
        validateAsset();
        return length;
    }

    /**
     * 현재 파일에 대한 InputStream을 반환.
     * 
     * @return 현재 파일에 대한 InputStream
     * @throws IOException 파일을 열 수 없거나 여는 도중 에러 발생.
     * @since 1.0
     */
    public InputStream createInputStream() throws IOException {
        validateAsset();
        return assetManager.open(assetPath, AssetManager.ACCESS_STREAMING);
    }

    /**
     * 현재 디렉토리의 하위파일들의 이름 목록을 반환. 파일이 존재하지 않거나
     * 
     * @return 하위파일들의 목록
     * @throws IOException 목록을 반환할 수 없거나 에러 발생.
     * @since 1.0
     */
    public String[] list() throws IOException {
        return assetManager.list(assetPath);
    }

    /**
     * 이 객체가 가리키는 실제 Asset File을 검증함.
     * <p>
     * 실제 이 객체가 가리키는 Asset File에 대한 속성을 가려오려 할 때 호출함. 한번 검증한 대상에 대해서는 재검증을 하지 않음. 재검증을 위해서는 직접
     * validated를 false로 설정한 후 호출함.
     */
    private void validateAsset() {
        if (validated) return;

        try {
            synchronized (this) {
                // XXX:
                InputStream in = null;
                try {
                    in = assetManager.open(assetPath);

                    // file
                    file = true;
                    length = in.available();
                    return;
                }
                catch (IOException e) {
                    // Nothing to do.
                }
                finally {
                    FileSystemUtils.closeQuietly(in);
                }

                try {
                    String[] names = assetManager.list(assetPath);

                    // directory
                    directory = null != names && 0 < names.length;
                    return;
                }
                catch (IOException e) {}

                // neither file nor directory
            }
        }
        finally {
            validated = true;
        }
    }
}
