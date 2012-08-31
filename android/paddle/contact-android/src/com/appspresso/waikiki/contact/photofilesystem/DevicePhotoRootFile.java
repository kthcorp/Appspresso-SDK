package com.appspresso.waikiki.contact.photofilesystem;

import java.io.IOException;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileFilter;

public class DevicePhotoRootFile implements AxFile {
    private String prefix;

    public DevicePhotoRootFile(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public long getCreated() {
        return 0;
    }

    @Override
    public long getModified() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public String getName() {
        return prefix;
    }

    @Override
    public AxFile getParent() {
        return null;
    }

    @Override
    public String getPath() {
        return prefix;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public AxFile[] listFiles(AxFileFilter filter) {
        // XXX 이 기능을 구현하기 위해서는 모든 주소록을 돌며 사진을 설정해 놓은 데이터의 ID만 가져와야 하므로
        // 이후 Iterator를 위한 API를 추가할 경우 구현하도록 한다.
        return new AxFile[0];
    }

    @Override
    public Object getPeer() {
        return null;
    }

    @Override
    public void open() throws IOException {}

    @Override
    public void open(int mode) throws IOException {
        throw new IOException();
    }

    @Override
    public void close() throws IOException {}

    @Override
    public boolean isEof() throws IOException {
        return false;
    }

    @Override
    public void seek(long position) throws IOException {}

    @Override
    public long getPosition() throws IOException {
        return 0;
    }

    @Override
    public byte[] read(int size) throws IOException {
        return null;
    }

    @Override
    public void write(byte[] data) throws IOException {}
}
