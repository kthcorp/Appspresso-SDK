package com.appspresso.core.runtime.server.kraken;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a simple mime-type database.
 * <p/>
 * NOTE: supports well-known(essential) mime types only!
 * 
 */
public class MimeTypeUtils {

    /**
     * fallback mime type
     */
    public static final String MIME_TYPE_BINARY = "application/octet-stream";

    //
    // well-known mimetypes
    //

    public static final String MIME_TYPE_HTML = "text/html";
    public static final String MIME_TYPE_XHTML = "application/xhtml+xml";
    public static final String MIME_TYPE_CSS = "text/css";
    public static final String MIME_TYPE_JS = "application/javascript";
    public static final String MIME_TYPE_XML = "text/xml";
    public static final String MIME_TYPE_TEXT = "text/plain";
    public static final String MIME_TYPE_JSON = "application/json";
    public static final String MIME_TYPE_PNG = "image/png";
    public static final String MIME_TYPE_JPEG = "image/jpeg";
    public static final String MIME_TYPE_GIF = "image/gif";
    public static final String MIME_TYPE_SVG = "image/svg+xml";

    // TODO: add more mime types here...
    public static final String MIME_TYPE_WGT = "application/widget";

    //
    // well-known extensions
    //

    public static final String EXTENSION_HTML = "html";
    public static final String EXTENSION_HTM = "htm";
    public static final String EXTENSION_XHTML = "xhtml";
    public static final String EXTENSION_XHTM = "xhtm";
    public static final String EXTENSION_XHT = "xht";
    public static final String EXTENSION_CSS = "css";
    public static final String EXTENSION_JS = "js";
    public static final String EXTENSION_XML = "xml";
    public static final String EXTENSION_TEXT = "text";
    public static final String EXTENSION_TXT = "txt";
    public static final String EXTENSION_JSON = "json";
    public static final String EXTENSION_PNG = "png";
    public static final String EXTENSION_JPEG = "jpeg";
    public static final String EXTENSION_JPG = "jpg";
    public static final String EXTENSION_GIF = "gif";
    public static final String EXTENSION_SVG = "svg";

    // TODO: add more extensions here...
    public static final String EXTENSION_WGT = "wgt";

    private static final Map<String, String> extensionToMimeTypes = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            put(EXTENSION_HTML, MIME_TYPE_HTML);
            put(EXTENSION_HTM, MIME_TYPE_HTML);
            put(EXTENSION_XHTML, MIME_TYPE_XHTML);
            put(EXTENSION_XHTM, MIME_TYPE_XHTML);
            put(EXTENSION_XHT, MIME_TYPE_XHTML);
            put(EXTENSION_CSS, MIME_TYPE_CSS);
            put(EXTENSION_JS, MIME_TYPE_JS);
            put(EXTENSION_XML, MIME_TYPE_XML);
            put(EXTENSION_TEXT, MIME_TYPE_TEXT);
            put(EXTENSION_TXT, MIME_TYPE_TEXT);
            put(EXTENSION_JSON, MIME_TYPE_JSON);
            put(EXTENSION_PNG, MIME_TYPE_PNG);
            put(EXTENSION_JPEG, MIME_TYPE_JPEG);
            put(EXTENSION_JPG, MIME_TYPE_JPEG);
            put(EXTENSION_GIF, MIME_TYPE_GIF);
            put(EXTENSION_SVG, MIME_TYPE_SVG);
            // TODO: add more mappings here...
        }
    };

    /**
     * extract an extension from the given path.
     * 
     * @param path a whole path string with an extension
     * @return the extension part if available, otherwise the given path itself
     */
    public static String extractExtension(String path) {
        int index = path.lastIndexOf('.');
        return (index > 0) ? path.substring(index + 1) : path;
    }

    /**
     * get mime type for the given extension.
     * 
     * @param extension an extension
     * @return the matching mime type, otherwise, 'application/octet-stream'
     */
    public static String getMimeTypeForExtension(String extension) {
        String mimeType = extensionToMimeTypes.get(extension);
        return (mimeType != null) ? mimeType : MIME_TYPE_BINARY;
    }

    /**
     * get mime type for the given path(or extension).
     * 
     * @param path a whole path string or an extension itself
     * @return the matching mime type, otheriwse 'application/octet-stream'
     */
    public static String getMimeType(String path) {
        return getMimeTypeForExtension(extractExtension(path));
    }

}
