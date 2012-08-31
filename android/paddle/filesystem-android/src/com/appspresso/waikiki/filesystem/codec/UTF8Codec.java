package com.appspresso.waikiki.filesystem.codec;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;

public class UTF8Codec implements Codec {
    public static final Log LOG = AxLog.getLog(Codec.class);

    public static String NAME = "UTF-8";
    public static final byte[] READ_BYTE_ARRAY_UTF_8 = new byte[] {(byte) 0xFC, (byte) 0xF8,
            (byte) 0xF0, (byte) 0xE0, (byte) 0xC0, (byte) 0x0};
    public static final int BUFFER_SIZE = 1024 * 32;

    @Override
    public byte[] convertToByteArray(String data) {
        try {
            return data.getBytes(NAME);
        }
        catch (UnsupportedEncodingException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("", e); // TODO 지정하지 않은 인코딩
            }
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
            if (LOG.isErrorEnabled()) {
                LOG.error("", e); // TODO 파일포인터를 가져오는데 실패했음. 보통 rafile이 이용할 수
                                  // 없거나 close되어 있으면 ㅅ
            }
            return null;
        }

        StringBuffer result = new StringBuffer(charCount); // FIXME
        int skipBytesLength = 0;;
        int totalLength = 0;
        int length = 0;

        // if(0 == position) {
        // // Check the byte order mark
        // try {
        // if((byte)0xEF != randomAccess.readByte()) {
        // randomAccess.seek(0);
        // } else if((byte)0xBB != randomAccess.readByte()) {
        // randomAccess.seek(0);
        // } if((byte)0xBF != randomAccess.readByte()) {
        // randomAccess.seek(0);
        // }
        // totalLength += 3;
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }

        byte[] buffer = new byte[BUFFER_SIZE];
        while (0 < charCount) {
            try {
                length = getLength(randomAccess.readByte());
                charCount--;
                totalLength += length;
                skipBytesLength = length - 1;

                if (skipBytesLength != randomAccess.skipBytes(skipBytesLength)) { throw new EOFException(); }

                if (totalLength > BUFFER_SIZE - 6) {
                    append(randomAccess, result, buffer, position, totalLength);
                    position = randomAccess.getFilePointer();
                    totalLength = 0;
                }
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

    private int getLength(byte readByte) {
        for (int i = 0; i < 6; i++) {
            if ((readByte & READ_BYTE_ARRAY_UTF_8[i]) == READ_BYTE_ARRAY_UTF_8[i]) { return 6 - i; }
        }

        return 1;
    }
}
