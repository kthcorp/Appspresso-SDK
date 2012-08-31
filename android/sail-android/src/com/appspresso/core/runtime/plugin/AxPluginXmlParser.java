package com.appspresso.core.runtime.plugin;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

interface AxPluginXmlParser {
    public AxPluginXml parseAxPluginXml(InputStream in) throws SAXException, IOException,
            ParserConfigurationException, XPathExpressionException;
}
