package com.appspresso.core.runtime.filesystem;

import java.io.File;
import com.appspresso.api.fs.AssetFileSystem;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileSystemManager;
import com.appspresso.api.fs.DefaultFileSystem;
import android.content.Context;
import android.os.Environment;

public class WaikikiFileSystems {
    public static final String ROOT_IMAGES = "images";
    public static final String ROOT_VIDEOS = "videos";
    public static final String ROOT_MUSIC = "music";
    public static final String ROOT_DOCUMENTS = "documents";
    public static final String ROOT_DOWNLOADS = "downloads";
    public static final String ROOT_WGT_PRIVATE = "wgt-private";
    public static final String ROOT_WGT_PACKAGE = "wgt-package";
    public static final String ROOT_WGT_PRIVATE_TMP = "wgt-private-tmp";
    public static final String ROOT_REMOVABLE = "removable";

    public static final String WGT_PACKAGE_ROOT_PATH = "ax_www";
    public static final String TEMP_PATH = "AxTemp";
    public static final String WGT_PRIVATE_TMP_ROOT_PATH = "temp";

    private AxFileSystemManager manager;
    private Context context;

    public WaikikiFileSystems(AxFileSystemManager manager, Context context) {
        this.manager = manager;
        this.context = context;
    }

    public void mountFileSystems() {
        // set Temoporary file system
        File cache =
                new File(context.getCacheDir(), TEMP_PATH + File.separator
                        + WGT_PRIVATE_TMP_ROOT_PATH);
        cache.mkdirs();
        manager.mount(ROOT_WGT_PRIVATE_TMP, new TemporaryFileSystem(cache), null);

        // set Default file systems.
        String[] prefixs =
                new String[] {ROOT_IMAGES, ROOT_VIDEOS, ROOT_MUSIC, ROOT_DOCUMENTS, ROOT_DOWNLOADS,
                        ROOT_WGT_PRIVATE,
                        // ROOT_WGT_PRIVATE_TMP,
                        ROOT_REMOVABLE};
        File[] directories =
                new File[] {new File(context.getFilesDir().getAbsolutePath(), ROOT_IMAGES),
                        new File(context.getFilesDir().getAbsolutePath(), ROOT_VIDEOS),
                        new File(context.getFilesDir().getAbsolutePath(), ROOT_MUSIC),
                        new File(context.getFilesDir().getAbsolutePath(), ROOT_DOCUMENTS),
                        new File(context.getFilesDir().getAbsolutePath(), ROOT_DOWNLOADS),
                        new File(context.getFilesDir().getAbsolutePath(), ROOT_WGT_PRIVATE),
                        // new File(context.getCacheDir(), TEMP_PATH + File.separator +
                        // WGT_PRIVATE_TMP_ROOT_PATH),
                        Environment.getExternalStorageDirectory()};

        mountDefaultFileSystems(prefixs, directories);

        // set Asset file systems.
        prefixs = new String[] {ROOT_WGT_PACKAGE};
        String[] rootPaths = new String[] {WGT_PACKAGE_ROOT_PATH};
        mountAssetFileSystems(prefixs, rootPaths);
    }

    public void unmountFileSystems() {
        String[] prefixs =
                new String[] {ROOT_IMAGES, ROOT_VIDEOS, ROOT_MUSIC, ROOT_DOCUMENTS, ROOT_DOWNLOADS,
                        ROOT_WGT_PRIVATE, ROOT_WGT_PRIVATE_TMP, ROOT_REMOVABLE, ROOT_WGT_PACKAGE};

        for (String prefix : prefixs) {
            manager.unmount(prefix);
        }
    }

    private void mountDefaultFileSystems(String[] prefixs, File[] directories) {
        int length = prefixs.length;
        for (int i = 0; i < length; i++) {
            directories[i].mkdirs();
            manager.mount(prefixs[i], new DefaultFileSystem(directories[i]), null);
        }
    }

    private void mountAssetFileSystems(String[] prefixs, String[] rootPaths) {
        AssetFileSystem fileSystem = null;

        int length = prefixs.length;
        for (int i = 0; i < length; i++) {
            fileSystem = new AssetFileSystem(context.getAssets(), rootPaths[i]);
            manager.mount(prefixs[i], fileSystem, null);
        }
    }

    public class TemporaryFileSystem extends DefaultFileSystem {

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
}
