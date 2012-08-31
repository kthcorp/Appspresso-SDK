package com.appspresso.waikiki.filesystem;

import java.io.File;
import android.content.Context;
import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileSystem;
import com.appspresso.api.fs.AxFileSystemManager;
import com.appspresso.waikiki.filesystem.errors.IOError;
import com.appspresso.waikiki.filesystem.errors.InvalidValuesError;
import com.appspresso.waikiki.filesystem.errors.NotFoundError;
import com.appspresso.waikiki.filesystem.errors.UnknownError;
import com.appspresso.waikiki.filesystem.files.AssetFile;
import com.appspresso.waikiki.filesystem.files.AssetProperties;
import com.appspresso.waikiki.filesystem.files.DefaultDirectory;
import com.appspresso.waikiki.filesystem.files.DefaultFile;
import com.appspresso.waikiki.filesystem.files.DefaultProperties;
import com.appspresso.waikiki.filesystem.filesystems.AssetFileSystem;
import com.appspresso.waikiki.filesystem.filesystems.ExternalFileSystem;
import com.appspresso.waikiki.filesystem.filesystems.InternalFileSystem;
import com.appspresso.waikiki.filesystem.filesystems.TemporaryFileSystem;
import com.appspresso.waikiki.filesystem.utils.PathUtils;

public class WkFileManager {
    private AxFileSystemManager axManager;

    public WkFileManager(AxFileSystemManager axManager) {
        this.axManager = axManager;
    }

    public void remountFileSystems(Context context) {
        AxFileSystem axFileSystem = null;
        String prefix = "wgt-package";

        axManager.unmount(prefix);
        axFileSystem = new AssetFileSystem(context.getAssets(), "ax_www");
        axManager.mount(prefix, axFileSystem, null);

        String[] internalNames =
                new String[] {"images", "videos", "music", "documents", "downloads", "wgt-private"};
        int length = internalNames.length;
        File root = null;
        for (int i = 0; i < length; i++) {
            prefix = internalNames[i];
            axFileSystem = axManager.getFileSystem(prefix);

            if (axFileSystem != null) {
                root = (File) axFileSystem.getRoot().getPeer();
                axManager.unmount(prefix);
                axFileSystem = new InternalFileSystem(root);
                axManager.mount(prefix, axFileSystem, null);
            }
        }

        String[] externalNames = new String[] {"removable"};
        length = externalNames.length;
        for (int i = 0; i < length; i++) {
            prefix = externalNames[i];
            axFileSystem = axManager.getFileSystem(prefix);

            if (axFileSystem != null) {
                root = (File) axFileSystem.getRoot().getPeer();
                axManager.unmount(prefix);
                axFileSystem = new ExternalFileSystem(root);
                axManager.mount(prefix, axFileSystem, null);
            }
        }

        String[] temporaryNames = new String[] {"wgt-private-tmp"};
        length = temporaryNames.length;

        for (int i = 0; i < length; i++) {
            prefix = temporaryNames[i];
            axFileSystem = axManager.getFileSystem(prefix);

            if (axFileSystem != null) {
                root = (File) axFileSystem.getRoot().getPeer();
                axManager.unmount(prefix);
                axFileSystem = new TemporaryFileSystem(root);
                axManager.mount(prefix, axFileSystem, null);
            }
        }
    }

    /**
     * 모드가 사용가능한지 검증한다.
     * 
     * @param mode mode
     * @return 사용가능한 모드라면 true, 그렇지 않다면 false
     */
    private boolean validateMode(String mode) {
        return "r".equals(mode) || "rw".equals(mode);
    }

    /**
     * 해당 경로가 모드에 대한 AxFile를 반환한다.
     * 
     * @param location 완전한 가상 경로
     * @return 해당 경로에 대한 AxFile를 반환. 유효하지 않은 경로일 경우 null.
     */
    private AxFile getAxFile(String fullPath) {
        AxFile axFile = axManager.getFile(fullPath);
        return axFile;
    }

    /**
     * @param axFile AxFile
     * @param mode 모드
     * @throws InvalidValuesError 해당 파일 시스템이 허용하지 않는 모드
     * @throws UnknownError 존재하지 않는 파일
     */
    private void validateParameter(AxFile axFile, String mode) throws InvalidValuesError,
            UnknownError {
        if (!validateMode(mode)) { throw new InvalidValuesError("Unknown mode"); }

        if (axFile == null || !axFile.exists()) { throw new UnknownError("The file does not exist."); }

        if ((axFile instanceof com.appspresso.api.fs.AssetFile) && !"r".equals(mode)) { throw new InvalidValuesError(
                "Unknown mode"); }
    }

    /**
     * 해당 경로와 모드에 대한 AxFile를 반환한다. 반드시 실제 존재하는 파일에 대한 WkFileHandle만을 반환하며, 잘못된 경로이거나 존재하지 않는 파일일 경우
     * NotFoundError가 발생한다.
     * 
     * @param location 완전한 가상 경로
     * @param mode 모드
     * @return 실제 존재하는 파일에 대한 WkFileHandle
     * @throws InvalidValuesError 유효한 모드가 아님
     * @throws UnknownError 잘못된 경로이거나 실제 존재하지 않는 파일
     */
    public WkFileHandle createFileHandle(String fullPath, String mode) throws InvalidValuesError,
            UnknownError {
        AxFile axFile = getAxFile(fullPath);
        validateParameter(axFile, mode);
        return new WkFileHandle(axFile, mode);
    }

    /**
     * 해당 경로와 모드에 대한 WkProperties를 반환한다. 반드시 실제 존재하는 파일에 대한 WkProperties만을 반환하며, 잘못된 경로이거나 존재하지 않는
     * 파일일 경우 NotFoundError가 발생한다.
     * 
     * @param fullPath 파일 전체 가상 경로
     * @param mode 모드
     * @return 파일에 대한 WkProperties
     * @throws InvalidValuesError 유효한 모드가 아님
     * @throws UnknownError 실제 존재하지 않는 파일임
     */
    public WkProperties createFileProperties(String fullPath, String mode)
            throws InvalidValuesError, UnknownError {
        AxFile axFile = getAxFile(fullPath);
        validateParameter(axFile, mode);

        if (axFile instanceof com.appspresso.api.fs.AssetFile) {
            return new AssetProperties(axFile);
        }
        else if (axFile instanceof com.appspresso.api.fs.DefaultFile) {
            return new DefaultProperties(axFile, mode);
        }
        else {
            throw new UnknownError("An unknown error has occurred.");
        }
    }

    /**
     * 해당 경로와 모드에 대한 WkFile을 반환한다. 반드시 실제 존재하는 파일에 대한 WkFile만을 반환하며, 잘못된 경로이거나 존재하지 않는 파일일 경우
     * NotFoundError가 발생한다.
     * 
     * @param fullPath 파일 전체 가상 경로
     * @param mode 모드
     * @return 파일에 대한 WkFile
     * @throws InvalidValuesError 유효한 모드가 아님
     * @throws IOError 파일이 아님
     * @throws UnknownError 실제 존재하지 않는 파일임
     */
    public WkFile createFile(String fullPath, String mode) throws InvalidValuesError, IOError,
            UnknownError {
        AxFile axFile = getAxFile(fullPath);
        validateParameter(axFile, mode);

        if (!axFile.isFile()) { throw new IOError("Not a file"); }

        if (axFile instanceof com.appspresso.api.fs.AssetFile) {
        	AxFileSystem axFileSystem = axManager.getFileSystem("wgt-private-tmp");
        	File tempDir = (File)axFileSystem.getRoot().getPeer();
        	
            return new AssetFile(axFile, mode, tempDir);
        }
        else if (axFile instanceof com.appspresso.api.fs.DefaultFile) {
            return new DefaultFile(axFile, mode);
        }
        else {
            throw new UnknownError("An unknown error has occurred.");
        }
    }

    /**
     * 해당 경로와 모드에 대한 WkDirectory을 반환한다. 반드시 실제 존재하는 파일에 대한 WkFile만을 반환하며, 잘못된 경로이거나 존재하지 않는 파일일 경우
     * NotFoundError가 발생한다.
     * 
     * @param fullPath 파일 전체 가상 경로
     * @param mode 모드
     * @return 파일에 대한 WkFile
     * @throws InvalidValuesError 유효한 모드가 아님
     * @throws IOError 디렉토리가 아님
     * @throws UnknownError 실제 존재하지 않는 파일임
     */
    public WkDirectory createDirectory(String fullPath, String mode) throws InvalidValuesError,
            IOError, UnknownError {
        AxFileSystem axFileSystem = axManager.getFileSystem(PathUtils.getPrefix(fullPath));
        if (axFileSystem == null) { throw new UnknownError("File handle is not valid"); }

        AxFile axFile = getAxFile(fullPath);
        validateParameter(axFile, mode);

        if (!axFile.isDirectory()) { throw new IOError("Not a directory"); }

        return new DefaultDirectory(axFileSystem, axFile, mode);
    }

    /**
     * 두 패스에 대한 파일시스템 배열을 반환한다. 첫번째 매개변수 fullPath는 이 파일시스템을 사용하려는 파일의 경로로 두번째 매개변수 oPath인 fullPath의
     * 하위 경로이어야 한다.
     * 
     * @param fullPath 파일 전체 가상 경로
     * @param oPath 전체 경로로서 fullPath의 하위 경로
     * @param dPath 다른 파일의 전에 경로
     * @return oPath와 dPath의 FileSystem 배열.
     * @throws NotFoundError 파일시스템을 찾을 수 없거나 유효하지 않은 경로
     */
    public WkFileSystem[] getFileSystems(String fullPath, String oPath, String dPath)
            throws NotFoundError {
        if (!oPath.startsWith(fullPath)) {
            // originFilePath은 현재 디렉토릭의 하위 파일이어야 함
            throw new NotFoundError("Invalid filePath");
        }

        if (dPath.startsWith(oPath)) {
            if (!dPath.equals(oPath) && dPath.charAt(oPath.length()) == File.separatorChar) {
                // destinationPath가 originalPath의 하위 경로
                throw new NotFoundError("Invalid filePath");
            }
        }

        WkFileSystem oFileSystem =
                (WkFileSystem) axManager.getFileSystem(PathUtils.getPrefix(oPath));
        if (oFileSystem == null) { throw new NotFoundError("Invalid filePath"); }

        WkFileSystem dFileSystem =
                (WkFileSystem) axManager.getFileSystem(PathUtils.getPrefix(dPath));
        if (dFileSystem == null) { throw new NotFoundError("Invalid filePath"); }

        return new WkFileSystem[] {oFileSystem, dFileSystem};
    }
}
