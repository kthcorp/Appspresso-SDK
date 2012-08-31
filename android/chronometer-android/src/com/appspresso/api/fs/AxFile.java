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
 * 앱스프레소의 가상 파일.
 * 
 * @version 1.0
 * @see AxFileSystem
 * @see AxFileSystemManager
 */
public interface AxFile {
    /**
     * 읽기 모드
     * 
     * @since 1.0
     */
    static int MODE_READ = 0x81;

    /**
     * 쓰기 모드
     * 
     * @since 1.0
     */
    static int MODE_WRITE = 0x82;

    /**
     * 덧붙이기 모드
     * 
     * @since 1.0
     */
    static int MODE_APPEND = 0x84;

    /**
     * 파일 이름을 반환.
     * 
     * @return 파일 이름
     * @since 1.0
     */
    String getName();

    /**
     * 파일시스템의 이름으로 시작하는 가상 파일 경로를 반환
     * 
     * @return 파일의 가상 경로
     * @since 1.0
     */
    String getPath();

    /**
     * 부모 파일을 반환
     * 
     * @return 부모 파일의 인스턴스. 단, 파일이 root 이면 {@literal null} 을 반환.
     */
    AxFile getParent();

    /**
     * 파일인지 아닌지 식별.
     * 
     * @return 파일이면 {@literal true}, 아니면 {@literal false}
     * @since 1.0
     */
    boolean isFile();

    /**
     * 디렉토리인지 아닌지 식별.
     * 
     * @return 디렉토리이면 {@literal true}, 아니면 {@literal false}
     * @since 1.0
     */
    boolean isDirectory();

    /**
     * 파일의 크기(byte)를 반환. 파일이 아니면 0을 반환.
     * 
     * @return 파일의 길이.
     * @since 1.0
     */
    long getLength();

    /**
     * 생성일자를 반환.
     * 
     * @return 생성일자. 알 수 없으면 임의의 날짜.
     * @since 1.0
     */
    long getCreated();

    /**
     * 파일이 마지막으로 수정된 일자를 반환.
     * 
     * @return 수정일자. 알 수 없으면 임의의 날짜.
     * @since 1.0
     */
    long getModified();

    /**
     * 파일의 존재 여부를 반환.
     * 
     * @return 존재하면 {@literal true}, 아니면 {@literal false}
     * @since 1.0
     */
    boolean exists();

    /**
     * 파일의 쓰기 가능 여부를 반환.
     * 
     * @return 쓸 수 있으면 {@literal true}, 아니면 {@literal false}
     * @since 1.0
     */
    boolean canWrite();

    /**
     * 파일이 읽기 가능 여부를 반환.
     * 
     * @return 읽을 수 있으면 {@literal true}, 아니면 {@literal false}
     * @since 1.0
     */
    boolean canRead();

    /**
     * 해당 조건을 만족하는 파일 목록을 반환.
     * 
     * @param filter 하위 파일을 가져오기 위한 조건. {@literal null}이면 모든 파일을 가져옴.
     * @return 필터에 만족하는 파일 객체 배열
     * @since 1.0
     */
    AxFile[] listFiles(AxFileFilter filter);

    /**
     * 해당 파일 객체를 식별할 데이터를 반환.
     * 
     * @return 객체 식별용 고유값
     * @since 1.0
     */
    Object getPeer();

    /**
     * 읽고 쓰기 위한 상태로 파일을 연다.
     * 
     * @param mode 모드. {@literal |} 연산자를 이용하여 조합이 가능. (e.g. MODE_READ | MODE_WRITE)
     * @see #MODE_READ
     * @see #MODE_WRITE
     * @see #MODE_APPEND
     * @throws IOException 파일을 열 수 없음.
     * @since 1.0
     */
    void open(int mode) throws IOException;

    /**
     * 기본 상태로 파일을 연다.
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
     * 파일포인터가 파일의 끝에 위치하고 있는지 확인.
     * 
     * @return eof 일 경우 {@literal true}, 아니면 {@literal false}
     * @throws IOException 파일의 끝에 위치하는지 판단할 수 없거나 에러 발생.
     * @since 1.0
     */
    boolean isEof() throws IOException;

    /**
     * 지정한 위치로 파일포인터를 이동.
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
     * 현재 파일포인터의 위치부터 지정한 크기만큼 읽음. 앞으로 읽을 수 있는 크기가 지정한 크기보다 작을 경우 가능한 크기 만큼만 읽는다.
     * 
     * @param size 읽어 올 크기
     * @return 읽어온 바이트 배열
     * @throws IOException 데이터를 읽을 수 없거나 읽는 도중 에러 발생.
     * @since 1.0
     */
    byte[] read(int size) throws IOException;

    /**
     * 현재 파일포인터의 위치부터 데이터를 쓴다.
     * 
     * @param data 쓰고자 하는 데이터
     * @throws IOException 데이터를 쓸 수 없거나 쓰는 도중 에러 발생.
     * @since 1.0
     */
    void write(byte[] data) throws IOException;
}
