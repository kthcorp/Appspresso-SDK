package com.appspresso.waikiki.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.appspresso.waikiki.filesystem.codec.Codec;
import com.appspresso.waikiki.filesystem.codec.EucKrCodec;
import com.appspresso.waikiki.filesystem.codec.Latin1Codec;
import com.appspresso.waikiki.filesystem.codec.UTF8Codec;
import com.appspresso.waikiki.filesystem.errors.IOError;
import com.appspresso.waikiki.filesystem.errors.InvalidValuesError;
import com.appspresso.waikiki.filesystem.filestreams.AppendFileStream;
import com.appspresso.waikiki.filesystem.filestreams.OverwriteFileStream;
import com.appspresso.waikiki.filesystem.filestreams.ReadFileStream;

public class FileStreamFactory {
    public static final String MODE_READ = "r";
    public static final String MODE_APPEND = "a";
    public static final String MODE_WRITE = "w";

    private Map<Long, WkFileStream> fileStreams;
    private static FileStreamFactory instance = null;

    private FileStreamFactory() {
        fileStreams = new HashMap<Long, WkFileStream>();
    }

    public static FileStreamFactory getInstance() {
        if (instance == null) instance = new FileStreamFactory();
        return instance;
    }

    public WkFileStream createFileStream(File file, String mode, String encoding) {
        Codec codec = getCodec(encoding.toUpperCase());
        if (codec == null) {
            // throw new InvalidValuesError("The encoding \"" + encoding +
            // "\" is invalid.");
            throw new InvalidValuesError("Unknown encoding");
        }

        try {
            WkFileStream fileStream = getFileStream(file, codec, mode);
            if (fileStream == null) {
                // throw new InvalidValuesError("The mode \"" + mode +
                // "\" is invalid.");
                throw new InvalidValuesError("Unknown mode");
            }
            fileStreams.put(fileStream.getHandle(), fileStream);
            return fileStream;
        }
        catch (FileNotFoundException e) {
            throw new IOError("The file does not exist.");
        }
        catch (IOException e) {
            throw new UnknownError("An unknown error has occurred.");
        }
    }

    public WkFileStream getFileStream(long handle) {
        if (!fileStreams.containsKey(handle)) { throw new UnknownError(
                "Filestream handle is not valid"); }
        return fileStreams.get(handle);
    }

    public WkReadFileStream getReadFileStream(long handle) {
        if (!fileStreams.containsKey(handle)) { throw new UnknownError(
                "Filestream handle is not valid"); }

        try {
            return (WkReadFileStream) fileStreams.get(handle);
        }
        catch (ClassCastException e) {
            return null;
        }
    }

    public WkWriteFileStream getWriteFileStream(long handle) {
        if (!fileStreams.containsKey(handle)) { throw new UnknownError(
                "Filestream handle is not valid"); }

        try {
            return (WkWriteFileStream) fileStreams.get(handle);
        }
        catch (ClassCastException e) {
            return null;
        }
    }

    public void close(long handle) {
        if (!fileStreams.containsKey(handle)) { throw new UnknownError(
                "Filestream handle is not valid"); }

        WkFileStream fileStream = fileStreams.remove(handle);
        fileStream.close();
    }

    public void close() {
        for (WkFileStream fileStream : fileStreams.values()) {
            fileStream.close();
        }

        fileStreams.clear();
    }

    private Codec getCodec(String encoding) {
        if (UTF8Codec.NAME.equals(encoding)) return new UTF8Codec();
        if (Latin1Codec.NAME.equals(encoding)) return new Latin1Codec();
        if (EucKrCodec.NAME.equals(encoding)) return new EucKrCodec();

        return null;
    }

    private WkFileStream getFileStream(File file, Codec codec, String mode)
            throws FileNotFoundException, IOException {
        if (MODE_READ.equals(mode)) return new ReadFileStream(file, codec);
        if (MODE_APPEND.equals(mode)) return new AppendFileStream(file, codec);
        if (MODE_WRITE.equals(mode)) return new OverwriteFileStream(file, codec);

        return null;
    }
}
