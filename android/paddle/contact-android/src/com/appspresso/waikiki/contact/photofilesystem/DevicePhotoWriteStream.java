package com.appspresso.waikiki.contact.photofilesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import android.content.ContentResolver;

public class DevicePhotoWriteStream implements PhotoFileStream {
    private static final int BUFFER_CAPASITY = 1024 * 1024; // XXX 적당한 버퍼 크기를
                                                            // 지정해 준다.
    private final ContentResolver contentResolver;
    private final long contactId;
    private ByteBuffer buffer;
    private int lastPosition;

    DevicePhotoWriteStream(ContentResolver contentResolver, long contactId) {
        this.contentResolver = contentResolver;
        this.contactId = contactId;
    }

    @Override
    public void open() throws IOException {
        buffer = ByteBuffer.allocate(BUFFER_CAPASITY);
    }

    @Override
    public void close() throws IOException {
        boolean result = PhotoFileUtils.updatePhotoFile(contentResolver, contactId, buffer.array());

        buffer.clear();
        buffer = null;

        if (!result) { throw new IOException( /* TODO */); }
    }

    @Override
    public boolean isEof() throws IOException {
        return buffer.position() == lastPosition;
    }

    @Override
    public void seek(long position) throws IOException {
        buffer.position((int) position);
    }

    @Override
    public long getPosition() throws IOException {
        return buffer.position();
    }

    @Override
    public byte[] read(int size) throws IOException {
        throw new IOException();
    }

    @Override
    public void write(byte[] data) throws IOException {
        buffer.put(data);
        if (buffer.position() > lastPosition) {
            lastPosition = buffer.position();
        }
    }
}
