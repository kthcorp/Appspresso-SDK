package com.appspresso.waikiki.filesystem.codec;

import java.io.RandomAccessFile;

public interface Codec {
    public byte[] convertToByteArray(String data);

    public String getString(RandomAccessFile randomAccess, int charCount);
}
