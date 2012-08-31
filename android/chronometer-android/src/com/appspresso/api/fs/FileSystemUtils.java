/*
 * Appspresso
 * 
 * Copyright (c) 2011 KT Hitel Corp.
 * 
 * This source is subject to Appspresso license terms. Please see http://appspresso.com/ for more
 * information.
 * 
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package com.appspresso.api.fs;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import android.text.TextUtils;
import com.appspresso.api.AxLog;

/**
 * *** INTERNAL USE ONLY ***
 * @version 1.0
 */
public class FileSystemUtils {
    public static final Log L = AxLog.getLog(AxFile.class.getSimpleName());
    public static final String EMPTY_PATH = "";
    public static final int BUFFER_SIZE = 4096;

    /**
     * path component 들을 합쳐 하나의 path로 만들어 반환.
     * 
     * @param paths path component들
     * @return 하나로 합쳐진 path
     */
    public static String mergePath(String... paths) {
        String result = EMPTY_PATH;
        for (String path : paths) {
            if (0 == path.length()) continue;
            if (0 != result.length()) result += File.separator;
            result += path;
        }

        return result;
    }

    /**
     * path의 양옆에 path separator를 제거.
     * 
     * @param path separator를 제거할 path
     * @return path separator가 제거된 path
     */
    public static String removeExtraFileSeparator(String path) {
        int index = path.indexOf(File.separator);
        if (0 == index) {
            path = path.substring(1);
        }

        index = path.lastIndexOf(File.separator);
        if (path.length() - 1 == index) {
            path = path.substring(0, index);
        }

        return path;
    }

    /**
     * path를 path separator를 기준으로 분리.
     * 
     * @param path 분리할 path
     * @return 분리된 path component의 배열
     */
    public static String[] split(String path) {
        String[] pathArray = path.split(File.separator);
        ArrayList<String> newPathArray = new ArrayList<String>();

        int length = pathArray.length;
        for (int i = 0; i < length; i++) {
            if (TextUtils.isEmpty(pathArray[i])) continue;
            newPathArray.add(pathArray[i]);
        }

        return newPathArray.toArray(new String[newPathArray.size()]);
    }

    /**
     * path가 특정 prefix를 가지고 있는지 확인.
     * 
     * @param path 확인할 path
     * @param prefix path가 가지고 있는지 확인할 prefix
     * @return path가 prefix로 시작하면 {@literal true}, 그렇지 않으면 {@literal false}를 반환.
     */
    public static boolean hasPrefix(String path, String prefix) {
        if (!path.startsWith(prefix)) return false;
        if (path.equals(prefix)) return true;
        return File.separatorChar == path.charAt(prefix.length());
    }

    /**
     * path에서 특정 prefix를 제외한 나머지 path만을 추출.
     * 
     * @param path 대상 path
     * @param prefix 제외할 prefix
     * @return 특정 prefix가 제외된 path
     */
    public static String extractRelativePath(String path, String prefix) {
        try {
            return path.substring(prefix.length() + 1);
        }
        catch (StringIndexOutOfBoundsException e) {
            if (path.equals(prefix)) return EMPTY_PATH;
            return null;
        }
    }

    /**
     * path에서 name만을 추출. 여기서 name은 파일의 이름으로 path의 마지막 path component를 의미.
     * 
     * @param path name을 추출할 path
     * @return 추출된 path
     */
    public static String extractName(String path) {
        int index = path.lastIndexOf(File.separator);
        return path.substring(index + 1);
    }

    /**
     * path에서 prefix를 다른 prefix로 대체한다.
     * 
     * @param path path
     * @param oldPrefix 원래 prefix
     * @param newPrefix 대체할 새 prefix
     * @return prefix가 대체된 path
     */
    public static String replacePrefix(String path, String oldPrefix, String newPrefix) {
        String relativePath = extractRelativePath(path, oldPrefix);
        return mergePath(newPrefix, relativePath);
    }

    /**
     * path을 name와 name을 제외한 path로 나누어 반환.
     * 
     * @param path 나눌 path
     * @return 첫번재 항목에서는 name을 제외한 path가, 두번째 path에는 name이 있는 크기가 2인 배열
     */
    public static String[] divideToParentPathAndName(String path) {
        path = removeExtraFileSeparator(path);

        int index = path.lastIndexOf(File.separator);
        if (-1 == index) {
            return new String[] {EMPTY_PATH, path};
        }
        else {
            return new String[] {path.substring(0, index), path.substring(index + 1)};
        }
    }

    /**
     * InputStream으로부터 특정 path에 위치하는 파일로 복사.
     * 
     * @param inputStream 원본 InputStream
     * @param destFilePath 복사본 파일이 위치할 path
     * @param overwrite 해당 path에 이미 파일이 존재할 때 덮어쓰기 여부
     * @return 복사되었으면 {@literal true}, 그렇지 않으면 {@literal false}
     * @throws IOException 파일을 복사할 수 없음.
     */
    public static boolean copy(InputStream inputStream, String destFilePath, boolean overwrite)
            throws IOException {
        File destFile = new File(destFilePath);
        File parent = destFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs() && !parent.mkdir()) {
            return false;
        }

        if (destFile.exists()) {
            if (!overwrite) {
                return false;
            }
            if (!destFile.delete()) {
                return false;
            }
        }

        if (!destFile.createNewFile()) {
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

    /**
     * InputStream을 닫음.
     * 
     * @param in 닫을 InputStream
     */
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

    /**
     * OutputStream을 닫음.
     * 
     * @param out 닫을 OutputStream
     */
    public static void closeQuietly(OutputStream out) {
        if (null != out) {
            try {
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * InputStream으로부터 특정 인코딩으로 텍스트를 읽어와 반환.
     * 
     * @param inputStream 대상 InputStream
     * @param encoding 인코딩
     * @return 읽어온 텍스트
     * @throws UnsupportedEncodingException 지원하지 않는 인코딩
     * @throws OutOfMemoryError 작업 시 메모리 부족
     * @throws IOException 텍스를 읽어올 수 없음
     */
    public static String readAsText(InputStream inputStream, String encoding)
            throws UnsupportedEncodingException, OutOfMemoryError, IOException {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();

            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) {
                buffer.append(temp);
            }

            return buffer.toString();
        }
        finally {
            closeQuietly(inputStream);
        }
    }

    /**
     * path가 null이거나 빈 문자열인지 검사
     * 
     * @param path 검사할 path
     * @return path가 null이거나 빈 문자열이면 {@literal true}, 그렇지 않으면 {@literal false}
     */
    public static boolean isEmptyPath(String path) {
        return EMPTY_PATH.equals(path);
    }
}
