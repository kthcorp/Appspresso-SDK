package com.appspresso.waikiki.filesystem.files;

import java.io.File;
import com.appspresso.api.fs.AxFile;
import com.appspresso.waikiki.filesystem.WkFileHandle;
import com.appspresso.waikiki.filesystem.WkProperties;

public class DefaultProperties implements WkProperties {
    private AxFile axFile;
    private String mode;

    public DefaultProperties(AxFile axFile, String mode) {
        this.axFile = axFile;
        this.mode = mode;
    }

    @Override
    public WkFileHandle getParent() {
        AxFile parent = axFile.getParent();
        return new WkFileHandle(parent, mode);
    }

    @Override
    public boolean readOnly() {
        return !axFile.canWrite();
    }

    @Override
    public boolean isFile() {
        return axFile.isFile();
    }

    @Override
    public boolean isDirectory() {
        return axFile.isDirectory();
    }

    @Override
    public long getCreated() {
        return -1;
    }

    @Override
    public long getModified() {
        return axFile.getModified();
    }

    @Override
    public long getFileSize() {
        if (!axFile.isFile()) return -1;

        File peer = (File) axFile.getPeer();
        return peer.length();
    }

    @Override
    public long getLength() {
        if (!axFile.isDirectory()) return -1;

        File peer = (File) axFile.getPeer();
        return peer.list().length;
    }
}
