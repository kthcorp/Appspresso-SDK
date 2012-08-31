package com.appspresso.waikiki.filesystem.filesystems;

import java.io.File;
import com.appspresso.api.fs.AxFile;

public class TemporaryFileSystem extends InternalFileSystem {
    public TemporaryFileSystem(File rootDirectory) {
        super(rootDirectory);
    }

    @Override
    public void onUnmount() {
        super.onUnmount();
        clearTemporary();
    }

    private void clearTemporary() {
        AxFile root = getRoot();
        if (root == null) return;

        File temporary = (File) root.getPeer();
        delete(temporary);
    }

    private void delete(File parent) {
        File[] children = parent.listFiles();
        File child;
        int length = children.length;
        for (int i = 0; i < length; i++) {
            child = children[i];
            if (child.isDirectory()) delete(child);
            child.delete();
        }
    }
}
