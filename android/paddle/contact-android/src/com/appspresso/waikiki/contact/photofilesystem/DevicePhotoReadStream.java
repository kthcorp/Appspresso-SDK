package com.appspresso.waikiki.contact.photofilesystem;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;
import android.content.ContentResolver;

public class DevicePhotoReadStream implements PhotoFileStream {
    public static final Log L = AxLog.getLog(DevicePhotoFile.class);

    private final ContentResolver contentResolver;
    private final long contactId;
    private int length;
    private InputStream inputStream;

    public DevicePhotoReadStream(ContentResolver contentResolver, long contactId) {
        this.contentResolver = contentResolver;
        this.contactId = contactId;
    }

    @Override
    public void open() throws IOException {
        inputStream = PhotoFileUtils.getInputStream(contentResolver, contactId);
        length = inputStream.available();
        inputStream.mark(0);
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public boolean isEof() throws IOException {
        return inputStream.available() < 1;
    }

    @Override
    public void seek(long position) throws IOException {
        inputStream.reset();
        inputStream.skip(position);
    }

    @Override
    public long getPosition() throws IOException {
        return length - inputStream.available();
    }

    @Override
    public byte[] read(int size) throws IOException {
        byte[] buffer = new byte[size];

        int read = inputStream.read(buffer);
        if (read < size) {
            byte[] newBuffer = new byte[read];
            System.arraycopy(buffer, 0, newBuffer, 0, read);
            buffer = newBuffer;
        }

        return buffer;
    }

    @Override
    public void write(byte[] data) throws IOException {
        throw new IOException();
    }
}
