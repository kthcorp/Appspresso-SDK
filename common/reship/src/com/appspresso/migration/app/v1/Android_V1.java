package com.appspresso.migration.app.v1;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.ParseVisitor;

public class Android_V1 implements AxProjectReadableElement {

	private String id;
	protected Map<String, String> preferences = null;

	public Android_V1() {
		preferences = new HashMap<String, String>();
	}

	@Override
	public void read(ParseVisitor visitor, Node node) {
		visitor.visit(this, node);
	}

	// ///////////////////////////////////////////////////////////////////
	// get / set
	public void setId(String id) {
		this.id = id;
	}

	public void setPreference(String name, String value) {
		this.preferences.put(name, value);
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getPreferences() {
		return preferences;
	}

}
