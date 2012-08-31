package com.appspresso.waikiki.filesystem;

import com.appspresso.api.AxError;

public interface WkFile {
    /**
     * 주어진 인코딩으로 파일의 문자열을 읽어온다.
     * 
     * @param encoding 인코딩
     * @return 읽어온 문자열
     * @throws AxError 문자열을 읽어오는데 실패했을 경우 발생
     */
    public String readAsText(String encoding) throws AxError;

    /**
     * 주어진 모드와 인코딩으로 파일의 스트림을 연 후 이를 핸들링하는 WkFileStream을 반환한다. 모드는 읽기 위한 "r", 쓰기 위한 "w", 덧붙이기 위한
     * "a"만 가능하다.
     * 
     * @param mode 모드
     * @param encoding 인코딩
     * @return WkFileStream
     * @throws AxError WkFileStream을 가졍오는데 실패했을 경우 발생
     */
    public WkFileStream openStream(String mode, String encoding) throws AxError;
}
