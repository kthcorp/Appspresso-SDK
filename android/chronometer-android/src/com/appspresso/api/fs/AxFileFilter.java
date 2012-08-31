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

/**
 * 앱스프레소 가상 파일시스템의 파일 목록을 필터링하는 인터페이스.
 * 
 * @see AxFile#listFiles(AxFileFilter)
 * @see java.io.FileFilter
 * @see java.io.FilenameFilter
 */
public interface AxFileFilter {

    /**
     * 주어진 파일이 필터링된 결과에 포함될지 여부.
     * 
     * NOTE : 1.0의 accept
     * 
     * @param file 앱스프레소 가상 파일
     * @return 결과에 포함된다면 {@literal true}, 아니면 {@literal false}
     * @since 1.1
     */
    boolean acceptFile(AxFile file);
}
