package com.appspresso.w3.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.DOMException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.appspresso.w3.Storage;
import com.appspresso.w3.Widget;

class Preferences implements Storage {

    private final static String STORAGE_AREA_NAME = "ax.w3.widget.preference";
    private final static String KEY_READONLY_NAME = "ax.w3.widget.preference.readonly";

    private final static String KEY_CURRENT_VERSION = "ax.app.version";

    List<String> itemOrder = new ArrayList<String>();

    private boolean fromPreferences = false;

    private SharedPreferences appVersion;
    private SharedPreferences readonlyProp;

    private SharedPreferences storageArea;
    private Editor storageAreaEditor;

    Preferences(Activity activity, Widget widget) {
        this.appVersion = activity.getSharedPreferences(KEY_CURRENT_VERSION, Context.MODE_PRIVATE);
        this.fromPreferences = appVersion.contains(KEY_CURRENT_VERSION);

        this.readonlyProp = activity.getSharedPreferences(KEY_READONLY_NAME, Context.MODE_PRIVATE);

        this.storageArea = activity.getSharedPreferences(STORAGE_AREA_NAME, Context.MODE_PRIVATE);
        this.storageAreaEditor = this.storageArea.edit();

        restoreFromStorageArea(widget);
    }

    private void restoreFromStorageArea(Widget widget) {
        if (this.fromPreferences) {
            Iterator<String> it = this.storageArea.getAll().keySet().iterator();
            while (it.hasNext()) {
                this.itemOrder.add(it.next());
            }
        }
    }

    @Override
    public long length() {
        return itemOrder.size();
    }

    @Override
    public String key(long index) {
        if (length() <= index) { return null; }

        return itemOrder.get(new Long(index).intValue());
    }

    @Override
    public String getItem(String key) {
        return this.storageArea.getString(key, null);
    }

    @Override
    public void setItem(String key, String value) {
        if (this.readonlyProp.contains(key)) { throw new DOMException(
                DOMException.NO_MODIFICATION_ALLOWED_ERR, key + " property is readonly "); }

        putInSharedPreferences(key, value);
    }

    @Override
    public void removeItem(String key) {
        if (this.readonlyProp.contains(key)) { throw new DOMException(
                DOMException.NO_MODIFICATION_ALLOWED_ERR, key + " property is readonly "); }

        removeFromSharedPreferences(key);
    }

    @Override
    public void clear() {
        Collection<String> collection = this.storageArea.getAll().keySet();
        String[] keys = null;
        keys = collection.toArray(new String[0]);

        for (int i = 0; i < keys.length; i++) {
            if (!this.readonlyProp.contains(keys[i])) {
                this.storageAreaEditor.remove(keys[i]);
                itemOrder.remove(keys[i]);
            }
        }

        this.storageAreaEditor.commit();
    }

    // Internal Call
    void setItem(String key, String value, boolean readonly) {
        if (this.fromPreferences) { return; }

        putInSharedPreferences(key, value);

        if (readonly) {
            Editor editor = this.readonlyProp.edit();
            editor.putString(key, "");
            editor.commit();
        }
    }

    void writeApplicationVersion(String version) {
        Editor editor = this.appVersion.edit();
        editor.putString(KEY_CURRENT_VERSION, version);
        editor.commit();
    }

    // /////////////////////////////////////////////////////////////////////////////////
    void putInSharedPreferences(String key, String value) {
        this.storageAreaEditor.putString(key, value);
        this.storageAreaEditor.commit();

        if (!itemOrder.contains(key)) {
            itemOrder.add(key);
        }
    }

    void removeFromSharedPreferences(String key) {
        this.storageAreaEditor.remove(key);
        this.storageAreaEditor.commit();

        itemOrder.remove(key);
    }
}
