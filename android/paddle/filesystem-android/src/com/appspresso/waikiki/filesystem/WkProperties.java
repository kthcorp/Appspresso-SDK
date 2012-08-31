package com.appspresso.waikiki.filesystem;

public interface WkProperties {
    /**
     * 이 WkProperties의 부모 파일에 해당하는 WkFileHandle를 반환한다. WkProperties가 어떤 파일시스템의 루트에 해당한다면 null을 반환한다.
     * 
     * @return 부모 WkFileHandle. 루트일 경우는 null
     */
    public WkFileHandle getParent();

    /**
     * 파일이 읽기전용인지 반환한다. 실제 존재하는 물리적인 파일의 읽기전용 여부이다.
     * 
     * @return 파일이 읽기만 가능할 경우 true, 그렇지 않을 경우 false를 반환한다.
     */
    public boolean readOnly();

    /**
     * 이 파일 핸들이 가리키는 파일이 디렉토리가 아닌 파일인지 반환한다.
     * 
     * @return 파일일 경우 true, 디렉토리일 경우 false를 반환한다.
     */
    public boolean isFile();

    /**
     * 이 파일 핸들이 가리키는 파일이 파일이 아닌 디렉토리인지 반환한다.
     * 
     * @return 디렉토리일 경우 true, 파일일 경우 false를 반환한다.
     */
    public boolean isDirectory();

    /**
     * 이 파일이 생성된 시기의 timestamp를 반한다. 생성 시기를 알 수 없을 경우에는 -1를 반환한다.
     * 
     * @return 파일이 생성된 시기의 timestamp
     */
    public long getCreated();

    /**
     * 이 파일이 마지막으로 수정된 시기의 timestamp를 반한다. 수정 시기를 알 수 없을 경우에는 -1를 반환한다.
     * 
     * @return 파일이 마지막으로 수정된 시기의 timestamp
     */
    public long getModified();

    /**
     * 이 파일의 바이트 단위의 크기를 반환한다. 파일 크기를 알 수 없거나 파일 핸들이 디렉토리를 가리킬 경우 -1을 반환한다.
     * 
     * @return 파일의 바이트 단위의 크기. 알 수 없을 경우 -1
     */
    public long getFileSize();

    /**
     * 이 디렉토리의 하위 디렉토리와 파일의 개수를 반환한다. 개수를 알 수 없거나 파일 핸들이 디렉토리를 가리키지 않을 경우 -1를 반환한다.
     * 
     * @return 디렉토리의 하위 디렉토리, 파일의 개수. 알 수 없을 경우 -1
     */
    public long getLength();
}
