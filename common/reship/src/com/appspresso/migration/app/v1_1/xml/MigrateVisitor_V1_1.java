package com.appspresso.migration.app.v1_1.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.appspresso.migration.app.v1.Android_V1;
import com.appspresso.migration.app.v1.AxProjectXML_V1;
import com.appspresso.migration.app.v1.IOS_V1;
import com.appspresso.migration.app.v1.Plugin_V1;
import com.appspresso.migration.app.xml.AxProjectMigratableElement;
import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.MigrateVisitor;
import com.appspresso.migration.w3c.widget.Widget;

public class MigrateVisitor_V1_1 implements MigrateVisitor {

	@Override
	public void visit(AxProjectReadableElement from, AxProjectMigratableElement to) {
		if (from instanceof AxProjectXML_V1) {
			visit((AxProjectXML_V1) from, (AxProjectXML_V1_1) to);
		}
		else if (from instanceof Widget) {
			visit((Widget) from, (Widget) to);
		}
		else if (from instanceof Android_V1) {
			visit((Android_V1) from, (Android_V1_1) to);
		}
		else if (from instanceof IOS_V1) {
			visit((IOS_V1) from, (IOS_V1_1) to);
		}
		else if (from instanceof Plugin_V1) {
			visit((Plugin_V1) from, (Plugin_V1_1) to);
		}
	}

	protected void visit(AxProjectXML_V1 from, AxProjectXML_V1_1 to) {
		// project
		Widget widget = null;
		try {
			widget = from.getWidget().clone();
			widget.setId(from.getId());
			widget.setVersion(from.getVersion());
			to.setWidget(widget);
		}
		catch (CloneNotSupportedException e) {
		}

		// android
		Android_V1_1 android = new Android_V1_1();
		android.migrate(this, from.getAndroid());
		android.setVersion(widget.getVersion());
		to.setAndroid(android);

		// ios
		IOS_V1_1 ios = new IOS_V1_1();
		ios.migrate(this, from.getIOS());
		ios.setVersion(widget.getVersion());
		to.setIOS(ios);

		// plugin
		List<Plugin_V1> plugins = from.getPlugins();
		for (Plugin_V1 plugin : plugins) {
			Plugin_V1_1 v1_1 = new Plugin_V1_1();
			v1_1.migrate(this, plugin);
			to.addPlugin(v1_1);
		}
	}

	protected void visit(Widget from, Widget to) {
	}

	protected void visit(Android_V1 from, Android_V1_1 to) {
		to.setId(from.getId());

		Map<String, String> preferences = from.getPreferences();
		for (Entry<String, String> pref : preferences.entrySet()) {
			String name = pref.getKey();
			String value = pref.getValue();

			String newName = name;
			String newValue = value;
			if ("orientation".equals(name)) {
				if ("unspecified".equals(value)) {
					newValue = "0";
				}
				else if ("portrait".equals(value)) {
					newValue = "1";
				}
				else if ("landscape".equals(value)) {
					newValue = "2";
				}
			}
			else if ("fullscreen".equals(name) && Boolean.parseBoolean(value)) {
				newName = "activity.theme";
				newValue = "2";
			}

			to.setPreference(newName, newValue);
		}
	}

	protected void visit(IOS_V1 from, IOS_V1_1 to) {
		to.setId(from.getId());

		List<String> supportedOrientation = new ArrayList<String>(4);
		Map<String, String> preferences = from.getPreferences();
		for (Entry<String, String> pref : preferences.entrySet()) {
			String name = pref.getKey();
			String value = pref.getValue();

			String newName = name;
			String newValue = value;
			if ("initOrientation".equals(name)) {
				if ("UIInterfaceOrientationPortrait".equals(value)) {
					newValue = "1";
				}
				else if ("UIInterfaceOrientationLandscapeRight".equals(value)) {
					newValue = "2";
				}
				else if ("UIInterfaceOrientationPortraitUpsideDown".equals(value)) {
					newValue = "3";
				}
				else if ("UIInterfaceOrientationLandscapeLeft".equals(value)) {
					newValue = "4";
				}
			}
			else if ("UIInterfaceOrientationPortrait".equals(name) && Boolean.parseBoolean(value)) {
				supportedOrientation.add("1");
				continue;
			}
			else if ("UIInterfaceOrientationLandscapeRight".equals(name) && Boolean.parseBoolean(value)) {
				supportedOrientation.add("2");
				continue;
			}
			else if ("UIInterfaceOrientationPortraitUpsideDown".equals(name) && Boolean.parseBoolean(value)) {
				supportedOrientation.add("3");
				continue;
			}
			else if ("UIInterfaceOrientationLandscapeLeft".equals(name) && Boolean.parseBoolean(value)) {
				supportedOrientation.add("4");
				continue;
			}
			to.setPreference(newName, newValue);
		}

		StringBuilder b = new StringBuilder();
		Iterator<String> it = supportedOrientation.iterator();
		while (it.hasNext()) {
			String o = it.next();
			b.append(o);
			if (it.hasNext()) {
				b.append(",");
			}
		}
		if (b.length() > 0) {
			to.setPreference("application.orientation.suuported", b.toString());
		}
	}

	protected void visit(Plugin_V1 from, Plugin_V1_1 to) {
		to.setId(from.getId());
		to.setLocation(from.getLocation());
		to.setVersion(from.getVersion());
	}
}
