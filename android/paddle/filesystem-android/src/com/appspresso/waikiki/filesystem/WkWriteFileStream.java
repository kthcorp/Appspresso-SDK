package com.appspresso.waikiki.filesystem;

import com.appspresso.api.AxError;

public interface WkWriteFileStream extends WkFileStream {
    public void write(String data) throws AxError;

    public void writeBase64(String data) throws AxError;

    public void writeBytes(byte[] data) throws AxError;
}
