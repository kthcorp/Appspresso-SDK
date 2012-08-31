package com.appspresso.cli.ant.utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class XPathTask extends Task {

	private String document;
	private String property;
	private String expression;

	@Override
	public void execute() throws BuildException {

		try {
			XPathExpression expr = XPathFactory.newInstance().newXPath().compile(this.expression);
			Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(this.document));
			NodeList list = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
			if (list.getLength() > 0) {
				getProject().setProperty(this.property, list.item(0).getTextContent());
			}
		}
		catch (Exception e) {
			throw new BuildException(e);
		}

	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
