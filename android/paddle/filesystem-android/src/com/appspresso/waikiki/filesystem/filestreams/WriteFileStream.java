package com.appspresso.waikiki.filesystem.filestreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import com.appspresso.api.AxError;
import com.appspresso.waikiki.filesystem.WkWriteFileStream;
import com.appspresso.waikiki.filesystem.codec.Codec;
import com.appspresso.waikiki.filesystem.errors.IOError;

public class WriteFileStream extends DefaultFileStream implements WkWriteFileStream {
    private static Class<?> CLASS_BASE64;
    private static Method DECODE_BASE64;

    static {
        try {
            CLASS_BASE64 = Class.forName("org.apache.commons.codec.binary.Base64");
            Class<?>[] parameterTypes = new Class[] {byte[].class};
            DECODE_BASE64 = CLASS_BASE64.getMethod("decodeBase64", parameterTypes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WriteFileStream(File file, Codec codec) throws FileNotFoundException, IOException {
        init(file, "rws", codec);
    }

    @Override
    public void write(String data) throws AxError {
        byte[] byteData = codec.convertToByteArray(data);
        writeBytes(byteData);
    }

    @Override
    public void writeBase64(String data) throws AxError {
        byte[] byteData = decodeBase64(data.getBytes());
        writeBytes(byteData);
    }

    @Override
    public void writeBytes(byte[] data) throws AxError {
        try {
            accessFile.write(data);
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) {
                L.error("", e);
            }
            throw new IOError("An unknown error has occurred.");
        }
    }

    public void writeBytes(int[] data) throws IOException {
        byte[] byteData = convertIntArrayToByteArray(data);
        writeBytes(byteData);
    }

    public static byte[] convertIntArrayToByteArray(int[] intArray) {
        int length = intArray.length;
        byte[] byteArray = new byte[length];

        for (int i = 0; i < length; i++) {
            byteArray[i] = (byte) intArray[i];
        }

        return byteArray;
    }

    public static byte[] decodeBase64(byte[] base64Data) {
        try {
            return (byte[]) DECODE_BASE64.invoke(CLASS_BASE64, base64Data);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
