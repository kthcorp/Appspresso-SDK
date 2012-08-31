package com.appspresso.cli.ant.android.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.Project;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AndroidBuildUtils {
	private static final String FORMAT_ANDROID_PROPERTY_EXPR = "axplugin/module[@platform='android']/property[@name='%s']";

	private final static Map<String, XPathExpression> xpathExpressions = new HashMap<String, XPathExpression>(1);

	public static int getPluginLength(Project project) {
		String length = project.getProperty("plugin.length");
		return length != null ? Integer.parseInt(length) : 0;
	}

	public static String getPluginPath(Project project, int index) {
		return project.getProperty("plugin." + index + ".path");
	}

	public static String getPluginID(Project project, int index) {
		return project.getProperty("plugin." + index + ".id");
	}

	public static String[] getPropertyValueWithName(Project project, int index, String propertyName) throws Exception {
		List<String> values = new ArrayList<String>();
		File axplugin = new File(getPluginPath(project, index), "axplugin.xml");
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(axplugin);
		NodeList nodes = (NodeList) getExpr(propertyName).evaluate(document, XPathConstants.NODESET);

		int length = nodes.getLength();
		for (int i = 0; i < length; i++) {
			Node node = nodes.item(i);
			values.add(node.getAttributes().getNamedItem("value").getTextContent());
		}

		return values.toArray(new String[] {});
	}

	private static XPathExpression getExpr(String propertyName) throws XPathExpressionException {
		XPathExpression xpathExpr = null;
		if ((xpathExpr = xpathExpressions.get(propertyName)) == null) {
			String expression = String.format(FORMAT_ANDROID_PROPERTY_EXPR, propertyName);
			xpathExpr = XPathFactory.newInstance().newXPath().compile(expression);
		}

		return xpathExpr;
	}
}
