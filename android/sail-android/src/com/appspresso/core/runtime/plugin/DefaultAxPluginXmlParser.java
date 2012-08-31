package com.appspresso.core.runtime.plugin;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultAxPluginXmlParser implements AxPluginXmlParser {

    @Override
    public AxPluginXml parseAxPluginXml(InputStream in) throws SAXException, IOException,
            ParserConfigurationException, XPathExpressionException {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        final DefaultAxPluginXml axPluginXml = new DefaultAxPluginXml();
        parser.parse(in, new DefaultHandler() {

            @Override
            public void startElement(String uri, String localName, String qName,
                    Attributes attributes) throws SAXException {
                if ("axplugin".equals(localName)) {
                    axPluginXml.setId(attributes.getValue("id"));
                }
                else if ("module".equals(localName)) {
                    String platform = attributes.getValue("platform");
                    if ("android".equals(platform)) {
                        axPluginXml.setClassName(attributes.getValue("class"));
                    }
                }
                else if ("feature".equals(localName)) {
                    String featureUri = attributes.getValue("id");
                    axPluginXml.addFeature(featureUri);
                }
                else {
                    super.startElement(uri, localName, qName, attributes);
                }
            }
        });

        return axPluginXml;
    }

}
