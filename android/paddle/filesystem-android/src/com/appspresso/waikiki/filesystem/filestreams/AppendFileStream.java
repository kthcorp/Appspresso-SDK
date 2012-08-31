package com.appspresso.waikiki.filesystem.filestreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.appspresso.api.AxError;
import com.appspresso.waikiki.filesystem.codec.Codec;

public class AppendFileStream extends WriteFileStream {
    public AppendFileStream(File file, Codec codec) throws FileNotFoundException, IOException {
        super(file, codec);
    }

    @Override
    protected void init(File file, String mode, Codec codec) throws FileNotFoundException,
            IOException {
        super.init(file, mode, codec);
        accessFile.seek(accessFile.length());
    }

    @Override
    public void write(String data) throws AxError {
        try {
            accessFile.seek(accessFile.length());
        }
        catch (IOException e) {
            throw new AxError(AxError.IO_ERR, "File write failed");
        }
        super.write(data);
    }

    @Override
    public void setPosition(long position) throws AxError {
        // Nothing to do.
    }
}
