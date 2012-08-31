package com.appspresso.waikiki.filesystem.codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class Latin1Codec implements Codec {
    public static String NAME = "ISO-8859-1";

    @Override
    public byte[] convertToByteArray(String data) {
        try {
            return data.getBytes(NAME);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getString(RandomAccessFile randomAccess, int charCount) {
        byte[] buffer = new byte[charCount];

        try {
            int readCount = randomAccess.read(buffer, 0, charCount);
            if (readCount != charCount) {
                byte[] newBuffer = new byte[readCount];
                System.arraycopy(buffer, 0, newBuffer, 0, readCount);
                buffer = newBuffer;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            return new String(buffer, NAME);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
