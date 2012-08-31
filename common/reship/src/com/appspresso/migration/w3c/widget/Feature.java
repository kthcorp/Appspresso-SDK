/*
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * This source is subject to Appspresso license terms.
 * Please see http://appspresso.com/ for more information.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
package com.appspresso.migration.w3c.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Feature {

	private String name;
	private String required;
	private Map<String, String> params;

	public Feature(final String name, final String required) {
		this.name = name;
		this.required = required;
		this.params = new HashMap<String, String>(0);
	}

	public String getName() {
		return this.name;
	}

	public String isRequired() {
		return this.required;
	}

	public Map<String, String> getParams() {
		return this.params;
	}

	public void putParam(final String name, final String value) {
		this.params.put(name, value);
	}

	@Override
	public Feature clone() throws CloneNotSupportedException {
		Feature clone = new Feature(this.name, this.required);
		Set<Entry<String, String>> entries = this.params.entrySet();
		for (Entry<String, String> e : entries) {
			clone.params.put(e.getKey(), e.getValue());
		}

		return clone;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("<feature");
		if (name != null) {
			builder.append(" name=\"").append(name).append("\"");
		}
		if (required != null) {
			builder.append(" required=\"").append(required).append("\"");
		}
		builder.append(">");

		for (Entry<String, String> param : params.entrySet()) {
			builder.append("<param").append(" name=\"").append(param.getKey()).append("\"").append(" value=\"")
					.append(param.getValue()).append("\" />\n");
		}
		builder.append("</feature>\n");
		return builder.toString();
	}

}
