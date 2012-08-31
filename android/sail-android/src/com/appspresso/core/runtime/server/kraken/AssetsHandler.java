package com.appspresso.core.runtime.server.kraken;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import android.content.res.AssetManager;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.appspresso.api.AxLog;
import com.appspresso.api.AxRuntimeContext;

/**
 * expose "assets" under the specified directory via http.
 * 
 */
class AssetsHandler extends AxRequestHandler {
    private static final Log L = AxLog.getLog(AssetsHandler.class);

    private static final int BUF_SIZE = 8 * 1024;
    private static final String LOCALE_DIR = "locales";

    private final AxRuntimeContext runtimeContext;

    // private final AssetManager assetManager;
    private final String basePath;

    private String[] userAgentLocales;

    public AssetsHandler(AxRuntimeContext runtimeContext, String basePath) {
        this.runtimeContext = runtimeContext;
        this.basePath = basePath;

        deriveUserAgentLocale();
    }

    // XXX: for kraken
    @Override
    public void specificHandler(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        final String path = Uri.parse(request.getRequestLine().getUri()).getPath();
        final InputStream in;
        try {
            in = findWidgetContent(path);
        }
        catch (FileNotFoundException e) {
            Kraken.sendErrorPage(response, HttpStatus.SC_NOT_FOUND, "404 Not Found");
            return;
        }

        try {
            response.setStatusCode(HttpStatus.SC_OK);
            EntityTemplate entity = new EntityTemplate(new ContentProducer() {
                @Override
                public void writeTo(OutputStream outStream) throws IOException {
                    OutputStream out = new BufferedOutputStream(outStream);
                    try {
                        // @@IOUtils.copy(in, outstream);
                        byte[] buf = new byte[BUF_SIZE];
                        int n;
                        while ((n = in.read(buf)) > 0) {
                            out.write(buf, 0, n);
                        }
                        out.flush();
                    }
                    finally {
                        // @@IOUtils.closeQuietly(in);
                        if (in != null) {
                            try {
                                in.close();
                            }
                            catch (IOException ignored) {}
                        }
                    }
                }
            });

            String contentType = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(path);
            if ((contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)) == null) {
                contentType = MimeTypeUtils.getMimeTypeForExtension(extension);
            }
            entity.setContentType(contentType);
            setCacheHeader(request, response);
            response.setEntity(entity);
        }
        catch (Exception e) {
            Kraken.sendErrorPage(response, HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "500 Internal Server Error: " + e.getMessage());
        }
    }

    // TODO path map?? (path <-> asset path)
    private InputStream findWidgetContent(String path) throws IOException {
        InputStream is = null;
        if (userAgentLocales != null) {
            for (String locale : userAgentLocales) {
                String filename =
                        new StringBuilder(basePath).append(File.separator).append(LOCALE_DIR)
                                .append(File.separator).append(locale).append(path).toString();
                try {
                    is = getAssetManager().open(filename);

                    if (L.isDebugEnabled()) {
                        L.debug("find widget content : " + filename);
                    }

                    break;
                }
                catch (IOException ignore) {
                    if (L.isTraceEnabled()) {
                        L.trace(ignore);
                    }
                }
            }
        }

        // default content
        if (is == null) {
            String filename = basePath + path;
            is = getAssetManager().open(filename);

            if (L.isDebugEnabled()) {
                L.debug("find default content");
            }
        }
        return new BufferedInputStream(is, BUF_SIZE);
    }

    // see also,
    // http://www.w3.org/TR/widgets/#rule-for-deriving-the-user-agent-locales
    private void deriveUserAgentLocale() {
        // Locale locale = Resources.getSystem().getConfiguration().locale;
        Locale locale = Locale.getDefault();

        List<String> userAgentLocales = new ArrayList<String>();
        String language = locale.getLanguage().toLowerCase();
        String country = locale.getCountry().toLowerCase();

        if (!TextUtils.isEmpty(language)) {
            if (!TextUtils.isEmpty(country)) {
                userAgentLocales.add(language + "-" + country);
            }
            userAgentLocales.add(language);
        }

        this.userAgentLocales = userAgentLocales.toArray(new String[userAgentLocales.size()]);

        if (L.isTraceEnabled()) {
            L.trace("User-agent locale : " + Arrays.toString(this.userAgentLocales));
        }
    }

    private AssetManager getAssetManager() {
        return this.runtimeContext.getActivity().getAssets();
    }

}
