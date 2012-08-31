package com.appspresso.migration.app.xml;

import org.w3c.dom.Node;

public interface ParseVisitor {

	public void visit(AxProjectReadableElement element, Node node);

}