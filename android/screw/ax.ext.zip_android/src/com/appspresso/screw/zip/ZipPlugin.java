package com.appspresso.screw.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;

import com.appspresso.api.AxError;
import com.appspresso.api.AxLog;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.DefaultAxPlugin;

/**
 * ax.ext.zip
 */
public class ZipPlugin extends DefaultAxPlugin {

    private static final Log L = AxLog.getLog(ZipPlugin.class);

    private static final int BUF_SIZE = 16 * 1024;

    public void unzip(AxPluginContext ctx) {
        try {
            String path = ctx.getParamAsString(0);
            String virtualTargetDir = ctx.getParamAsString(1);

            // virtual -> real
            String nativePath = runtimeContext.getFileSystemManager().toNativePath(path);
            String nativeTargetDir =
                    runtimeContext.getFileSystemManager().toNativePath(virtualTargetDir);

            // TODO mungyu path(and virtualTagetDir) validation check

            ZipFile zipFile = new ZipFile(nativePath);
            File targetDir_ = new File(nativeTargetDir);

            long totalBytes = 0; // totalBytes
            for (Enumeration<? extends ZipEntry> zipEntries = zipFile.entries(); zipEntries
                    .hasMoreElements();) {
                ZipEntry zipEntry = zipEntries.nextElement();
                totalBytes += extractZipEntry(zipFile, zipEntry, targetDir_);
            }

            ctx.sendResult(totalBytes);
        }
        catch (AxError e) {
            ctx.sendError(e.getCode(), e.getMessage());
        }
        catch (IOException e) {
            ctx.sendError(AxError.IO_ERR, "failed to read zip entry");
        }
    }

    private long extractZipEntry(ZipFile zipFile, ZipEntry zipEntry, File targetDir)
            throws IOException {
        String entryName = zipEntry.getName();
        if (L.isTraceEnabled()) {
            L.trace("extractZipEntry: " + entryName);
        }
        long totalBytes = 0;
        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                if (L.isWarnEnabled()) {
                    L.warn("failed to mkdirs: " + targetDir);
                }
            }
        }
        File targetFile = new File(targetDir, entryName);
        if (zipEntry.isDirectory()) {
            if (!targetFile.mkdirs()) {
                if (L.isWarnEnabled()) {
                    L.warn("failed to mkdirs: " + targetDir);
                }
            }
        }
        else {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(zipFile.getInputStream(zipEntry), BUF_SIZE);
                out = new BufferedOutputStream(new FileOutputStream(targetFile), BUF_SIZE);
                // @@IOUtils.copy(in, out);
                byte[] buf = new byte[BUF_SIZE];
                int readBytes;
                while ((readBytes = in.read(buf)) != -1) {
                    out.write(buf, 0, readBytes);
                    totalBytes += readBytes;
                }
                out.flush();
            }
            finally {
                // @@IOUtils.closeQuietly(in);
                if (in != null) {
                    try {
                        in.close();
                        in = null;
                    }
                    catch (IOException ignored) {}
                }
                // @@IOUtils.closeQuietly(out);
                if (out != null) {
                    try {
                        out.close();
                        out = null;
                    }
                    catch (IOException ignored) {}
                }
            }
        }
        return totalBytes;
    }

}
