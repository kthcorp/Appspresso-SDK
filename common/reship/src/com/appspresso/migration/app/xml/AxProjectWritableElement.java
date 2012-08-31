package com.appspresso.migration.app.xml;

import java.io.Writer;

public interface AxProjectWritableElement {
	public void write(WriteVisitor visitor, Writer writer);
}
