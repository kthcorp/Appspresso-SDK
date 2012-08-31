package com.appspresso.migration.w3c.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Preferences implements Cloneable {
	List<String> itemOrder = new ArrayList<String>();

	private Map<String, Boolean> readonlyProp = new HashMap<String, Boolean>();
	private Map<String, String> storageArea = new HashMap<String, String>();

	public long length() {
		return itemOrder.size();
	}

	public String key(long index) {
		if (length() <= index) {
			return null;
		}

		return itemOrder.get(new Long(index).intValue());
	}

	public String getItem(String key) {
		return this.storageArea.get(key);
	}

	public void removeItem(String key) {
	}

	public void clear() {
	}

	public void setItem(String key, String value) {
		putInSharedPreferences(key, value, true);
	}

	// /////////////////////////////////////////////////////////////////////////////////
	public void setItem(String key, String value, boolean readonly) {
		putInSharedPreferences(key, value, readonly);
	}

	void putInSharedPreferences(String key, String value, boolean readonly) {
		storageArea.put(key, value);
		readonlyProp.put(key, readonly);
		itemOrder.add(key);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		long length = this.length();
		for (long i = 0; i < length; i++) {
			String key = this.key(i);
			String value = this.getItem(key);
			Boolean readonly = this.readonlyProp.get(key);

			builder.append("<preference").append(" name=\"").append(key).append("\"").append(" value=\"").append(value)
					.append("\"");

			if (readonly != null && readonly) {
				builder.append(" readonly=\"" + readonly + "\"");
			}
			builder.append(" />\n");
		}

		return builder.toString();
	}

	@Override
	protected Preferences clone() throws CloneNotSupportedException {
		Preferences clone = new Preferences();
		clone.storageArea.putAll(this.storageArea);
		clone.readonlyProp.putAll(this.readonlyProp);
		clone.itemOrder.addAll(this.itemOrder);

		return clone;
	}

}
