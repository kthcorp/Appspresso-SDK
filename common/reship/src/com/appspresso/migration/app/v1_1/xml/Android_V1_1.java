package com.appspresso.migration.app.v1_1.xml;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.appspresso.migration.app.v1.Android_V1;
import com.appspresso.migration.app.xml.AxProjectMigratableElement;
import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.AxProjectWritableElement;
import com.appspresso.migration.app.xml.MigrateVisitor;
import com.appspresso.migration.app.xml.WriteVisitor;

public class Android_V1_1 extends Android_V1 implements AxProjectReadableElement, AxProjectMigratableElement,
		AxProjectWritableElement {

	protected String version;

	public Android_V1_1() {
		super();

		this.preferences.put("activity", "Activity");
		this.preferences.put("versionCode", "1");
		this.preferences.put("maxSdkVersion", "");
		this.preferences.put("installLocation", "internalOnly");
		this.preferences.put("activity.theme", "0");
		this.preferences.put("orientation", "0");
		this.preferences.put("targetSdkVersion", "7");
		this.preferences.put("minSdkVersion", "7");
		this.preferences.put("webview.cache.enable", "true");
		this.preferences.put("webview.cache.clearonfinish", "false");
		this.preferences.put("defaultZoom", "MEDIUM");
		this.preferences.put("zoom.enable", "false");
		this.preferences.put("zoom.control", "false");
		this.preferences.put("splash.enable", "true");
		this.preferences.put("splash.orientation", "0");
		this.preferences.put("splash.duration.min", "0");
		this.preferences.put("splash.duration.max", "0");
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public void migrate(MigrateVisitor visitor, AxProjectReadableElement from) {
		visitor.visit(from, this);
	}

	@Override
	public void write(WriteVisitor visitor, Writer writer) {
		visitor.visit(this, writer);
	}

	private final static List<String> obsoluteProperty = new ArrayList<String>(){
		private static final long serialVersionUID = 1L;
		{
			add("splash");
			add("icon_high");
			add("icon_middle");
			add("icon_low");
		}
	};

	static boolean isObsoluteProperty(String key) {
		if (obsoluteProperty.contains(key)) {
			return true;
		}
		return false;
	}

}
