package com.appspresso.waikiki.filesystem.filesystems;

import java.io.File;
import com.appspresso.waikiki.filesystem.WkFileSystem;

public class ExternalFileSystem extends DefaultFileSystem {
    public ExternalFileSystem(File rootDirectory) {
        super(rootDirectory);
    }

    @Override
    public boolean onSameMount(WkFileSystem fileSystem) {
        return fileSystem instanceof ExternalFileSystem;
    }
}
