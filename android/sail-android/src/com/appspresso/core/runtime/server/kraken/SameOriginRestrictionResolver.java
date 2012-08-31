package com.appspresso.core.runtime.server.kraken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.webkit.WebView;

import com.appspresso.api.AxLog;

/**
 * 
 */
class SameOriginRestrictionResolver {
    private static final Log L = AxLog.getLog(SameOriginRestrictionResolver.class);

    private SameOriginRestrictionResolver() {}

    public static void resolve(Activity activity, WebView webView, final int from, final int to) {
        resolveWebStorageData(activity, webView, from, to);

        updateCacheUrl(activity, webView, to);
    }

    private static void updateCacheUrl(Activity activity, WebView webView, int newPort) {
        Cursor cursor = null;
        SQLiteDatabase cacheDB = null;
        // cache database file...
        File cacheDBFile = new File(webView.getSettings().getDatabasePath(), "webviewCache.db");
        try {
            String cacheDBPath = cacheDBFile.getAbsolutePath();
            cacheDB = SQLiteDatabase.openDatabase(cacheDBPath, null, SQLiteDatabase.OPEN_READWRITE);
            cacheDB.beginTransaction();
            cursor = cacheDB.query("cache", new String[] {"url"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String oldUrl = cursor.getString(0);
                int port = Uri.parse(oldUrl).getPort();
                String newUrl = oldUrl.replaceFirst(String.valueOf(port), String.valueOf(newPort));

                ContentValues values = new ContentValues();
                values.put("url", newUrl);
                cacheDB.update("cache", values, "url = ?", new String[] {oldUrl});
            }

            cacheDB.setTransactionSuccessful();
        }
        catch (SQLiteException ignored) {
            // SQLiteException(database is locked) handling...
            if (L.isDebugEnabled()) {
                L.debug(ignored);
            }
        }
        catch (Exception ignored) {}
        finally {
            if (cursor != null) {
                cursor.close();
            }

            if (cacheDB != null) {
                cacheDB.endTransaction();
                cacheDB.close();
            }
        }
    }

    private static final String PREFIX = "http_localhost_";
    private static final String MetaDatabase = "Databases.db";

    protected static void resolveWebStorageData(Activity activity, WebView webView, final int from,
            final int to) {

        final String oldOrigin = PREFIX + from;
        final String newOrigin = PREFIX + to;

        String dbPath = webView.getSettings().getDatabasePath();
        File dbPathFile = new File(dbPath);

        // meta file scanning
        // localStorage - http_localhost_50010.localstorage (file)
        // sql db - http_localhost_50010 (directory)
        List<File> origins = new ArrayList<File>();
        Stack<File> stack = new Stack<File>();
        stack.push(dbPathFile);
        while (!stack.empty()) {
            File c = stack.pop();
            if (c.isDirectory()) {
                File[] files = c.listFiles();
                if (files != null) {
                    for (File f : files) {
                        stack.add(f);
                    }
                }
            }

            String name = c.getName();
            if (name.startsWith(oldOrigin)) {
                origins.add(c);
            }
        }

        renameLocalStorageFile(origins, oldOrigin, newOrigin);
        updateWebSqlDatabase(dbPath, newOrigin);
    }

    private static void renameLocalStorageFile(List<File> origins, String oldOrigin,
            String newOrigin) {
        // localstorage
        if (origins != null && origins.size() > 0) {
            int size = origins.size();
            for (int i = 0; i < size; i++) {
                File origin = origins.get(i);
                // String newFileName = origin.getName().replaceFirst(PREFIX +
                // "[0-9]*", newOriginPrefix);
                String newFileName = origin.getName().replaceFirst(oldOrigin, newOrigin);
                origin.renameTo(new File(origin.getParent() + File.separator + newFileName));
            }
        }
    }

    private static void updateWebSqlDatabase(final String dbPath, final String newOrigin) {
        // update meta database
        SQLiteDatabase sqldb = null;
        try {
            File metaDB = new File(dbPath, MetaDatabase);
            if (metaDB.exists()) {
                final String where = " origin LIKE ?";
                final String[] whereArgs = new String[] {PREFIX + "%"};

                sqldb = SQLiteDatabase.openOrCreateDatabase(metaDB, null);
                ContentValues value = new ContentValues();
                value.put("origin", newOrigin);
                sqldb.update("Origins", value, where, whereArgs);
                sqldb.update("Databases", value, where, whereArgs);
            }
        }
        catch (Exception ignore) {}
        finally {
            if (sqldb != null) sqldb.close();
        }
    }

    public static int restoreWebServerPort(Activity activity, String serverName) {
        SharedPreferences pref = activity.getPreferences(Context.MODE_PRIVATE);
        return pref.getInt(serverName, -1);
    }

    public static void saveWebServerPort(Activity activity, String serverName, int port) {
        SharedPreferences pref = activity.getPreferences(Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putInt(serverName, port);
        editor.commit();
    }

}
