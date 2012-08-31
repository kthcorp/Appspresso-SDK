package com.appspresso.migration.app;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.appspresso.migration.app.v1.AxProject_V1;
import com.appspresso.migration.app.v1_1.AxProject_V1_1;
import com.appspresso.migration.exception.MigrationException;

public abstract class AxProject {

	public final static String ATTR_AX_PROJECT_VERSION = "version";

	private final static String LASTEST_AX_PROJECT_VERSION = "1.1";

	public static AxProject createAxProject(File projectDir) {
		File projectXML = new File(projectDir, "project.xml");
		AxProject project = null;

		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document document = builder.parse(projectXML);
			Node projectElement = document.getChildNodes().item(0);
			Node projectVerAttr = projectElement.getAttributes().getNamedItem(ATTR_AX_PROJECT_VERSION);
			Node ns = projectElement.getAttributes().getNamedItem("xmlns");

			if (projectVerAttr == null || ns == null) {
				project = AxProject_V1.create(projectDir, projectElement);
			}
			else {
				String projectVer = projectVerAttr.getTextContent();
				if (LASTEST_AX_PROJECT_VERSION.equals(projectVer)) {
					return null;
				}
			}
		}
		catch (IOException e) {
		}
		catch (ParserConfigurationException e) {
		}
		catch (SAXException e) {
		}

		return project;
	}

	private static AxProject createAxProject(String version) {
		if ("1.1".equals(version)) {
			return new AxProject_V1_1();
		}
		else if ("1.0".equals(version) || "1.01".equals(version)) {
			return new AxProject_V1();
		}

		return new AxProject_V1();
	}

	public static AxProject createLastestAxProject() {
		return createAxProject(LASTEST_AX_PROJECT_VERSION);
	}

	public abstract void migrate(AxProject from) throws MigrationException;

}
