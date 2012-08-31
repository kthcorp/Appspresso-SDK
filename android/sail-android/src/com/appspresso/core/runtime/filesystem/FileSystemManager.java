package com.appspresso.core.runtime.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.appspresso.api.AxLog;
import com.appspresso.api.fs.AssetFileSystem;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileSystem;
import com.appspresso.api.fs.AxFileSystemManager;
import com.appspresso.api.fs.FileSystemUtils;

public class FileSystemManager implements AxFileSystemManager {
    private static Log LOG = AxLog.getLog(AxFileSystemManager.class);

    private final Map<String, AxFileSystem> fileSystemMap;
    private final List<String> pluginList;
    private AxUriCodec uriCodec;

    public FileSystemManager() {
        fileSystemMap = new HashMap<String, AxFileSystem>();
        uriCodec = new DefaultUriCodec();
        pluginList = new ArrayList<String>();
    }

    // ---------------------------------------------------------------------------------------------
    // Implement AxFileSystemManager

    @Override
    public void mount(String prefix, AxFileSystem fileSystem, Properties options) {
        if (fileSystemMap.containsKey(prefix)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("This file system was already mounted");
            }
            return;
        }

        fileSystem.onMount(prefix, options);
        fileSystemMap.put(prefix, fileSystem);
    }

    @Override
    public void unmount(String prefix) {
        if (!fileSystemMap.containsKey(prefix)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("This file system was not mounted.");
            }
            return;
        }

        if (pluginList.contains(prefix)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("This file system cannot be mounted.");
            }
            return;
        }

        fileSystemMap.remove(prefix).onUnmount();
    }

    @Override
    public AxFileSystem getFileSystem(String prefix) {
        return fileSystemMap.get(prefix);
    }

    @Override
    public AxFile getFile(String path) {
        path = FileSystemUtils.removeExtraFileSeparator(path);

        String prefix;
        for (Map.Entry<String, AxFileSystem> entry : fileSystemMap.entrySet()) {
            prefix = entry.getKey();
            if (FileSystemUtils.hasPrefix(path, prefix)) {
                path = FileSystemUtils.extractRelativePath(path, prefix);
                return entry.getValue().getFile(path);
            }
        }

        return null;
    }

    @Override
    public String fromUri(String uri) {
        return uriCodec.decode(uri);
    }

    @Override
    public String toUri(String path) {
        return uriCodec.encode(path);
    }

    @Override
    public String toVirtualPath(String path) {
        // for (Map.Entry<String,AxFileSystem> entry : fileSystemMap.entrySet())
        // {
        // // toVirtualPath는 선택적 구현
        // try {
        // String ret = entry.getValue().toVirtualPath(path);
        // if (ret != null) {
        // return entry.getKey() + "/" + ret;
        // }
        // } catch (UnsupportedOperationException ignored) {
        // }
        // }
        // return null;

        if (TextUtils.isEmpty(path)) return null;

        // 앞뒤의 "/"만 제외해준다.
        try {
            if (path.charAt(0) == File.separatorChar) path = path.substring(1);
            if (path.charAt(path.length() - 1) == File.separatorChar)
                path = path.substring(0, path.length() - 1);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }

        String nativePath = null;
        for (Map.Entry<String, AxFileSystem> entry : fileSystemMap.entrySet()) {
            try {
                nativePath = entry.getValue().toVirtualPath(path);
                if (nativePath != null) return nativePath;
            }
            catch (UnsupportedOperationException ignored) {}
        }

        return null;
    }

    @Override
    public String toNativePath(String path) {
        // Virtual full path > Native full path

        // int pos = path.indexOf("/");
        // String prefix = path.substring(0, pos);
        // AxFileSystem fileSystem = fileSystemMap.get(prefix);
        // // toNativePath는 선택적 구현
        // if (fileSystem != null) {
        // try {
        // return fileSystem.toNativePath(path.substring(pos + 1));
        // } catch (UnsupportedOperationException ignored) {
        // }
        // }
        // return null;

        if (TextUtils.isEmpty(path)) return null;

        // 앞뒤의 "/"만 제외해준다.
        try {
            if (path.charAt(0) == File.separatorChar) path = path.substring(1);
            if (path.charAt(path.length() - 1) == File.separatorChar)
                path = path.substring(0, path.length() - 1);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }

        AxFileSystem fileSystem = null;

        for (String prefix : fileSystemMap.keySet()) {
            if (FileSystemUtils.hasPrefix(path, prefix)) {
                fileSystem = fileSystemMap.get(prefix);
                String relativePath = "";

                try {
                    relativePath = path.substring(prefix.length() + 1);
                }
                catch (IndexOutOfBoundsException ignored) {}

                try {
                    return fileSystem.toNativePath(relativePath);
                }
                catch (UnsupportedOperationException ignored) {}
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public synchronized void mountPlugin(AssetManager assetManager, String pluginId) {
        AssetFileSystem axFileSystem =
                new AssetFileSystem(assetManager, "ax_res" + File.separator + pluginId);
        axFileSystem.onMount(pluginId, null);
        fileSystemMap.put(pluginId, axFileSystem);
        pluginList.add(pluginId);
    }

    public synchronized void unmountPlugin(String pluginId) {
        if (!pluginList.contains(pluginId)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("This file system was not mounted.");
            }
            return;
        }

        AxFileSystem axFileSystem = fileSystemMap.get(pluginId);
        axFileSystem.onUnmount();

        fileSystemMap.remove(pluginId);
        pluginList.remove(pluginList);
    }
}
