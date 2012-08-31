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

/**
 * *** INTERNAL USE ONLY ***
 * 
 * @version 1.0
 */
public interface AxFileStream {

    /**
     * 파일을 읽고 쓰기 위한 스트림을 기본모드로 염.
     * 
     * @throws IOException 파일을 열 수 없음.
     * @since 1.0
     */
    void open() throws IOException;

    /**
     * 파일을 닫음.
     * 
     * @throws IOException 파일을 닫을 수 없음.
     * @since 1.0
     */
    void close() throws IOException;

    /**
     * 파일포인터가 파일의 끝에 위치하는지 여부.
     * 
     * @return 파일의 끝에 위치할 경우 {@literal true}, 그렇지 않으면 {@literal false}
     * @throws IOException 파일의 끝에 위치하는지 판단할 수 없거나 에러 발생.
     * @since 1.0
     */
    boolean isEof() throws IOException;

    /**
     * 파일포인터를 특정 위치로 이동시킴.
     * 
     * @param position 파일포인터를 이동시킬 위치
     * @throws IOException 파일포인터를 이동시킬 수 없거나 이동 중 에러 발생.
     * @since 1.0
     */
    void seek(long position) throws IOException;

    /**
     * 파일포인터의 현재 위치를 반환.
     * 
     * @return 파일포인터의 현재 위치.
     * @throws IOException 파일포인터의 현재 위치를 반환할 수 없거나 에러 발생.
     * @since 1.0
     */
    long getPosition() throws IOException;

    /**
     * 현재 위치에서 지정한 크기만큼 읽어옴. 지정한 크기보다 읽을 수 있는 크기가 더 작을 경우에는 읽을 수 있는만큼만 읽어옴.
     * 
     * @param size 읽어올 크기
     * @return 읽어온 바이트 배열
     * @throws IOException 데이터를 읽을 수 없거나 읽는 도중 에러 발생.
     * @since 1.0
     */
    byte[] read(int size) throws IOException;

    /**
     * 지정한 크기만큼 파일의 현재 위치에서부터 데이터를 씀.
     * 
     * @param data 쓸 데이터
     * @throws IOException 데이터를 쓸 수 없거나 쓰는 도중 에러 발생.
     * @since 1.0
     */
    void write(byte[] data) throws IOException;
}
