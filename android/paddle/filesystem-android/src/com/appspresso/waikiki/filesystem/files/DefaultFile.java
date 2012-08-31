package com.appspresso.waikiki.filesystem.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.appspresso.api.AxError;
import com.appspresso.api.fs.AxFile;
import com.appspresso.waikiki.filesystem.FileStreamFactory;
import com.appspresso.waikiki.filesystem.WkFile;
import com.appspresso.waikiki.filesystem.WkFileStream;
import com.appspresso.waikiki.filesystem.errors.IOError;
import com.appspresso.waikiki.filesystem.errors.InvalidValuesError;
import com.appspresso.waikiki.filesystem.errors.NotFoundError;
import com.appspresso.waikiki.filesystem.utils.FileOperator;

public class DefaultFile implements WkFile {
    private AxFile axFile;
    private String mode;

    public DefaultFile(AxFile axFile, String mode) throws IOError {
        if (!axFile.isFile()) { throw new IOError(
                "The operation must be launched on a directory (not a file)."); }

        this.axFile = axFile;
        this.mode = mode;
    }

    @Override
    public String readAsText(String encoding) throws AxError {
        File peer = (File) axFile.getPeer();

        try {
            FileInputStream inputStream = new FileInputStream(peer);
            return FileOperator.readAsText(inputStream, encoding);
        }
        catch (FileNotFoundException e) {
            throw new NotFoundError("The file does not exist.");
        }
        catch (UnsupportedEncodingException e) {
            throw new InvalidValuesError("Unknown encoding");
        }
        catch (OutOfMemoryError e) {
            throw new UnknownError("Memory leak occurred while reading this file.");
        }
        catch (IOException e) {
            throw new UnknownError("An unknown error has occurred.");
        }
    }

    @Override
    public WkFileStream openStream(String mode, String encoding) throws AxError {
        if ("r".equals(this.mode)) {
            if (!"r".equals(mode)) { throw new IOError("Unknown mode"); }
        }
        else if (!"r".equals(mode) && !"a".equals(mode) && !"w".equals(mode)) { throw new IOError(
                "Unknown mode"); }

        File peer = (File) axFile.getPeer();
        return FileStreamFactory.getInstance().createFileStream(peer, mode, encoding);
    }
}
