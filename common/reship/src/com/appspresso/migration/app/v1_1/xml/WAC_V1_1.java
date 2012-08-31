package com.appspresso.migration.app.v1_1.xml;

import java.io.Writer;

import org.w3c.dom.Node;

import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.AxProjectWritableElement;
import com.appspresso.migration.app.xml.ParseVisitor;
import com.appspresso.migration.app.xml.WriteVisitor;

public class WAC_V1_1 implements AxProjectReadableElement, AxProjectWritableElement {
	private String minVersion = "2.0";

	@Override
	public void write(WriteVisitor visitor, Writer writer) {
		visitor.visit(this, writer);
	}

	@Override
	public void read(ParseVisitor visitor, Node node) {
		visitor.visit(this, node);
	}

	public String getMinVersion() {
		return this.minVersion;
	}
}
