package com.appspresso.waikiki.contact.photofilesystem;

import java.util.Properties;
import android.content.Context;
import com.appspresso.api.fs.AxFile;

public class DevicePhotoFileSystem implements PhotoFileSystem {
    public static final String PROPERTY_CONTEXT = "context";
    private Context context;
    private AxFile rootAxFile;

    @Override
    public void onMount(String prefix, Properties options) {
        context = (Context) options.get(PROPERTY_CONTEXT);
        rootAxFile = new DevicePhotoRootFile(prefix);
    }

    @Override
    public void onUnmount() {
        // Nothing to do.
    }

    @Override
    public AxFile getRoot() {
        return rootAxFile;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public AxFile getFile(String path) {
        long contactId = -1;

        try {
            contactId = Long.parseLong(path);
        }
        catch (NumberFormatException e) {
            if (L.isErrorEnabled()) L.error("It is an invalid uri.", e);
            return null;
        }

        return getFileByContactId(contactId);
    }

    @Override
    public AxFile getFileByRawId(long rawId) {
        long contactId = PhotoFileUtils.getContactId(context.getContentResolver(), rawId);
        return getFileByContactId(contactId);
    }

    @Override
    public AxFile getFileByContactId(long contactId) {
        if (contactId < 0) {
            if (L.isErrorEnabled()) L.error("" /* TODO Error message */);
            return null;
        }

        DevicePhotoFile axFile = new DevicePhotoFile(context, rootAxFile, contactId);
        // TODO caching
        return axFile;
    }

    @Override
    public String toNativePath(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toVirtualPath(String path) {
        throw new UnsupportedOperationException();
    }
}
