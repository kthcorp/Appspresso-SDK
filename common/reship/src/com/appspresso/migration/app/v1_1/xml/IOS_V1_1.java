package com.appspresso.migration.app.v1_1.xml;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.appspresso.migration.app.v1.IOS_V1;
import com.appspresso.migration.app.xml.AxProjectMigratableElement;
import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.AxProjectWritableElement;
import com.appspresso.migration.app.xml.MigrateVisitor;
import com.appspresso.migration.app.xml.WriteVisitor;

public class IOS_V1_1 extends IOS_V1 implements AxProjectReadableElement, AxProjectWritableElement,
		AxProjectMigratableElement {

	protected String version;

	public IOS_V1_1() {
		super();

		this.preferences.put("buildVersionNumber", "1");
		this.preferences.put("targetDevice", "1");
		this.preferences.put("webview.cache.enable", "true");
		this.preferences.put("ScalesPageToFit", "false");
		this.preferences.put("application.orientation.supported", "1,2,3,4");
		this.preferences.put("exitOnSuspend", "false");
		this.preferences.put("initOrientation", "1");
		this.preferences.put("uiPrerenderedIcon", "false");
		this.preferences.put("uiStatusBarHidden", "false");
		this.preferences.put("uiStatusBarStyle", "UIStatusBarStyleDefault");
		this.preferences.put("splash.duration.min", "0");
		this.preferences.put("splash.duration.max", "0");
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void migrate(MigrateVisitor visitor, AxProjectReadableElement from) {
		visitor.visit(from, this);
	}

	@Override
	public void write(WriteVisitor visitor, Writer writer) {
		visitor.visit(this, writer);
	}

	private final static List<String> obsoluteProperty = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("splashImg");
			add("icon1");
			add("icon2");
			add("icon3");
		}
	};

	static boolean isObsoluteProperty(String key) {
		if (obsoluteProperty.contains(key)) {
			return true;
		}
		return false;
	}

}
