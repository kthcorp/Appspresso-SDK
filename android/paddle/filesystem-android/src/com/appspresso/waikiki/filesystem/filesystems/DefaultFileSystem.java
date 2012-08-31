package com.appspresso.waikiki.filesystem.filesystems;

import java.io.File;
import java.io.IOException;
import com.appspresso.api.AxError;
import com.appspresso.api.fs.AssetFilePeer;
import com.appspresso.api.fs.AxFile;
import com.appspresso.waikiki.filesystem.WkFileSystem;
import com.appspresso.waikiki.filesystem.errors.IOError;
import com.appspresso.waikiki.filesystem.errors.NotFoundError;
import com.appspresso.waikiki.filesystem.errors.UnknownError;
import com.appspresso.waikiki.filesystem.utils.FileOperator;
import com.appspresso.waikiki.filesystem.utils.PathUtils;

public abstract class DefaultFileSystem extends com.appspresso.api.fs.DefaultFileSystem
        implements
            WkFileSystem {
    public DefaultFileSystem(File rootDirectory) {
        super(rootDirectory);
    }

    private void validateFiles(AxFile originFile, AxFile destFile, boolean overwrite)
            throws NotFoundError, IOError {
        if (originFile == null || !originFile.exists()) {
            // 원본 파일에 대한 경로가 잘못되었거나 실제 존재하지 않는 파일임
            throw new NotFoundError("Invalid filePath");
        }

        if (destFile == null) {
            // 목적 파일에 대한 경로가 잘못되었음
            throw new NotFoundError("Invalid filePath");
        }

        if (destFile.exists()) {
            if (!overwrite) {
                // 목적 파일이 이미 존재하나 overwrite가 false
                throw new IOError("Permission denied");
            }

            if (!originFile.isFile() || !destFile.isFile()) {
                // 이미 존재하는 파일을 덮어쓰기 하려는 경우 둘다 파일이어야 함
                throw new IOError("Permission denied");
            }
        }
    }

    @Override
    public void copyToHere(WkFileSystem otherSystem, String originPath, String destPath,
            boolean overwrite) throws AxError {
        originPath = PathUtils.getRelativePath(originPath, otherSystem.getRoot().getPath());
        destPath = PathUtils.getRelativePath(destPath, getRoot().getPath());

        AxFile originFile = otherSystem.getFile(originPath);
        AxFile destFile = getFile(destPath);
        validateFiles(originFile, destFile, overwrite);

        // 원본과 복사본의 경로가 같을 때 덮어쓰기가 허용되면 복사 성공으로 간주
        if (originFile.getPath().equals(destFile.getPath()) && overwrite) return;

        File dFile = (File) destFile.getPeer();
        dFile.delete();

        boolean success = false;
        if (originFile instanceof com.appspresso.api.fs.DefaultFile) {
            File oFile = (File) originFile.getPeer();
            try {
                if (oFile.isFile()) {
                    success = FileOperator.copyFile(oFile, dFile, true);
                }
                else if (oFile.isDirectory()) {
                    success = FileOperator.copyDirectory(oFile, dFile);
                }
                else {
                    // Nothing to do.
                }
            }
            catch (IOException e) {
                // Nothing to do.
            }
            finally {
                if (!success) throw new UnknownError("An unknonwn error has occurred.");
            }
        }
        else if (originFile instanceof com.appspresso.api.fs.AssetFile) {
            try {
                if (originFile.isFile()) {
                    AssetFilePeer peer = (AssetFilePeer) (originFile.getPeer());
                    success = FileOperator.copyFile(peer.createInputStream(), dFile, true);
                }
                else if (originFile.isDirectory()) {
                    success =
                            copyAssetDirectory((com.appspresso.api.fs.AssetFile) originFile, dFile);
                }
                else {
                    // Nothing to do.
                }
            }
            catch (IOException e) {
                // Nothing to do.
            }
            finally {
                if (!success) throw new UnknownError("An unknonwn error has occurred.");
            }
        }
        else {
            throw new UnknownError("An unknonwn error has occurred.");
        }
    }

    private boolean copyAssetDirectory(com.appspresso.api.fs.AssetFile assetDir, File destDir)
            throws IOException {
        AxFile[] children = assetDir.listFiles(null);
        com.appspresso.api.fs.AssetFile child;
        int length = children.length;
        File otherChild;

        for (int i = 0; i < length; i++) {
            child = (com.appspresso.api.fs.AssetFile) children[i];
            otherChild = new File(destDir, child.getName());

            if (child.isDirectory()) {
                otherChild.mkdirs();
                if (!copyAssetDirectory(child, otherChild)) return false;
            }
            else if (child.isFile()) {
                AssetFilePeer peer = (AssetFilePeer) (child.getPeer());
                if (!FileOperator.copyFile(peer.createInputStream(), otherChild, false))
                    return false;
            }
            else {
                // Nothing to do.
            }
        }

        return true;
    }

    @Override
    public void moveToHere(WkFileSystem otherSystem, String originPath, String destPath,
            boolean overwrite) throws AxError {
        originPath = PathUtils.getRelativePath(originPath, otherSystem.getRoot().getPath());
        destPath = PathUtils.getRelativePath(destPath, getRoot().getPath());

        AxFile originFile = otherSystem.getFile(originPath);
        AxFile destFile = getFile(destPath);
        validateFiles(originFile, destFile, overwrite);

        if (originFile.equals(otherSystem.getRoot())) { throw new IOError("Permission denied"); }

        // 원본과 복사본의 경로가 같을 때 덮어쓰기가 허용되면 이동 성공으로 간주
        if (originFile.getPath().equals(destFile.getPath()) && overwrite) return;

        File dFile = (File) destFile.getPeer();
        boolean succeedToMove = false;
        try {
            if (originFile instanceof com.appspresso.api.fs.DefaultFile) {
                File oFile = (File) originFile.getPeer();

                if (onSameMount(otherSystem)) {
                    // createParent(dFile); // 부모 디렉토리를 만들지 않음. 부모 디렉토리 존재하지 않으면
                    // 예러
                    succeedToMove = FileOperator.rename(oFile, dFile);
                }
                else {
                    if (oFile.isFile()) {
                        succeedToMove = FileOperator.copyAndDeleteFile(oFile, dFile);
                    }
                    else if (oFile.isDirectory()) {
                        succeedToMove = FileOperator.copyAndDeleteDirectory(oFile, dFile);
                    }
                    else {
                        // Nothing to do.
                    }
                }
            }
            else if (originFile instanceof com.appspresso.api.fs.AssetFile) {
                // Nothing to do.
            }
            else {
                // Nothing to do.
            }
        }
        catch (Exception e) {
            // Nothing to do.
        }
        finally {
            if (!succeedToMove) { throw new UnknownError("An unknonwn error has occurred."); }
        }
    }

    private void createParent(File dFile) {
        File parent;

        do {
            parent = dFile.getParentFile();
            parent.mkdirs();
        }
        while (!parent.exists());
    }
}
