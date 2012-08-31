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
import org.apache.http.protocol.HttpRequestHandler;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;

import com.appspresso.api.AxRuntimeContext;

class ContactPhotoURIHandler implements HttpRequestHandler {

    private AxRuntimeContext runtimeContext;

    public ContactPhotoURIHandler(AxRuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        Uri uri = Uri.parse(request.getRequestLine().getUri().substring(1));
        String raw_id = uri.getLastPathSegment();

        ContentResolver cr = this.runtimeContext.getActivity().getContentResolver();

        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(raw_id));
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);

        final String[] projection = new String[] {Photo.PHOTO};
        Cursor cursor = cr.query(photoUri, projection, null, null, null);

        if (null != cursor && cursor.moveToNext()) {
            final byte[] data = cursor.getBlob(0);

            response.setStatusCode(HttpStatus.SC_OK);
            response.setEntity(new EntityTemplate(new ContentProducer() {
                @Override
                public void writeTo(OutputStream outStream) throws IOException {
                    OutputStream out = new BufferedOutputStream(outStream);
                    try {
                        out.write(data);
                        out.flush();
                    }
                    finally {}
                }
            }));

            cursor.close();
            // String contentType = MimeTypeUtils.getMimeType(uri);
            // entity.setContentType(contentType);
        }
    }

}
