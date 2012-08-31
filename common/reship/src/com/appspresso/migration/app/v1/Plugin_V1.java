package com.appspresso.migration.app.v1;

import org.w3c.dom.Node;

import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.ParseVisitor;

public class Plugin_V1 implements AxProjectReadableElement {
	protected String id;
	protected String version;
	protected String location;

	public Plugin_V1() {
	}

	public Plugin_V1(String id, String version) {
		this.id = id;
		this.version = version;
	}

	public Plugin_V1(String location) {
		this.location = location;
	}

	public Plugin_V1(String id, String version, String location) {
		this.id = id;
		this.version = version;
		this.location = location;
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public String getLocation() {
		return location;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public void read(ParseVisitor visitor, Node node) {
	}

}
