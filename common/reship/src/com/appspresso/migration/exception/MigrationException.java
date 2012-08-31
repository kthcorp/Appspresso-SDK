package com.appspresso.migration.exception;

public class MigrationException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -5099417820733676369L;

	public MigrationException(Exception e) {
		super(e);
	}

	public MigrationException(String message) {
		super(message);
	}
}
