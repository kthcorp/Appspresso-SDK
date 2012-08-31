package com.appspresso.waikiki.filesystem;

import com.appspresso.api.AxError;

public interface WkReadFileStream extends WkFileStream {
    public String read(int charCount) throws AxError;

    public String readBase64(int byteCount) throws AxError;

    public byte[] readBytes(int byteCount) throws AxError;
}
