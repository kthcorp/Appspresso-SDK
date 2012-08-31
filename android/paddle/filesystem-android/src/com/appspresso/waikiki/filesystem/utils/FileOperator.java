package com.appspresso.waikiki.filesystem.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class FileOperator {
    public static final int BUFFER_SIZE = 4096;

    public static boolean createDirectory(File dir) {
        return dir.mkdirs();
    }

    public static boolean createFile(File file) {
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) return false;

        try {
            return file.createNewFile();
        }
        catch (IOException e) {
            return false;
        }
    }

    public static boolean delete(File file) {
        if (!file.exists()) return true;

        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (!delete(child)) return false;
            }
        }

        return file.delete();
    }

    public static boolean copyDirectory(File originFile, File destFile) throws IOException {
        File[] children = originFile.listFiles();
        int lenght = children.length;
        File child;
        File otherChild;

        for (int i = 0; i < lenght; i++) {
            child = children[i];
            otherChild = new File(destFile, child.getName());

            if (child.isDirectory()) {
                destFile.mkdirs();
                copyDirectory(child, otherChild);
            }
            else if (child.isFile()) {
                copyFile(child, otherChild, false);
            }
        }

        return true;
    }

    public static boolean copyFile(File file, File destFile, boolean requireParent)
            throws IOException {
        File parent = destFile.getParentFile();

        if (!parent.exists()) {
            if (requireParent || !parent.mkdirs()) return false;
        }

        if (!destFile.exists() && !destFile.createNewFile()) return false;

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            outputStream = new BufferedOutputStream(new FileOutputStream(destFile));

            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;
            while ((length = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            return true;
        }
        finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
    }

    public static boolean copyFile(InputStream inputStream, File destFile, boolean requireParent)
            throws IOException {
        File parent = destFile.getParentFile();

        if (!parent.exists()) {
            if (requireParent || !parent.mkdirs()) return false;
        }

        if (!destFile.createNewFile()) { // 새 파일을 생성하는데 실패했다.
            return false;
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(destFile);
            BufferedOutputStream bOutputStream = new BufferedOutputStream(outputStream);

            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;
            while ((length = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                bOutputStream.write(buffer, 0, length);
            }
            bOutputStream.flush();
            return true;
        }
        finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
    }

    public static boolean copyAndDeleteDirectory(File originFile, File destFile) throws IOException {
        if (!copyDirectory(originFile, destFile)) return false;
        return delete(originFile);
    }

    public static boolean copyAndDeleteFile(File originFile, File destFile) throws IOException {
        if (!copyFile(originFile, destFile, true)) return false;
        originFile.delete();
        return true;
    }

    public static boolean rename(File file, File destFile) {
        return file.renameTo(destFile);
    }

    public static String readAsText(InputStream inputStream, String encoding)
            throws UnsupportedEncodingException, OutOfMemoryError, IOException {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();

            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) {
                System.out.println(temp);
                buffer.append(temp);
            }

            return buffer.toString();
        }
        finally {
            closeQuietly(inputStream);
        }
    }

    public static void closeQuietly(InputStream in) {
        if (null != in) {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(OutputStream in) {
        if (null != in) {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
