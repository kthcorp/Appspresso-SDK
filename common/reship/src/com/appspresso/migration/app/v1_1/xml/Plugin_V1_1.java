package com.appspresso.migration.app.v1_1.xml;

import java.io.Writer;

import com.appspresso.migration.app.v1.Plugin_V1;
import com.appspresso.migration.app.xml.AxProjectMigratableElement;
import com.appspresso.migration.app.xml.AxProjectReadableElement;
import com.appspresso.migration.app.xml.AxProjectWritableElement;
import com.appspresso.migration.app.xml.MigrateVisitor;
import com.appspresso.migration.app.xml.WriteVisitor;

public class Plugin_V1_1 extends Plugin_V1 implements AxProjectMigratableElement, AxProjectWritableElement {

	@Override
	public void migrate(MigrateVisitor visitor, AxProjectReadableElement from) {
		visitor.visit(from, this);
	}

	@Override
	public void write(WriteVisitor visitor, Writer writer) {
		visitor.visit(this, writer);
	}

}
