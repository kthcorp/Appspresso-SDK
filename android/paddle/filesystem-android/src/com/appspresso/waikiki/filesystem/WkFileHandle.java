package com.appspresso.waikiki.filesystem;

import java.util.HashMap;
import java.util.Map;
import com.appspresso.api.AxPluginResult;
import com.appspresso.api.fs.AxFile;

public class WkFileHandle implements AxPluginResult {
    public static final String KEY_PATH = "path";
    public static final String KEY_NAME = "name";
    public static final String KEY_FULL_PATH = "fullPath";
    public static final String KEY_IS_DIRECTORY = "isDirectory";
    public static final String KEY_IS_FILE = "isFile";
    public static final String KEY_MODE = "mode";

    private final AxFile axFile;
    private final String mode;

    public WkFileHandle(AxFile axFile, String mode) {
        this.axFile = axFile;
        this.mode = mode;
    }

    /**
     * WkFileHandle의 AxFile를 반환한다.
     * 
     * @return AxFile
     */
    public AxFile getAxFile() {
        return axFile;
    }

    /**
     * 모드를 반환한다.
     * 
     * @return 모드
     */
    public String getMode() {
        return mode;
    }

    /**
     * Waikiki 2.0 Spec에 따른 파일의 path를 반환한다. 파일의 전체 경로에서 파일의 이름을 제외한 문자열을 말하며, 만약 해당 파일이 어떤 파일시스템의
     * 루트라면 path는 파일의 전체 경로와 동일하다.
     * 
     * @return path
     */
    public String getPath() {
        if (axFile.getParent() == null) { // axFile is root.
            return axFile.getPath();
        }

        String fullPath = axFile.getPath();
        String name = axFile.getName();

        int index = fullPath.length() - name.length();
        String path = fullPath.substring(0, index);

        return path;
    }

    /**
     * Waikiki 2.0 Spec에 따른 파일의 name을 반환한다. 파일이나 디렉토리의 이름을 말하며, path와 name을 더한 문자열을 파일의 전체 경로와 동일하다.
     * 만약 해당 파일이 어떤 파일시스템의 루트라면 빈 문자열을 반환한다. 실제 파일의 이름을 얻기 위해서는 getAxFile()로 직접 AxFile를 얻은 다음의 이
     * AxFile의 getName()를 호출한다.
     * 
     * @return 파일의 name. 파일이 루트일경우 빈 문자열
     */
    public String getName() {
        return (axFile.getParent() == null) ? "" : axFile.getName();
    }

    public String getFullPath() {
        return axFile.getPath();
    }

    @Override
    public Object getPluginResult() {
        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put(KEY_PATH, getPath());
        result.put(KEY_NAME, getName());
        result.put(KEY_FULL_PATH, getFullPath());
        result.put(KEY_IS_DIRECTORY, axFile.isDirectory());
        result.put(KEY_IS_FILE, axFile.isFile());
        result.put(KEY_MODE, mode);

        return result;
    }
}
