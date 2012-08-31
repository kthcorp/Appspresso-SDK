package com.appspresso.migration.app.xml;

public interface AxProjectMigratableElement {
	public void migrate(MigrateVisitor visitor, AxProjectReadableElement from);
}
