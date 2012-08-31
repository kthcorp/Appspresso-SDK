package com.appspresso.migration.app.v1_1;

import java.io.File;

import com.appspresso.migration.Log;
import com.appspresso.migration.app.AxProject;
import com.appspresso.migration.app.v1.AxProjectXML_V1;
import com.appspresso.migration.app.v1.AxProject_V1;
import com.appspresso.migration.app.v1_1.xml.AxProjectXML_V1_1;
import com.appspresso.migration.app.v1_1.xml.MigrateVisitor_V1_1;
import com.appspresso.migration.exception.MigrationException;

public class AxProject_V1_1 extends AxProject {
	protected AxProjectXML_V1_1 projectXML = null;

	private String dir;

	@Override
	public void migrate(AxProject from) throws MigrationException {
		if (from instanceof AxProject_V1) {
			this.migrate((AxProject_V1) from);
		}
	}

	public void migrate(AxProject_V1 from) throws MigrationException {
		try {
			AxProjectXML_V1 fromXML = from.getAxProjectXML();

			if (Log.isInfoEnabled()) {
				Log.info("create new project.xml");
			}

			MigrateVisitor_V1_1 visitor = new MigrateVisitor_V1_1();
			projectXML = new AxProjectXML_V1_1();
			projectXML.migrate(visitor, fromXML);

			File newProjectXML = new File(from.getDir(), "/new.project.xml");
			projectXML.write(newProjectXML.getAbsolutePath());

			this.dir = from.getDir();

			Platforms_V1_1 platforms = Platforms_V1_1.create(this.dir);
			platforms.migrate(from);

			File projectXMLFile = new File(from.getDir(), "project.xml");
			if (!projectXMLFile.delete() || !newProjectXML.renameTo(projectXMLFile)) {
				throw new MigrationException("Failed to rename new.project.xml");
			}
		}
		catch (Exception e) {
			throw new MigrationException(e);
		}
	}

	public String getDir() {
		return this.dir;
	}
}
