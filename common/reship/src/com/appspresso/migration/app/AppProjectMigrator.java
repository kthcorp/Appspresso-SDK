package com.appspresso.migration.app;

import java.io.File;

import com.appspresso.migration.Log;
import com.appspresso.migration.exception.InvalidProjectException;
import com.appspresso.migration.exception.MigrationException;

public class AppProjectMigrator {
	public void migrate(File targetProject) throws MigrationException {
		if (!targetProject.exists()) {
			throw new InvalidProjectException("Project does not exist");
		}

		File projectXml = new File(targetProject, "project.xml");
		if (!projectXml.exists()) {
			throw new InvalidProjectException("Project does not exist");
		}

		AxProject fromAxProject = AxProject.createAxProject(targetProject);
		if (fromAxProject == null) {
			return;
		}

		if (Log.isInfoEnabled()) {
			Log.info("migrate appspresso project");
		}

		AxProject toAxProject = AxProject.createLastestAxProject();
		toAxProject.migrate(fromAxProject);

		if (Log.isInfoEnabled()) {
			Log.info("complete migration");
		}
	}
}
