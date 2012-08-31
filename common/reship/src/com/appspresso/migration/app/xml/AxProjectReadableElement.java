package com.appspresso.migration.app.xml;

import org.w3c.dom.Node;

public interface AxProjectReadableElement {

	public void read(ParseVisitor visitor, Node node);

}
