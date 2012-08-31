package com.appspresso.cli.ant.ios.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AppNameGen extends Task {

	@Override
	public void execute() throws BuildException {
		try {
			File appNameXml = new File(getResourcesPath(), "appName.xml");
			process(appNameXml);
		}
		catch (Exception e) {
			throw new BuildException(e);
		}
	}

	private String getResourcesPath() {
		return getProject().getProperty("app.platform.dir") + "/resources";
	}

	private void process(File file) throws SAXException, IOException, ParserConfigurationException,
			XPathExpressionException {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile("/resources/app-name");
		Object result = expr.evaluate(document, XPathConstants.NODESET);
		if (result != null) {
			NodeList nodes = (NodeList) result;
			int length = nodes.getLength();
			for (int i = 0; i < length; i++) {
				Node node = nodes.item(i);

				Node langNode = node.getAttributes().getNamedItem("lang");
				String lang = langNode == null ? "" : langNode.getTextContent();
				String content = node.getTextContent();

				makeAxStringsXml(content, lang);
			}
		}
	}

	private void makeAxStringsXml(String content, String lang) throws IOException {
		String bundle = null;
		if (lang == null || "".equals(lang.trim())) {
			return;
		}
		else {
			bundle = lang + ".lproj";
		}

		String application = getProject().getProperty("app.runtime.project.dir") + "/"
				+ getProject().getProperty("app.runtime.project.name");
		File parent = new File(application, bundle);
		if (!parent.exists() && !parent.mkdirs()) {
			throw new IOException("can't create directory : " + parent.getAbsolutePath());
		}

		File axStrings = new File(parent, "InfoPlist.strings");

		Writer writer = new BufferedWriter(new FileWriter(axStrings));
		writer.append("\"CFBundleDisplayName\"=\"")
			.append(content)
			.append("\";");
		writer.close();
	}

}
