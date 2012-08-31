package com.appspresso.migration;

public class Log {
	private final static int LOG_LEVEL = Integer.parseInt(System.getProperty("ax.log.level", "1"));

	private static final int INFO = 1;
	private static final int DEBUG = 0;

	private static void print(String message) {
		System.out.println(message);
	}

	public static boolean isInfoEnabled() {
		return LOG_LEVEL <= INFO;
	}

	public static boolean isDebugEnabled() {
		return LOG_LEVEL <= DEBUG;
	}

	public static void info(String message) {
		if (isInfoEnabled()) {
			print(message);
		}
	}

	public static void debug(String message) {
		if (isDebugEnabled()) {
			print(message);
		}
	}
}
