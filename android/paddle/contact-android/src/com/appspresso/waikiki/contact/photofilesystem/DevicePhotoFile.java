package com.appspresso.waikiki.contact.photofilesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileFilter;
import com.appspresso.api.fs.FileSystemUtils;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

public class DevicePhotoFile implements AxFile {
    public static final Log L = AxLog.getLog(DevicePhotoFile.class);

    private Context context;
    private AxFile parent;
    private long contactId;
    private boolean canRead;
    private boolean canWrite;
    private long length;
    private PhotoFileStream fileStream;

    public DevicePhotoFile(Context context, AxFile parent, long contactId) {
        this.context = context;
        this.parent = parent;
        this.contactId = contactId;

        init();
    }

    private void init() {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);

        InputStream inputStream =
                ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
        if (inputStream == null) { return; }

        try {
            length = inputStream.available();
            canRead = true;
        }
        catch (IOException e) {
            return;
        }
        finally {
            FileSystemUtils.closeQuietly(inputStream);
        }
        // TODO 현재 쓰기 적용 안됨
    }

    @Override
    public boolean exists() {
        return canRead;
    }

    @Override
    public boolean canRead() {
        return canRead;
    }

    @Override
    public boolean canWrite() {
        return canWrite;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public long getCreated() {
        // TODO 사진의 최초 수정 날짜를  적용한다.
        return 0;
    }

    @Override
    public long getModified() {
        // TODO 사진의 최종 수정 날짜를 적용한다.
        return 0;
    }

    @Override
    public String getName() {
        return Long.toString(contactId);
    }

    @Override
    public AxFile getParent() {
        return parent;
    }

    @Override
    public String getPath() {
        return parent.getPath() + File.separator + contactId;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public AxFile[] listFiles(AxFileFilter filter) {
        return null;
    }

    @Override
    public Object getPeer() {
        return PhotoFileUtils.getInputStream(context.getContentResolver(), contactId);
    }

    @Override
    public void open() throws IOException {
        open(MODE_READ);
    }

    @Override
    public void open(int mode) throws IOException {
        if (!canRead) throw new IOException("The stream cannot be opened.");
        if (fileStream != null) throw new IOException("The stream already was opened.");

        switch (mode) {
            case MODE_READ:
                fileStream = new DevicePhotoReadStream(context.getContentResolver(), contactId);
                break;
            case MODE_WRITE:
                if (!canWrite) throw new IOException("The stream cannot be opened.");
                fileStream = new DevicePhotoWriteStream(context.getContentResolver(), contactId);
                break;
            default:
                throw new IOException("It's an unsupported mode.");
        }

        fileStream.open();
    }

    @Override
    public void close() throws IOException {
        if (fileStream == null) throw new IOException("The stream was not opened.");

        try {
            fileStream.close();
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) {
                L.error("An unknown error has occurred.", e);
            }
        }
        finally {
            fileStream = null;
        }
    }

    @Override
    public boolean isEof() throws IOException {
        try {
            return fileStream.isEof();
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }

    @Override
    public void seek(long position) throws IOException {
        try {
            fileStream.seek(position);
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }

    @Override
    public long getPosition() throws IOException {
        try {
            return fileStream.getPosition();
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }

    @Override
    public byte[] read(int size) throws IOException {
        try {
            return fileStream.read(size);
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }

    @Override
    public void write(byte[] data) throws IOException {
        try {
            fileStream.write(data);
        }
        catch (NullPointerException e) {
            throw new IOException("The stream was not opened.");
        }
    }
}
