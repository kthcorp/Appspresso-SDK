package com.appspresso.migration.app.xml;

import java.io.Writer;

public interface WriteVisitor {

	public void visit(AxProjectWritableElement element, Writer writer);

}