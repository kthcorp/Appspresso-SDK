package com.appspresso.core.runtime.server.kraken;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.appspresso.api.fs.AxFile;
import com.appspresso.api.fs.AxFileSystemManager;

class AxFileHandler extends AxRequestHandler {
    protected static final int BUF_SIZE = 1024 * 8;

    private final AxFileSystemManager fsManager;

    public AxFileHandler(AxFileSystemManager fsManager) {
        this.fsManager = fsManager;
    }

    @Override
    public void specificHandler(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        Uri uri = Uri.parse(request.getRequestLine().getUri());
        String path = uri.getPath();

        final AxFile file = getAxFile(path);
        if (file == null) {
            Kraken.sendErrorPage(response, HttpStatus.SC_NOT_FOUND, "404 Not Found");
            return;
        }

        response.setStatusCode(HttpStatus.SC_OK);
        EntityTemplate entity = new EntityTemplate(new ContentProducer() {
            @Override
            public void writeTo(OutputStream outStream) throws IOException {
                OutputStream out = new BufferedOutputStream(outStream);
                try {
                    file.open();
                    byte[] buf = null;
                    while (!file.isEof()) {
                        buf = file.read(BUF_SIZE);
                        out.write(buf, 0, buf.length);
                    }
                    out.flush();
                }
                finally {
                    if (file != null) {
                        try {
                            file.close();
                        }
                        catch (IOException ignored) {}
                    }
                }
            }
        });

        String contentType = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        if ((contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)) == null) {
            contentType = MimeTypeUtils.getMimeTypeForExtension(extension);
        }
        entity.setContentType(contentType);
        response.setEntity(entity);

    }

    private AxFile getAxFile(String uri) {
        String path = fsManager.fromUri(uri);
        return fsManager.getFile(path);
    }

}
