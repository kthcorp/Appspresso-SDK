package com.appspresso.migration.app.v1_1.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.appspresso.migration.app.xml.AxProjectWritableElement;
import com.appspresso.migration.app.xml.WriteVisitor;
import com.appspresso.migration.w3c.widget.Feature;
import com.appspresso.migration.w3c.widget.Widget;
import com.appspresso.migration.w3c.widget.Widget.Author;
import com.appspresso.migration.w3c.widget.Widget.Content;
import com.appspresso.migration.w3c.widget.Widget.Icon;
import com.appspresso.migration.w3c.widget.Widget.License;
import com.appspresso.migration.w3c.widget.Widget.Name;

public class WriteVisitor_V1_1 implements WriteVisitor {

	private static final List<String> BUILT_IN_PLUGINS = new ArrayList<String>(){
		private static final long serialVersionUID = 1L;

		{
			add("deviceapis");
			add("deviceapis.accelerometer");
			add("deviceapis.camera");
			add("deviceapis.deviceinteraction");
			add("deviceapis.devicestatus");
			add("deviceapis.filesystem");
			add("deviceapis.geolocation");
			add("deviceapis.messaging");
			add("deviceapis.orientation");
			add("deviceapis.pim");
			add("deviceapis.pim.contact");
			add("ax.ext.admob");
			add("ax.ext.android");
			add("ax.ext.contact");
			add("ax.ext.ga");
			add("ax.ext.ios");
			add("ax.ext.media");
			add("ax.ext.net");
			add("ax.ext.ui");
			add("ax.ext.zip");
		}
	};

	@Override
	public void visit(AxProjectWritableElement e, Writer writer) {
		try {
			if (e instanceof AxProjectXML_V1_1) {
				visit((AxProjectXML_V1_1) e, writer);
			}
			else if (e instanceof Widget) {
				visit((Widget) e, writer);
			}
			else if (e instanceof WAC_V1_1) {
				visit((WAC_V1_1) e, writer);
			}
			else if (e instanceof Android_V1_1) {
				visit((Android_V1_1) e, writer);
			}
			else if (e instanceof IOS_V1_1) {
				visit((IOS_V1_1) e, writer);
			}
			else if (e instanceof Plugin_V1_1) {
				visit((Plugin_V1_1) e, writer);
			}
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	protected void visit(AxProjectXML_V1_1 e, Writer writer) throws IOException {
		writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n")
				.append("<project xmlns=\"http://www.appspresso.com/ns/app\" ")
				.append("version=\"" + e.getAxAppVersion() + "\"").append(">\n");

		e.getWidget().write(this, writer);
		e.getWAC().write(this, writer);
		e.getAndroid().write(this, writer);
		e.getIOS().write(this, writer);

		List<Plugin_V1_1> plugins = e.getPlugins();
		for (Plugin_V1_1 plugin : plugins) {
			plugin.write(this, writer);
		}

		writer.write("</project>\n");
	}

	private void visit(Widget e, Writer writer) throws IOException {
		String id = e.getId();
		String version = e.getVersion();
		writer.write("<widget");
		if (id != null) {
			writer.write(" id=\"" + id + "\"");
		}
		if (version != null) {
			writer.write(" version=\"" + version + "\"");
		}
		writer.write(">\n");

		Name name = e.getName();
		if (name != null) {
			writer.write(name.toString());
		}

		for (Icon icon : e.getIcons()) {
			writer.write(icon.toString());
		}

		Author author = e.getAuthor();
		if (author != null) {
			writer.write(author.toString());
		}

		Content content = e.getContent();
		if (content != null) {
			writer.write(content.toString());
		}

		License license = e.getLicense();
		if (license != null) {
			writer.write(license.toString());
		}

		String description = e.getDescription();
		if (description != null) {
			writer.write("<description>");
			writer.write(description);
			writer.write("</description>\n");
		}

		for (Feature feature : e.getFeatures()) {
			writer.write(feature.toString());
		}

		writer.write(e.getPreferences().toString());
		writer.write("</widget>\n");
	}

	protected void visit(WAC_V1_1 e, Writer writer) throws IOException {
		writer.write("<wac ");
		String version = e.getMinVersion();
		if (version != null) {
			writer.write("min-version=\"" + version + "\"");
		}
		writer.write(">");
		writer.write("</wac>\n");
	}

	private void visit(Android_V1_1 e, Writer writer) throws IOException {
		writer.write("<android");
		if (e.getId() != null) {
			writer.write(" id=\"" + e.getId() + "\"");
		}
		if (e.getVersion() != null) {
			writer.write(" version=\"" + e.getVersion() + "\"");
		}

		writer.write(">\n");

		for (Entry<String, String> pref : e.getPreferences().entrySet()) {
			if (!Android_V1_1.isObsoluteProperty(pref.getKey())) {
				writer.append("<preference name=\"" + pref.getKey() + "\" ")
						.append("value=\"" + pref.getValue() + "\" ").append("/>\n");
			}
		}
		writer.write("</android>\n");
	}

	private void visit(IOS_V1_1 e, Writer writer) throws IOException {
		writer.write("<iphone");
		if (e.getId() != null) {
			writer.write(" id=\"" + e.getId() + "\"");
		}
		if (e.getVersion() != null) {
			writer.write(" version=\"" + e.getVersion() + "\"");
		}
		writer.write(">\n");

		for (Entry<String, String> pref : e.getPreferences().entrySet()) {
			if (!IOS_V1_1.isObsoluteProperty(pref.getKey())) {
				writer.append("<preference name=\"" + pref.getKey() + "\" ")
						.append("value=\"" + pref.getValue() + "\" ").append("/>\n");
			}
		}
		writer.write("<sign_info>");
		if (e.getSignInfo() != null) {
			writer.write(e.getSignInfo());
		}
		writer.append("</sign_info>\n").append("</iphone>\n");

	}

	private void visit(Plugin_V1_1 e, Writer writer) throws IOException {
		String id = e.getId();
		String location = e.getLocation();
		String version = e.getVersion();

		writer.write("<plugin ");
		if (id != null) {
			writer.write("id=\"" + id + "\" ");

			if (BUILT_IN_PLUGINS.contains(id)) {
				writer.write("/>\n");
				return;
			}
		}
		if (version != null) {
			writer.write("version=\"" + version + "\" ");
		}
		if (location != null) {
			writer.write("location=\"" + location + "\" ");
		}

		writer.write("/>\n");
	}

}