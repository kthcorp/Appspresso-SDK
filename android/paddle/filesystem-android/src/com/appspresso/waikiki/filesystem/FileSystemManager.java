package com.appspresso.waikiki.filesystem;

import com.appspresso.api.AxRuntimeContext;
import com.appspresso.api.AxError;
import com.appspresso.api.AxPluginContext;
import com.appspresso.api.DefaultAxPlugin;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileSystemManager;
import com.appspresso.waikiki.filesystem.errors.IOError;
import com.appspresso.waikiki.filesystem.errors.InvalidValuesError;
import com.appspresso.waikiki.filesystem.errors.NotFoundError;
import com.appspresso.waikiki.filesystem.utils.DataConverter;

public class FileSystemManager extends DefaultAxPlugin {
    public static final String FEATURE_DEFAULT = "http://wacapps.net/api/filesystem";
    public static final String FEATURE_READ = "http://wacapps.net/api/filesystem.read";
    public static final String FEATURE_WRITE = "http://wacapps.net/api/filesystem.write";

    private AxFileSystemManager axManager;
    private WkFileManager wkManager;
    private FileStreamFactory streamFactory;

    private boolean canRead;
    private boolean canWrite;

    @Override
    public void activate(AxRuntimeContext runtimeContext) {
        super.activate(runtimeContext);

        runtimeContext.requirePlugin("deviceapis");

        canRead =
                runtimeContext.isActivatedFeature(FEATURE_DEFAULT)
                        || runtimeContext.isActivatedFeature(FEATURE_READ);
        canWrite =
                runtimeContext.isActivatedFeature(FEATURE_DEFAULT)
                        || runtimeContext.isActivatedFeature(FEATURE_WRITE);

        if (!canRead && !canWrite) { throw new AxError(AxError.SECURITY_ERR, "Permission denied"); }

        this.axManager = runtimeContext.getFileSystemManager();
        this.wkManager = new WkFileManager(axManager);
        wkManager.remountFileSystems(runtimeContext.getActivity());
        streamFactory = FileStreamFactory.getInstance();
    }

    @Override
    public void deactivate(AxRuntimeContext runtimeContext) {
        streamFactory.close();
        super.deactivate(runtimeContext);
    }

    // /////////////////////////////////////////////////////////////////////////
    // File manager

    public void getMaxPathLength(AxPluginContext context) {
        context.sendResult(4096);
    }

    public void resolve(AxPluginContext context) {
        String location = context.getParamAsString(0);
        String mode = context.getParamAsString(1);

        WkFileHandle properties = wkManager.createFileHandle(location, mode);
        context.sendResult(properties);
    }

    // File properties
    //
    //

    public void getParent(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);

        WkProperties file = wkManager.createFileProperties(fullPath, mode);
        context.sendResult(file.getParent());
    }

    public void getReadOnly(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);

        WkProperties file = wkManager.createFileProperties(fullPath, mode);
        context.sendResult(file.readOnly());
    }

    public void isFile(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);

        WkProperties file = wkManager.createFileProperties(fullPath, mode);
        context.sendResult(file.isFile());
    }

    public void isDirectory(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);

        WkProperties file = wkManager.createFileProperties(fullPath, mode);
        context.sendResult(file.isDirectory());
    }

    public void getCreated(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);

        WkProperties file = wkManager.createFileProperties(fullPath, mode);
        long created = file.getCreated();
        context.sendResult((created == -1) ? null : created);
    }

    public void getModified(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);

        WkProperties file = wkManager.createFileProperties(fullPath, mode);
        context.sendResult(file.getModified());
    }

    public void getFileSize(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);

        WkProperties file = wkManager.createFileProperties(fullPath, mode);
        long fileSize = file.getFileSize();
        context.sendResult((fileSize == -1) ? null : fileSize);
    }

    public void getLength(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);

        WkProperties file = wkManager.createFileProperties(fullPath, mode);
        long length = file.getLength();
        context.sendResult((length == -1) ? null : length);
    }

    // File
    //
    //

    public void createDirectory(AxPluginContext context) {
        ensureCanWrite();

        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);
        String dirPath = context.getParamAsString(1);

        WkDirectory targetDir = wkManager.createDirectory(fullPath, mode);
        context.sendResult(targetDir.createDirectory(dirPath));
    }

    public void createFile(AxPluginContext context) {
        ensureCanWrite();

        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);
        String filePath = context.getParamAsString(1);

        WkDirectory targetDir = wkManager.createDirectory(fullPath, mode);
        context.sendResult(targetDir.createFile(filePath));
    }

    public void deleteDirectory(AxPluginContext context) {
        ensureCanWrite();

        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);
        String dirPath = context.getParamAsString(1);
        boolean recursive = context.getParamAsBoolean(2);

        WkDirectory targetDir = wkManager.createDirectory(fullPath, mode);
        targetDir.deleteDirectory(dirPath, recursive);
        context.sendResult();
    }

    public void deleteFile(AxPluginContext context) {
        ensureCanWrite();

        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);
        String filePath = context.getParamAsString(1);

        WkDirectory targetDir = wkManager.createDirectory(fullPath, mode);
        targetDir.deleteFile(filePath);
        context.sendResult();
    }

    public void listFiles(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);

        WkFileFilter filter = null;
        try {
            filter = new WkFileFilter(context.getParamAsMap(1));
        }
        catch (Exception ignored) {
            // Nothing to do.
        }

        WkDirectory targetDir = wkManager.createDirectory(fullPath, mode);
        context.sendResult(targetDir.listFiles(filter));
    }

    public void copyTo(AxPluginContext context) {
        ensureCanWrite();

        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        // String mode = context.getNamedParamAsString(0,
        // WkFileHandle.KEY_MODE);
        String originFilePath = context.getParamAsString(1);
        String destinationFilePath = context.getParamAsString(2);
        boolean overwrite = context.getParamAsBoolean(3);

        WkFileSystem[] fileSystems =
                wkManager.getFileSystems(fullPath, originFilePath, destinationFilePath);
        fileSystems[1].copyToHere(fileSystems[0], originFilePath, destinationFilePath, overwrite);
        context.sendResult();
    }

    public void moveTo(AxPluginContext context) {
        ensureCanWrite();

        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);
        if (!"rw".equals(mode)) { throw new IOError("Permission denied"); }

        String originFilePath = context.getParamAsString(1);
        String destinationFilePath = context.getParamAsString(2);
        boolean overwrite = context.getParamAsBoolean(3);

        WkFileSystem[] fileSystems =
                wkManager.getFileSystems(fullPath, originFilePath, destinationFilePath);
        fileSystems[1].moveToHere(fileSystems[0], originFilePath, destinationFilePath, overwrite);
        context.sendResult();
    }

    public void openStream(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);
        String streamMode = context.getParamAsString(1);
        String encoding = context.getParamAsString(2);

        if ("r".equals(streamMode)) {
            ensureCanRead();
        }
        else if ("w".equals(streamMode) || "a".equals(streamMode)) {
            ensureCanWrite();

            if (!"rw".equals(mode)) { throw new IOError("Permission denied"); }

        }
        else {
            throw new InvalidValuesError("Permission denied");
        }

        WkFile targetFile = wkManager.createFile(fullPath, mode);
        WkFileStream fileStream = targetFile.openStream(streamMode, encoding);
        context.sendResult(fileStream);
    }

    public void readAsText(AxPluginContext context) {
        ensureCanRead();

        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);
        String encoding = context.getParamAsString(1);

        try {
            WkFile file = wkManager.createFile(fullPath, mode);
            context.sendResult(file.readAsText(encoding));
        }
        catch (AxError error) {
            // 일반적으로 파일 핸들이 잘못되었을 경우엔 UnknownError이나
            // WAC 2.0의 readAsText는 IOError
            // 파일 핸들이 잘못되었을 경우 == 파일이 존재하지 않을 경우
            // 이렇게 하고 싶진 않다.
            if (error.getCode() == AxError.UNKNOWN_ERR) {
                AxFile axFile = axManager.getFile(fullPath);
                if (axFile == null || !axFile.exists()) { throw new NotFoundError(
                        "The file does not exist."); }
            }

            context.sendError(error);
        }
    }

    public void resolveFilePath(AxPluginContext context) {
        String fullPath = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        String mode = context.getNamedParamAsString(0, WkFileHandle.KEY_MODE);
        String filePath = context.getParamAsString(1);

        WkDirectory targetDir = wkManager.createDirectory(fullPath, mode);
        context.sendResult(targetDir.resolve(filePath));
    }

    public void toURI(AxPluginContext context) {
        String location = context.getNamedParamAsString(0, WkFileHandle.KEY_FULL_PATH);
        context.sendResult(axManager.toUri(location));
    }

    // File stream
    //
    //

    public void close(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();
        streamFactory.close(handle);
        context.sendResult();
    }

    public void read(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();
        long charCount = context.getParamAsNumber(1).longValue();
        int intCharCount = DataConverter.convertToInteger(charCount);

        WkReadFileStream readStream = streamFactory.getReadFileStream(handle);
        context.sendResult(readStream.read(intCharCount));
    }

    public void readBase64(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();
        long byteCount = context.getParamAsNumber(1).longValue();
        int intByteCount = DataConverter.convertToInteger(byteCount);

        WkReadFileStream readStream = streamFactory.getReadFileStream(handle);
        context.sendResult(readStream.readBase64(intByteCount));
    }

    public void readBytes(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();
        long byteCount = context.getParamAsNumber(1).longValue();
        int intByteCount = DataConverter.convertToInteger(byteCount);

        WkReadFileStream readStream = streamFactory.getReadFileStream(handle);
        byte[] byteArray = readStream.readBytes(intByteCount);
        int[] unsignedArray = DataConverter.convertToIntegerArray(byteArray);
        context.sendResult(unsignedArray);
    }

    public void write(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();
        String data = context.getParamAsString(1);

        WkWriteFileStream fileStream = streamFactory.getWriteFileStream(handle);
        fileStream.write(data);
        context.sendResult();
    }

    public void writeBase64(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();
        String data = context.getParamAsString(1);

        WkWriteFileStream fileStream = streamFactory.getWriteFileStream(handle);
        fileStream.writeBase64(data);
        context.sendResult();
    }

    public void writeBytes(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();
        Number[] data = context.getParamAsNumberArray(1);

        WkWriteFileStream fileStream = streamFactory.getWriteFileStream(handle);
        byte[] byteData = DataConverter.convertToByteArray(data);
        fileStream.writeBytes(byteData);
        context.sendResult();
    }

    public void getBytesAvailable(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();

        WkFileStream fileStream = streamFactory.getFileStream(handle);
        context.sendResult(fileStream.getBytesAvailable());
    }

    public void getPosition(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();

        WkFileStream fileStream = streamFactory.getFileStream(handle);
        context.sendResult(fileStream.getPosition());
    }

    public void setPosition(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();
        long position = context.getParamAsNumber(1).longValue();

        WkFileStream fileStream = streamFactory.getFileStream(handle);
        fileStream.setPosition(position);
        context.sendResult();
    }

    public void isEOF(AxPluginContext context) {
        long handle = context.getParamAsNumber(0).longValue();

        WkFileStream fileStream = streamFactory.getFileStream(handle);
        context.sendResult(fileStream.isEof());
    }

    // Utils
    //
    //

    public void ensureCanRead() {
        if (!canRead) { throw new AxError(AxError.SECURITY_ERR, "Permission denied"); }
    }

    public void ensureCanWrite() {
        if (!canWrite) { throw new AxError(AxError.SECURITY_ERR, "Permission denied"); }
    }
}
