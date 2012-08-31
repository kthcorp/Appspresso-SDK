package com.appspresso.migration;

import java.io.File;

import com.appspresso.migration.app.AppProjectMigrator;
import com.appspresso.migration.exception.MigrationException;

public class Main {
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				printHelp();
				return;
			}
			String type = args[0];
			String path = args[1];

			if ("app".equals(type)) {
				AppProjectMigrator migrator = new AppProjectMigrator();
				migrator.migrate(new File(path));
			}
			else {
				printHelp();
			}
		}
		catch (MigrationException e) {
			e.printStackTrace(System.err);
			System.err.println(e.getMessage());
		}
	}

	private static void printHelp() {
		System.out.println("Appsrpesso Migration Tool");
		System.out.println("Usage : java -jar migartion.jar [type] [path]");
		System.out.println("  type  project type. 'app' or 'axp'");
		System.out.println("  path  project path.");
	}
}
