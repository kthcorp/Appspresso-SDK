package com.appspresso.migration.app.v1;

public class Preference {
	private String name;
	private String value;

	public Preference(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}