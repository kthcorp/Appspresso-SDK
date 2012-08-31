package com.appspresso.migration.app.v1;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.appspresso.migration.app.AxProjectXML;
import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.ParseVisitor;
import com.appspresso.migration.w3c.widget.Feature;
import com.appspresso.migration.w3c.widget.Widget;

public class ParseVisitor_V1 implements ParseVisitor {

	@Override
	public void visit(AxProjectReadableElement e, Node node) {
		if (e instanceof AxProjectXML_V1) {
			visit((AxProjectXML) e, node);
		}
		else if (e instanceof Widget) {
			visit((Widget) e, node);
		}
		else if (e instanceof Android_V1) {
			visit((Android_V1) e, node);
		}
		else if (e instanceof IOS_V1) {
			visit((IOS_V1) e, node);
		}
	}

	protected void visit(AxProjectXML project, Node node) {
		AxProjectXML_V1 p = (AxProjectXML_V1) project;

		NamedNodeMap attr = node.getAttributes();
		p.setId(getTextContentOfNamedItem(attr, "id"));
		p.setVersion(getTextContentOfNamedItem(attr, "version"));
		p.setTitle(getTextContentOfNamedItem(attr, "title"));

		NodeList children = node.getChildNodes();
		int length = children.getLength();
		for (int i = 0; i < length; i++) {
			Node child = children.item(i);
			String nodeName = child.getNodeName();

			if ("widget".equals(nodeName)) {
				Widget widget = new Widget();
				widget.read(this, child);
				p.setWidgetElement(widget);
			}
			else if ("android".equals(nodeName)) {
				Android_V1 android = new Android_V1();
				android.read(this, child);
				p.setAndroidElement(android);
			}
			else if ("iphone".equals(nodeName)) {
				IOS_V1 ios = new IOS_V1();
				ios.read(this, child);
				p.setIOSElement(ios);
			}
			else if ("plugin".equals(nodeName)) {
				NamedNodeMap pluginAttr = child.getAttributes();
				String id = getTextContentOfNamedItem(pluginAttr, "id");
				String version = getTextContentOfNamedItem(pluginAttr, "version");
				String location = getTextContentOfNamedItem(pluginAttr, "location");
				p.addPlugin(new Plugin_V1(id, version, location));
			}
		}

		String id = p.getId();
		if (p.getAndroid().getId() == null) {
			p.getAndroid().setId(id);
		}

		if (p.getIOS().getId() == null) {
			p.getIOS().setId(id);
		}
	}

	protected void visit(Widget widget, Node node) {
		widget.setId(getTextContentOfNamedItem(node.getAttributes(), "id"));

		NodeList children = node.getChildNodes();
		int length = children.getLength();
		for (int i = 0; i < length; i++) {
			Node child = children.item(i);
			String nodeName = child.getNodeName();

			if ("content".equals(nodeName)) {
				NamedNodeMap attr = child.getAttributes();
				String src = getTextContentOfNamedItem(attr, "src");
				String encoding = getTextContentOfNamedItem(attr, "encoding");
				String type = getTextContentOfNamedItem(attr, "type");

				Widget.Content content = new Widget.Content(src, type, encoding);
				widget.setContent(content);
			}
			else if ("icon".equals(nodeName)) {
				widget.addIcon(getTextContentOfNamedItem(child.getAttributes(), "src"), null, null);
			}
			else if ("feature".equals(nodeName)) {
				String name = getTextContentOfNamedItem(child.getAttributes(), "name");
				String required = getTextContentOfNamedItem(child.getAttributes(), "required");

				Feature feature = new Feature(name, required);
				widget.addFeature(feature);
			}
			else if ("author".equals(nodeName)) {
				String content = child.getTextContent();

				NamedNodeMap attr = child.getAttributes();
				String href = getTextContentOfNamedItem(attr, "href");
				String email = getTextContentOfNamedItem(attr, "email");

				Widget.Author author = new Widget.Author(content, email, href);
				widget.setAuthor(author);
			}
			else if ("description".equals(nodeName)) {
				String description = child.getTextContent();
				widget.setDescription(description);
			}
			else if ("license".equals(nodeName)) {
				String href = getTextContentOfNamedItem(child.getAttributes(), "href");
				String content = child.getTextContent();

				Widget.License license = new Widget.License(content, href);
				widget.setLicense(license);
			}
			else if ("preference".equals(nodeName)) {
				NamedNodeMap attr = child.getAttributes();
				String name = getTextContentOfNamedItem(attr, "name");
				String value = getTextContentOfNamedItem(attr, "value");
				Boolean readonly = Boolean.parseBoolean(getTextContentOfNamedItem(attr, "readonly"));

				widget.addPreference(name, value, readonly);
			}
		}
	}

	protected void visit(Android_V1 android, Node node) {
		android.setId(getTextContentOfNamedItem(node.getAttributes(), "id"));

		NodeList children = node.getChildNodes();
		int length = children.getLength();
		for (int i = 0; i < length; i++) {
			Node child = children.item(i);
			String nodeName = child.getNodeName();

			if ("preference".equals(nodeName)) {
				NamedNodeMap attr = child.getAttributes();
				String name = getTextContentOfNamedItem(attr, "name");
				String value = getTextContentOfNamedItem(attr, "value");

				android.setPreference(name, value);
			}
		}
	}

	protected void visit(IOS_V1 ios, Node node) {
		ios.setId(getTextContentOfNamedItem(node.getAttributes(), "id"));

		NodeList children = node.getChildNodes();
		int length = children.getLength();
		for (int i = 0; i < length; i++) {
			Node child = children.item(i);
			String nodeName = child.getNodeName();

			if ("preference".equals(nodeName)) {
				NamedNodeMap attr = child.getAttributes();
				String name = getTextContentOfNamedItem(attr, "name");
				String value = getTextContentOfNamedItem(attr, "value");

				ios.setPreference(name, value);
			}
			else if ("sign_info".equals(nodeName)) {
				String signInfo = child.getTextContent();
				ios.setSignInfo(signInfo);
			}
		}
	}

	private String getTextContentOfNamedItem(NamedNodeMap attr, String name) {
		Node item = attr.getNamedItem(name);
		if (item != null) {
			return item.getTextContent();
		}
		return null;
	}

}