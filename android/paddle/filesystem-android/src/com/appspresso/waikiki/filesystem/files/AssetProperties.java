package com.appspresso.waikiki.filesystem.files;

import com.appspresso.api.fs.AxFile;

public class AssetProperties extends DefaultProperties {
    private AxFile axFile;

    public AssetProperties(AxFile axFile) {
        super(axFile, "r");
        this.axFile = axFile;
    }

    @Override
    public long getFileSize() {
        if (axFile.isDirectory()) return -1;
        return axFile.getLength();
    }

    @Override
    public long getLength() {
        if (axFile.isFile()) return -1;
        return axFile.listFiles(null).length;
    }
}
