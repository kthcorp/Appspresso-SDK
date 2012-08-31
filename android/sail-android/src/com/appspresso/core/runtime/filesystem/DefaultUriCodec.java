package com.appspresso.core.runtime.filesystem;

import java.io.File;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import com.appspresso.api.AxLog;
import com.appspresso.api.fs.FileSystemUtils;
import static com.appspresso.core.runtime.filesystem.FileSystemConstants.*;

public class DefaultUriCodec implements AxUriCodec {
    private static final Log L = AxLog.getLog(AxUriCodec.class);

    @Override
    public String encode(String path) {
        return FileSystemUtils.mergePath(URI_PROTOCOL, path);
    }

    @Override
    public String decode(String uri) {
        StringTokenizer tokens = new StringTokenizer(uri, File.separator);
        if (tokens.countTokens() < 2) return null;

        String path = File.separator + tokens.nextToken() + File.separator + tokens.nextToken();
        if (!URI_PROTOCOL.equals(path)) {
            if (L.isDebugEnabled()) {
                L.debug("Invalid file path."); // URI_PROTOCOL로 시작하지 않는다.
            }
            return null;
        }

        // Pattern pattern =
        // Pattern.compile(FileSystemConstants.PATH_COMPONENT_PATTERN);
        String component = null;
        path = "";
        while (tokens.hasMoreElements()) {
            component = tokens.nextToken();
            // if(!pattern.matcher((CharSequence)component).matches()) {
            // if(L.isDebugEnabled()) {
            // L.debug("Invalid file path."); // URI_PROTOCOL로 시작하지 않는다.
            // }
            // return null;
            // }
            path += File.separator + component;
        }

        try {
            return path.substring(1);
        }
        catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }
}
