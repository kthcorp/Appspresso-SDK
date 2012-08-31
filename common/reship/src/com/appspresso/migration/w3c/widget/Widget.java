/*
 * Appspresso
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

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.AxProjectWritableElement;
import com.appspresso.migration.app.xml.ParseVisitor;
import com.appspresso.migration.app.xml.WriteVisitor;

public class Widget implements AxProjectReadableElement, AxProjectWritableElement, Cloneable {

	private String id;
	private String defaultlocale;
	private String version;
	private long height;
	private long width;

	private Author author;
	private Content content;
	private Preferences preferences;

	private List<Feature> features;
	private List<Icon> icons;

	// locale
	private Name name;
	private License license;
	private String description;

	public Widget() {
		preferences = new Preferences();
		icons = new ArrayList<Icon>();
		features = new ArrayList<Feature>();
	}

	@Override
	public void read(ParseVisitor visitor, Node node) {
		visitor.visit(this, node);
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getWidth() {
		return this.width;
	}

	void setWidth(long width) {
		this.width = width;
	}

	public long getHeight() {
		return this.height;
	}

	void setHeight(long height) {
		this.height = height;
	}

	void setDefaultLocale(String defaultlocale) {
		this.defaultlocale = defaultlocale;
	}

	public String getDefaultLocale() {
		return this.defaultlocale;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void addPreference(String name, String value, boolean readonly) {
		this.preferences.setItem(name, value, readonly);
	}

	public Author getAuthor() {
		return this.author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public License getLicense() {
		return this.license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public Content getContent() {
		return this.content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public void addFeature(Feature feature) {
		this.features.add(feature);
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void addIcon(String src, String width, String height) {
		icons.add(new Icon(src, width, height));
	}

	public List<Icon> getIcons() {
		return icons;
	}

	public static class Name {
		private String shortName;
		private String lang;
		private String name;

		public Name(String name, String shortName) {
			this(shortName, name, null);
		}

		public Name(String name, String shortName, String lang) {
			this.shortName = shortName;
			this.lang = lang;
			this.name = name;
		}

		public String getShortName() {
			return shortName;
		}

		public String getLang() {
			return lang;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("<name");
			if (shortName != null) {
				builder.append(" short=\"").append(shortName).append("\"");
			}
			if (lang != null) {
				builder.append(" xml:lang=\"").append(lang).append("\"");
			}
			builder.append(">");
			if (name != null) {
				builder.append(name);
			}
			builder.append("</name>\n");
			return builder.toString();
		}

	}

	public static class Icon {
		private String src;
		private String width;
		private String height;

		public Icon(String src, String width, String height) {
			this.src = src;
			this.width = width;
			this.height = height;
		}

		public String getSrc() {
			return src;
		}

		public String getWidth() {
			return width;
		}

		public String getHeight() {
			return height;
		}

		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("<icon");
			if (src != null) {
				builder.append(" src=\"").append(src).append("\"");
			}
			if (width != null) {
				builder.append(" width=\"").append(width).append("\"");
			}
			if (height != null) {
				builder.append(" height=\"").append(height).append("\"");
			}
			builder.append(" />\n");
			return builder.toString();
		}
	}

	public static class Author {
		private String email;
		private String href;
		private String author;

		public Author(String author, String email, String href) {
			this.email = email;
			this.href = href;
			this.author = author;
		}

		public String getEmail() {
			return email;
		}

		public String getHref() {
			return href;
		}

		public String getAuthor() {
			return author;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("<author");
			if (email != null) {
				builder.append(" email=\"").append(email).append("\"");
			}
			if (href != null) {
				builder.append(" href=\"").append(href).append("\"");
			}
			builder.append(">");
			if (author != null) {
				builder.append(author);
			}
			builder.append("</author>\n");
			return builder.toString();
		}
	}

	public static class License {
		private String href;
		private String license;

		public License(String license, String href) {
			this.href = href;
			this.license = license;
		}

		public String getHref() {
			return href;
		}

		public String getLicense() {
			return license;
		}

		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("<license");
			if (href != null) {
				builder.append(" href=\"").append(href).append("\"");
			}
			builder.append(">");
			if (license != null) {
				builder.append(license);
			}
			builder.append("</license>\n");
			return builder.toString();
		}
	}

	public static class Content {
		private String src;
		private String type;
		private String encoding;
		private String lang;

		public Content(String src, String type, String encoding) {
			this(src, type, encoding, null);
		}

		public Content(String src, String type, String encoding, String lang) {
			this.src = src;
			this.type = type;
			this.encoding = encoding;
			this.lang = lang;
		}

		public String getSrc() {
			return src;
		}

		public String getType() {
			return type;
		}

		public String getEncoding() {
			return encoding;
		}

		public String getLang() {
			return lang;
		}

		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("<content");
			if (src != null) {
				builder.append(" src=\"").append(src).append("\"");
			}
			if (type != null) {
				builder.append(" type=\"").append(type).append("\"");
			}
			if (encoding != null) {
				builder.append(" encoding=\"").append(encoding).append("\"");
			}
			if (lang != null) {
				builder.append(" xml:lang=\"").append(lang).append("\"");
			}
			builder.append(" />\n");
			return builder.toString();
		}
	}

	@Override
	public Widget clone() throws CloneNotSupportedException {
		Widget clone = new Widget();

		clone.id = this.id;
		clone.defaultlocale = this.defaultlocale;
		clone.description = this.description;
		clone.height = this.height;
		clone.width = this.width;
		clone.version = this.version;

		long length = -1;

		// features
		length = this.features.size();
		for (int i = 0; i < length; i++) {
			clone.features.add(this.features.get(i).clone());
		}

		// icons
		length = this.icons.size();
		for (int i = 0; i < length; i++) {
			Icon icon = this.icons.get(i);
			clone.icons.add(new Icon(icon.src, icon.width, icon.height));
		}

		// preferences
		clone.preferences = this.preferences.clone();

		if (this.name != null) {
			clone.name = new Name(this.name.name, this.name.shortName);
		}
		if (this.author != null) {
			clone.author = new Author(this.author.author, this.author.email, this.author.href);
		}
		if (this.content != null) {
			clone.content = new Content(this.content.src, this.content.type, this.content.encoding);
		}
		if (this.license != null) {
			clone.license = new License(this.license.license, this.license.href);
		}

		return clone;
	}

	@Override
	public void write(WriteVisitor visitor, Writer writer) {
		visitor.visit(this, writer);
	}

}
