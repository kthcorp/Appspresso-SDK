package com.appspresso.waikiki.filesystem.filestreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.waikiki.filesystem.WkFileStream;
import com.appspresso.waikiki.filesystem.codec.Codec;
import com.appspresso.waikiki.filesystem.errors.IOError;
import com.appspresso.waikiki.filesystem.errors.InvalidValuesError;

public class DefaultFileStream implements WkFileStream {
    public static final Log L = AxLog.getLog(WkFileStream.class);
    public static final int BUFFER_SIZE = 1024 * 32;
    public static final String KEY_HANDLE = "_handle";
    private static final AtomicLong handleCreator = new AtomicLong(1073);

    private final long handle;
    protected RandomAccessFile accessFile;
    protected Codec codec;

    public DefaultFileStream() {
        this.handle = handleCreator.getAndIncrement();
    }

    protected void init(File file, String mode, Codec codec) throws FileNotFoundException,
            IOException {
        this.accessFile = new RandomAccessFile(file, mode);
        this.codec = codec;
    }

    @Override
    public void close() {
        try {
            accessFile.close();
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) {
                L.error("", e);
            }

            throw new IOError("An unknown error has occurred.");
        }
    }

    @Override
    public long getBytesAvailable() throws AxError {
        try {
            long bytesAvailable = accessFile.length() - accessFile.getFilePointer();
            return (bytesAvailable > 0) ? bytesAvailable : -1L;
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) {
                L.error("", e);
            }

            throw new IOError(
                    "An unknown error has occurred while getting the number of available bytes.");
        }
    }

    @Override
    public long getPosition() throws AxError {
        try {
            return accessFile.getFilePointer();
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) {
                L.error("", e);
            }
            throw new IOError("An unknown error has occurred.");
        }
    }

    @Override
    public void setPosition(long position) throws AxError {
        // if (position < 0) {
        // throw new InvalidValuesError("Negative position not allowed.");
        // }
        //
        // try {
        // long length = accessFile.length();
        // accessFile.seek((position > length) ? length : position);
        // }
        // catch (IOException e) {
        // if(L.isErrorEnabled()) {
        // L.error("", e);
        // }
        // throw new IOError("An unknown error has occurred.");
        // }

        if (position < 0) { throw new InvalidValuesError("Negative position not allowed."); }

        try {
            if (position > accessFile.length()) { throw new IOError(
                    "A position was given that is out of the stream range."); }

            accessFile.seek(position);
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) {
                L.error("", e);
            }
            throw new IOError("An unknown error has occurred.");
        }
    }

    @Override
    public boolean isEof() throws AxError {
        try {
            return accessFile.length() <= accessFile.getFilePointer();
        }
        catch (IOException e) {
            if (L.isErrorEnabled()) {
                L.error("", e);
            }
            throw new IOError("An unknown error has occurred.");
        }
    }

    @Override
    public Object getPluginResult() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put(KEY_HANDLE, handle);
        return result;
    }

    @Override
    public long getHandle() {
        return handle;
    }
}
