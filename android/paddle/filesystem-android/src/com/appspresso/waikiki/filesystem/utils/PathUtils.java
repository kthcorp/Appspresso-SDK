package com.appspresso.waikiki.filesystem.utils;

import java.io.File;
import java.util.regex.Pattern;

public class PathUtils {
    public static String convertToValidPath(String path) {
        if (path == null) return null;

        if (isInvalidPathComponent(path)) return null;

        Pattern pattern = Pattern.compile("/+");
        String[] components = pattern.split(path);

        int length = components.length;
        if (length == 0) return null;

        int index = 0;
        if (components[index].equals("")) index++;

        String component;
        path = "";
        for (; index < length; index++) {
            component = components[index];
            if (isInvalidPathComponent(component)) return null;
            path += File.separator + component;
        }

        return path.substring(1);
    }

    public static boolean isInvalidPathComponent(String pathComponent) {
        if (pathComponent == null || pathComponent.length() == 0 || pathComponent.equals(".")
                || pathComponent.equals("..")) { return true; }
        return Pattern.compile("[ \t\n\r]+").split(pathComponent).length == 0;
    }

    public static String mergePath(String... paths) {
        String result = "";
        for (String path : paths) {
            if (0 == path.length()) continue;
            if (0 != result.length()) result += File.separator;
            result += path;
        }

        return result;
    }

    public static String getPrefix(String fullPath) {
        if (fullPath == null || fullPath.length() == 0) return null;
        int index = fullPath.indexOf(File.separator);
        if (index == -1) return fullPath;
        if (index == 0) return null;
        return fullPath.substring(0, index);
    }

    public static String getRelativePath(String fullPath, String prefix) {
        if (!fullPath.startsWith(prefix)) return null;
        if (fullPath.equals(prefix)) return "";
        return fullPath.substring(prefix.length() + 1);
    }
}
