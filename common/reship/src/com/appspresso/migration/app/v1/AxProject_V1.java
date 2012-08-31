package com.appspresso.migration.app.v1;

import java.io.File;

import org.w3c.dom.Node;

import com.appspresso.migration.app.AxProject;

public class AxProject_V1 extends AxProject {
	private AxProjectXML_V1 projectXML = null;
	private String dir;

	public AxProject_V1() {
	}

	@Override
	public void migrate(AxProject from) {

	}

	public AxProjectXML_V1 getAxProjectXML() {
		return this.projectXML;
	}

	public String getDir() {
		return this.dir;
	}

	public static AxProject create(File projectDir, Node projectElement) {
		AxProject_V1 project = new AxProject_V1();

		project.dir = projectDir.getAbsolutePath();

		ParseVisitor_V1 visitor = new ParseVisitor_V1();
		project.projectXML = new AxProjectXML_V1();
		((AxProjectXML_V1) project.projectXML).read(visitor, projectElement);

		return project;
	}

}
