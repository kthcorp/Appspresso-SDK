package com.appspresso.migration.app.v1;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.appspresso.migration.app.AxProjectXML;
import com.appspresso.migration.app.xml.ParseVisitor;
import com.appspresso.migration.w3c.widget.Widget;

public class AxProjectXML_V1 extends AxProjectXML {

	private String id;
	private String title;
	private String version;

	private Widget widget;
	private Android_V1 android;
	private IOS_V1 ios;
	private List<Plugin_V1> plugins = new ArrayList<Plugin_V1>();

	@Override
	public String getAxAppVersion() {
		return "1.01";
	}

	@Override
	public void read(ParseVisitor visitor, Node node) {
		visitor.visit(this, node);
	}

	@Override
	public void write(String path) {
	}

	// ///////////////////////////////////////////////////////////////////
	// get / set
	void setId(String id) {
		this.id = id;
	}

	void setVersion(String version) {
		this.version = version;
	}

	void setTitle(String title) {
		this.title = title;
	}

	void setWidgetElement(Widget widget) {
		this.widget = widget;
	}

	void setAndroidElement(Android_V1 android) {
		this.android = android;
	}

	void setIOSElement(IOS_V1 ios) {
		this.ios = ios;
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public String getTitle() {
		return title;
	}

	public Widget getWidget() {
		return widget;
	}

	public Android_V1 getAndroid() {
		return android;
	}

	public IOS_V1 getIOS() {
		return ios;
	}

	public List<Plugin_V1> getPlugins() {
		return this.plugins;
	}

	public void addPlugin(Plugin_V1 plugin) {
		this.plugins.add(plugin);
	}

}
