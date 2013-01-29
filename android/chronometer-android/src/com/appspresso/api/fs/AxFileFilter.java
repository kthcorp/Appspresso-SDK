/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
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
