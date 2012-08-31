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

import java.util.Properties;

/**
 * 앱스프레소의 가상 파일시스템.
 * 
 * @version 1.0
 * @see AxFile
 * @see AxFileSystemManager
 */
public interface AxFileSystem {
    /**
     * AxFileSystem 마운트 과정 중에 호출되는 이벤트 메소드. 마운트와 관련한 동작을 정의.
     * 
     * @param prefix prefix for mounted filesystem. similar to mount-point.
     * @param options AxFileSystem을 mount하는데 필요한 옵션
     * @since 1.0
     */
    void onMount(String prefix, Properties options);

    /**
     * AxFileSystem이 마운트 해제 되는 중에 호출되는 이벤트 메소드.
     * 
     * @since 1.0
     */
    void onUnmount();

    /**
     * AxFileSystem의 root가 되는 AxFile을 반환.
     * 
     * @return Root AxFile
     * @since 1.0
     */
    AxFile getRoot();

    /**
     * 지정한 경로에 해당되는 AxFileSystem의 하위 AxFile을 반환.
     * 
     * @param path relative path to filesystem root(without filesystem prefix)
     * @return AxFile. 해당되는 AxFile이 존재하지 않을 시 {@literal null}
     * @since 1.0
     */
    AxFile getFile(String path);

    /**
     * AxFileSystem이 읽기 가능 여부를 반환.
     * 
     * @return 읽기 가능하면 {@literal true}, 불가능하면 {@literal false}
     * @since 1.0
     */
    boolean canRead();

    /**
     * AxFileSystem이 쓰기 가능 여부를 반환.
     * 
     * @return 쓰기 가능하면 {@literal true}, 불가능하면 {@literal false}
     * @since 1.0
     */
    boolean canWrite();

    /**
     * 가상 경로를 실제 파일의 절대 경로로 변환.
     * 
     * @param path "virtual" path (without the prefix)
     * @return "native" path (with the prefix)
     * @throw UnsupportedOperationException 지정한 가상 경로가 이 파일시스템에 속하지 않음.
     * @since 1.0
     */
    String toNativePath(String path);

    /**
     * 실제 파일의 절대 경로를 가상 경로로 변환.
     * 
     * @param path "native" path (with the prefix)
     * @return "virtual" path (with the prefix)
     * @throw UnsupportedOperationException 지정한 실제 경로가 이 파일시스템에 속하지 않음.
     * @since 1.0
     */
    String toVirtualPath(String path);
}
