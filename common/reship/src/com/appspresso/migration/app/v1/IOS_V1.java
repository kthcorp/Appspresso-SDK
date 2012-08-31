package com.appspresso.migration.app.v1;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.ParseVisitor;

public class IOS_V1 implements AxProjectReadableElement {

	protected String id;
	protected String signInfo;

	// private List<Preference> preferences;
	protected Map<String, String> preferences = null;

	public IOS_V1() {
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

	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
	}

	public String getId() {
		return id;
	}

	public String getSignInfo() {
		return signInfo;
	}

	public Map<String, String> getPreferences() {
		return preferences;
	}

}
