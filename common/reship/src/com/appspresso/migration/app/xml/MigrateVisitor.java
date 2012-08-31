package com.appspresso.migration.app.xml;

public interface MigrateVisitor {

	public void visit(AxProjectReadableElement from, AxProjectMigratableElement to);

}