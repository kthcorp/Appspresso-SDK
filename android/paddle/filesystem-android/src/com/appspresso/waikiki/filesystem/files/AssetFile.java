package com.appspresso.waikiki.filesystem.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import com.appspresso.api.AxError;
import com.appspresso.api.fs.AssetFilePeer;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.FileSystemUtils;
import com.appspresso.waikiki.filesystem.FileStreamFactory;
import com.appspresso.waikiki.filesystem.WkFile;
import com.appspresso.waikiki.filesystem.WkFileStream;
import com.appspresso.waikiki.filesystem.errors.IOError;
import com.appspresso.waikiki.filesystem.errors.InvalidValuesError;
import com.appspresso.waikiki.filesystem.errors.NotFoundError;
import com.appspresso.waikiki.filesystem.utils.FileOperator;

public class AssetFile implements WkFile {
    private AxFile axFile;
    private File tempDir;

    public AssetFile(AxFile axFile, String mode, File tempDir) throws IOError {
        if (!axFile.isFile()) { throw new IOError(
                "The operation must be launched on a directory (not a file)."); }

        this.axFile = axFile;
        this.tempDir = tempDir;
    }

    @Override
    public String readAsText(String encoding) throws AxError {
        AssetFilePeer peer = (AssetFilePeer) axFile.getPeer();
        try {
            InputStream inputStream = peer.createInputStream();
            return FileSystemUtils.readAsText(inputStream, encoding);
        }
        catch (FileNotFoundException e) {
            throw new NotFoundError("The file does not exist.");
        }
        catch (UnsupportedEncodingException e) {
            throw new InvalidValuesError("Unknown encoding");
        }
        catch (IOException e) {
            throw new UnknownError("An unknown error occurs.");
        }
    }

    @Override
    public WkFileStream openStream(String mode, String encoding) throws AxError {
        InputStream inputStream = null;
        try {
            // 파일 포인터의 이동이 잦기 때문에 InputStream을 임시파일에다 복사한 후 사용한다.
            File tempPeer = new File(tempDir, "axFile" + System.currentTimeMillis() % 100000);

            AssetFilePeer peer = (AssetFilePeer) axFile.getPeer();
            inputStream = peer.createInputStream();

            if (!FileOperator.copyFile(inputStream, tempPeer, false)) { throw new UnknownError(
                    "An unknown error occurs."); }

            return FileStreamFactory.getInstance().createFileStream(tempPeer, mode, encoding);
        }
        catch (IOException e) {
            throw new UnknownError("An unknown error occurs.");
        }
        finally {
            FileSystemUtils.closeQuietly(inputStream);
        }
    }
}
