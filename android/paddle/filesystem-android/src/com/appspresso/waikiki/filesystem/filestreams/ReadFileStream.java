package com.appspresso.waikiki.filesystem.filestreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import com.appspresso.api.AxError;
import com.appspresso.waikiki.filesystem.WkReadFileStream;
import com.appspresso.waikiki.filesystem.codec.Codec;
import com.appspresso.waikiki.filesystem.errors.IOError;

public class ReadFileStream extends DefaultFileStream implements WkReadFileStream {
    private static Class<?> CLASS_BASE64;
    private static Method ENCODE_BASE64;

    static {
        try {
            CLASS_BASE64 = Class.forName("org.apache.commons.codec.binary.Base64");
            ENCODE_BASE64 = CLASS_BASE64.getMethod("encodeBase64", new Class[] {byte[].class});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ReadFileStream(File file, Codec codec) throws FileNotFoundException, IOException {
        init(file, "r", codec);
    }

    @Override
    public String read(int charCount) throws AxError {
        String result = codec.getString(accessFile, charCount);
        if (result == null) { throw new AxError(AxError.UNKNOWN_ERR,
                "An unknown error has occurred."); }
        return result;
    }

    @Override
    public String readBase64(int byteCount) throws AxError {
        byte[] buffer = new byte[BUFFER_SIZE];
        byte[] temp = null;
        StringBuffer stringBuffer = new StringBuffer();
        int readCount = 0;

        while (0 < byteCount) {
            try {
                readCount = accessFile.read(buffer, 0, Math.min(byteCount, BUFFER_SIZE));
            }
            catch (IOException e) {
                if (L.isErrorEnabled()) {
                    L.error("", e);
                }
                throw new IOError("An unknown error has occurred.");
            }

            if (0 > readCount) break;

            temp = new byte[readCount];
            System.arraycopy(buffer, 0, temp, 0, readCount);
            stringBuffer.append(new String(encodeBase64(temp)));
            byteCount -= readCount;
        }

        return stringBuffer.toString();
    }

    @Override
    public byte[] readBytes(int byteCount) throws AxError {
        byte[] buffer = new byte[BUFFER_SIZE];
        ByteBuffer byteBuffer = ByteBuffer.allocate(byteCount);
        int readCount = 0;

        while (0 < byteCount) {
            try {
                readCount = accessFile.read(buffer, 0, Math.min(byteCount, BUFFER_SIZE));
            }
            catch (IOException e) {
                if (L.isErrorEnabled()) {
                    L.error("", e);
                }
                throw new IOError("An unknown error has occurred.");
            }

            if (0 > readCount) break;

            byteBuffer.put(buffer, 0, readCount);
            byteCount -= readCount;
        }

        if (byteBuffer.capacity() == byteBuffer.position()) {
            return byteBuffer.array();
        }
        else {
            int length = byteBuffer.position();
            buffer = new byte[length];
            System.arraycopy(byteBuffer.array(), 0, buffer, 0, length);
            return buffer;
        }
    }

    private static byte[] encodeBase64(byte[] binaryData) {
        try {
            Object object = ENCODE_BASE64.invoke(CLASS_BASE64, binaryData);
            return (byte[]) object;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
