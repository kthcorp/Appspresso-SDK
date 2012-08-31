package com.appspresso.cli.ant.wac.app;

import java.io.File;
import java.io.IOException;

import javax.naming.directory.InvalidAttributesException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class RegenConfig extends Task{

	private static final String WAC_NS_URI = "http://wacapps.net/ns/widgets";

	private String src;
	private String dest;

	private String minVersion;

	@Override
	public void execute() throws BuildException {
		try {
			if (this.src == null) {
				throw new InvalidAttributesException("src attribute not set");
			}

			if (this.dest == null) {
				throw new InvalidAttributesException("dest attribute not set");
			}

			Document document = read();
			write(document);
		}
		catch (Exception e) {
			throw new BuildException(e);
		}
	}

	private void write(Document document) throws TransformerFactoryConfigurationError, TransformerException {
		Node widget = document.getElementsByTagName("widget").item(0);

		//Attr wacNSAttr = document.createAttribute("xmlns:wac");
		//wacNSAttr.setTextContent(WAC_NS_URI);
		//widget.getAttributes().setNamedItem(wacNSAttr);

		if (minVersion != null) {
			Attr minVersionAttr = document.createAttributeNS(WAC_NS_URI, "wac:min-version");
			minVersionAttr.setTextContent(this.minVersion);
			widget.getAttributes().setNamedItem(minVersionAttr);
		}

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(this.dest));
		transformer.transform(source, result);
	}

	private Document read() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(this.src));
		return document;
	}

	public void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public static void main(String[] args) {
		try {
			RegenConfig c = new RegenConfig();
			c.src = "/Users/blueNmad/Appspresso/anchor/test/Hello/output/config.xml";
			c.read();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
