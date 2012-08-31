package com.appspresso.waikiki.filesystem.files;

import java.io.File;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileSystem;
import com.appspresso.api.fs.FileSystemUtils;
import com.appspresso.waikiki.filesystem.WkDirectory;
import com.appspresso.waikiki.filesystem.WkFileFilter;
import com.appspresso.waikiki.filesystem.WkFileHandle;
import com.appspresso.waikiki.filesystem.errors.NotFoundError;
import com.appspresso.waikiki.filesystem.errors.UnknownError;
import com.appspresso.waikiki.filesystem.errors.IOError;
import com.appspresso.waikiki.filesystem.errors.InvalidValuesError;
import com.appspresso.waikiki.filesystem.utils.FileOperator;
import com.appspresso.waikiki.filesystem.utils.PathUtils;

public class DefaultDirectory implements WkDirectory {
    private AxFileSystem axFileSystem;
    private AxFile axFile;
    private String mode;
    private boolean isRootDir;
    private boolean readonly;

    public DefaultDirectory(AxFileSystem axFileSystem, AxFile axFile, String mode) {
        if (!axFile.isDirectory()) { throw new IOError(
                "The operation must be launched on a file (not a directory)."); }

        this.axFileSystem = axFileSystem;
        this.axFile = axFile;
        this.mode = mode;
        this.isRootDir = axFileSystem.getRoot().equals(axFile);
        this.readonly = !"rw".equals(mode);
    }

    /**
     * 현재 파일시스템에 상대적인 경로로 변환한다.
     * 
     * @param relativePath
     * @return
     */
    private String toPath(String relativePath) {
        String path = FileSystemUtils.mergePath(axFile.getPath(), relativePath);
        String prefix = axFileSystem.getRoot().getPath();
        return PathUtils.getRelativePath(path, prefix);
    }

    /**
     * 전달된 전체 경로가 현재 디렉토리의 하위 경로가 아닐 경우는 null을 반환한다. 현재 디렉토리의 경롱일때는 현재 디렉토리의 경로를 반환한다.
     * 
     * @param relativePath
     * @return
     */
    private String toNativePath(String fullPath) {
        String relativePath = PathUtils.getRelativePath(fullPath, axFile.getPath());
        if (relativePath == null) return null;

        File peer = (File) axFile.getPeer();
        File childFile = new File(peer, relativePath);
        return childFile.getAbsolutePath();
    }

    /**
     * 하위 파일 경로인지 검사한다.
     * 
     * @param path 검사할 하위 경로
     * @param allowSelf 자식 경로가 자기 자신도 가능한지 여부
     * @return 해당 경로가 현재 디렉토리의 하위 경로이면 true, 아니면 false
     */
    private boolean validateChildPath(String virtualPath, boolean allowSelf) {
        String selfPath = axFile.getPath();
        if (!allowSelf && selfPath.equals(virtualPath)) return false;
        return virtualPath.startsWith(selfPath);
    }

    @Override
    public WkFileHandle createDirectory(String dirPath) throws InvalidValuesError, IOError,
            UnknownError {
        if (readonly) { throw new IOError("Fail to create file because it is read-only."); }

        String convertPath = PathUtils.convertToValidPath(dirPath);
        if (convertPath == null) { throw new InvalidValuesError("Invalid path"); }

        File peer = (File) axFile.getPeer();
        File newDir = new File(peer, convertPath);
        if (newDir.exists()) { throw new IOError("dirPath already exists."); }

        if (!FileOperator.createDirectory(newDir)) { throw new UnknownError(
                "Fail to create the directory"); }

        String path = toPath(convertPath);
        AxFile axFile = axFileSystem.getFile(path);

        return new WkFileHandle(axFile, mode);
    }

    @Override
    public WkFileHandle createFile(String filePath) throws InvalidValuesError, IOError,
            UnknownError {
        if (readonly) { throw new IOError("Fail to create file because it is read-only."); }

        String convertPath = PathUtils.convertToValidPath(filePath);
        if (convertPath == null) { throw new InvalidValuesError("Invalid path"); }

        File peer = (File) axFile.getPeer();
        File newFile = new File(peer, convertPath);

        if (newFile.exists()) { throw new IOError("The file already exists."); }

        if (!FileOperator.createFile(newFile)) { throw new UnknownError("Fail to create the file."); }

        String path = toPath(convertPath);
        AxFile axFile = axFileSystem.getFile(path);

        return new WkFileHandle(axFile, mode);
    }

    @Override
    public void deleteDirectory(String dirPath, boolean recursive) throws NotFoundError, IOError,
            UnknownError {
        if (readonly) { throw new IOError("Fail to create file because it is read-only."); }

        if (!validateChildPath(dirPath, !isRoot())) { throw new NotFoundError(
                "The target directory is not in current directory."); }

        String nativePath = toNativePath(dirPath);
        File dir = new File(nativePath);
        if (!dir.exists()) { throw new NotFoundError("The directory does not exist."); }

        if (!dir.isDirectory()) { throw new NotFoundError("The directory is not valid"); }

        if (!recursive && dir.list().length > 0) { throw new IOError("\"recursive\" is false."); }

        if (!FileOperator.delete(dir)) { throw new UnknownError("Fail to delete the directory"); }
    }

    @Override
    public void deleteFile(String filePath) {
        if (readonly) { throw new IOError("Fail to create file because it is read-only."); }

        if (!validateChildPath(filePath, false)) { throw new NotFoundError(
                "The target file is not in current directory."); }

        String nativePath = toNativePath(filePath);
        File file = new File(nativePath);
        if (!file.exists()) { throw new NotFoundError("The file does not exist."); }

        if (!file.isFile()) { throw new NotFoundError("The file is not valid."); }

        if (!FileOperator.delete(file)) { throw new UnknownError("Fail to delete the file."); }
    }

    @Override
    public WkFileHandle resolve(String filePath) {
        String convertPath = PathUtils.convertToValidPath(filePath);
        if (convertPath == null) { throw new InvalidValuesError("Invalid path"); }

        convertPath = axFile.getPath() + File.separator + convertPath;
        String prefix = axFileSystem.getRoot().getPath();
        convertPath = PathUtils.getRelativePath(convertPath, prefix);

        AxFile axFile = axFileSystem.getFile(convertPath);
        if (!axFile.exists()) { throw new NotFoundError("The file does not exist."); }

        return new WkFileHandle(axFile, mode);
    }

    @Override
    public WkFileHandle[] listFiles(WkFileFilter filter) {
        AxFile[] childFiles = axFile.listFiles(filter);

        int length = childFiles.length;
        WkFileHandle[] infoList = new WkFileHandle[length];
        for (int i = 0; i < length; i++) {
            infoList[i] = new WkFileHandle(childFiles[i], mode);
        }

        return infoList;
    }

    @Override
    public boolean isRoot() {
        return isRootDir;
    }
}
