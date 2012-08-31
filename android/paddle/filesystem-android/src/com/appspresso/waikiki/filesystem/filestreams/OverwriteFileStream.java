package com.appspresso.waikiki.filesystem.filestreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.appspresso.waikiki.filesystem.codec.Codec;

public class OverwriteFileStream extends WriteFileStream {
    public OverwriteFileStream(File file, Codec codec) throws FileNotFoundException, IOException {
        super(file, codec);
    }

    @Override
    protected void init(File file, String mode, Codec codec) throws FileNotFoundException,
            IOException {
        if (!file.delete()) throw new IOException();
        super.init(file, mode, codec);
    }
}
