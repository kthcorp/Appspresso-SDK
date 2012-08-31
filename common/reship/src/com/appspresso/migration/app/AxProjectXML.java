package com.appspresso.migration.app;

import com.appspresso.migration.app.xml.AxProjectReadableElement;

public abstract class AxProjectXML implements AxProjectReadableElement {

	public abstract String getAxAppVersion();

	public abstract void write(String path);

}
