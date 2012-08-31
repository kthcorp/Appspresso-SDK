package com.appspresso.cli.ant.ios.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IOSUtils {

	private static final String PATTERN_SDK_TYPE = "iphone([a-zA-Z]+).*";
	private static final String PATTERN_SDK_VERSION = "iphone[a-zA-Z]+([0-9.]+).*";

	public static String resolveSDKType(String activeSdk) {
		Pattern pattern = Pattern.compile(PATTERN_SDK_TYPE);
		Matcher matcher = pattern.matcher(activeSdk);

		String type = null;
		if (matcher.find()) {
			type = matcher.group(1);
		}

		return type;
	}

	public static String resolveSDKVersion(String activeSdk) {
		Pattern pattern = Pattern.compile(PATTERN_SDK_VERSION);
		Matcher matcher = pattern.matcher(activeSdk);

		String version = null;
		if (matcher.find()) {
			version = matcher.group(1);
		}
		return version;
	}

	public static String getUIInterfaceOrientation(int o) {
		switch (o) {
		case 1:
			return "UIInterfaceOrientationPortrait";
		case 2:
			return "UIInterfaceOrientationLandscapeRight";
		case 3:
			return "UIInterfaceOrientationPortraitUpsideDown";
		case 4:
			return "UIInterfaceOrientationLandscapeLeft";
		case 0:
		default:
			return null;
		}
	}
}
