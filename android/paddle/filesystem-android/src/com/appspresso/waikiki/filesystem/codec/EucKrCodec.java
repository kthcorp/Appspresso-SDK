package com.appspresso.waikiki.filesystem.codec;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class EucKrCodec implements Codec {
    public static String NAME = "EUC-KR";
    public static final int BUFFER_SIZE = 1024 * 32;

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
        long position = 0;

        try {
            position = randomAccess.getFilePointer();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        StringBuffer result = new StringBuffer(charCount); // FIXME
        int totalLength = 0;

        byte firstByte = 0;
        byte secondByte;
        boolean is2Bytes = true;

        while (0 < charCount) {
            if (is2Bytes) {
                try {
                    firstByte = randomAccess.readByte();
                }
                catch (EOFException e) {
                    break;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            try {
                secondByte = randomAccess.readByte();
            }
            catch (EOFException e) {
                totalLength++;
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            is2Bytes = is2BytesChar(firstByte, secondByte);
            if (is2Bytes) {
                totalLength += 2;
            }
            else {
                totalLength++;
                firstByte = secondByte;
            }

            charCount--;
            if (totalLength > BUFFER_SIZE - 2) {
                try {
                    append(randomAccess, result, buffer, position, totalLength);
                    position = randomAccess.getFilePointer();
                    totalLength = 0;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        try {
            append(randomAccess, result, buffer, position, totalLength);
            return result.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void append(RandomAccessFile randomAccess, StringBuffer stringBuffer, byte[] buffer,
            long position, int count) throws IOException {
        randomAccess.seek(position);

        int readCount = randomAccess.read(buffer, 0, count);
        if (buffer.length != readCount) {
            byte[] newBuffer = new byte[readCount];
            System.arraycopy(buffer, 0, newBuffer, 0, readCount);
            buffer = newBuffer;
        }

        stringBuffer.append(new String(buffer, NAME));
    }

    private boolean is2BytesChar(byte firstByte, byte secondByte) {
        int firstInt, secondInt;
        firstInt = (int) firstByte & 0xFF;
        secondInt = (int) secondByte & 0xFF;

        if (firstInt < 0x81) return false;
        return (firstInt >= 0xA0) && (firstInt <= 0xFF) && (secondInt >= 0xA0)
                && (secondInt <= 0xFF);
    }
}
