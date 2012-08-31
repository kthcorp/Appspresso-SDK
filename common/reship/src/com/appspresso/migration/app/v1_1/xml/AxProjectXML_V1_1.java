package com.appspresso.migration.app.v1_1.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.appspresso.migration.app.AxProjectXML;
import com.appspresso.migration.app.xml.AxProjectMigratableElement;
import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.AxProjectWritableElement;
import com.appspresso.migration.app.xml.MigrateVisitor;
import com.appspresso.migration.app.xml.ParseVisitor;
import com.appspresso.migration.app.xml.WriteVisitor;
import com.appspresso.migration.w3c.widget.Widget;

public class AxProjectXML_V1_1 extends AxProjectXML implements AxProjectWritableElement, AxProjectMigratableElement {

	private final static String AX_APP_VERSION = "1.1";

	private Widget widget;
	private List<Plugin_V1_1> plugins = new ArrayList<Plugin_V1_1>();

	private Android_V1_1 android;
	private IOS_V1_1 ios;
	private WAC_V1_1 wac = new WAC_V1_1();

	@Override
	public String getAxAppVersion() {
		return AX_APP_VERSION;
	}

	@Override
	public void read(ParseVisitor visitor, Node node) {
		visitor.visit(this, node);
	}

	@Override
	public void migrate(MigrateVisitor visitor, AxProjectReadableElement from) {
		visitor.visit(from, this);
	}

	@Override
	public void write(WriteVisitor visitor, Writer writer) {
		visitor.visit(this, writer);
	}

	@Override
	public void write(String path) {
		File file = new File(path);
		WriteVisitor visitor = new WriteVisitor_V1_1();
		Writer writer = null;
		try {
			writer = new BufferedWriter(new PrintWriter(file));
			write(visitor, writer);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
				}
				catch (IOException ignored) {
				}
			}
		}
	}

	public Widget getWidget() {
		return this.widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	public void addPlugin(Plugin_V1_1 plugin) {
		plugins.add(plugin);
	}

	public void setAndroid(Android_V1_1 android) {
		this.android = android;
	}

	public Android_V1_1 getAndroid() {
		return this.android;
	}

	public void setIOS(IOS_V1_1 ios) {
		this.ios = ios;
	}

	public IOS_V1_1 getIOS() {
		return this.ios;
	}

	public List<Plugin_V1_1> getPlugins() {
		return this.plugins;
	}

	public WAC_V1_1 getWAC() {
		return this.wac;
	}
}
