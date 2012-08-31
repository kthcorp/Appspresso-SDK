package com.appspresso.waikiki.filesystem.filesystems;

import com.appspresso.api.AxError;
import com.appspresso.waikiki.filesystem.WkFileSystem;
import com.appspresso.waikiki.filesystem.errors.IOError;
import android.content.res.AssetManager;

public class AssetFileSystem extends com.appspresso.api.fs.AssetFileSystem implements WkFileSystem {
    public AssetFileSystem(AssetManager assetManager, String rootAssetPath) {
        super(assetManager, rootAssetPath);
    }

    @Override
    public void copyToHere(WkFileSystem otherSystem, String originPath, String destPath,
            boolean overwrite) throws AxError {
        throw new IOError("Permission denied");
    }

    @Override
    public void moveToHere(WkFileSystem otherSystem, String originPath, String destPath,
            boolean overwrite) throws AxError {
        throw new IOError("Permission denied");
    }

    @Override
    public boolean onSameMount(WkFileSystem fileSystem) {
        return false;
    }
}
