package com.appspresso.cli.ant.android.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FilterSet;

public class PrepareFilterSet extends Task {
	private static FilterSet filterset = null;

	public final static String BUILD_API_VERSION = "15";
	private String property;

	@Override
	public void execute() throws BuildException {
		try {
			prepare(getProject());

			getProject().addReference(this.property, getFilterSet());
		}
		catch (Exception e) {
			new BuildException(e);
		}
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public static FilterSet getFilterSet() {
		return PrepareFilterSet.filterset;
	}

	public static void prepare(Project project) throws Exception {
		if (filterset != null) {
			return;
		}

		filterset = (FilterSet) project.createDataType("filterset");

		filterset.addFilter("ANDROID_SDK_DIR", getProperty(project, "android.sdk.dir").replaceAll("\\\\", "/")); // path
		filterset.addFilter("PACKAGE", getProperty(project, "android.app.id"));
		filterset.addFilter("ACTIVITY", getProperty(project, "android.activity"));
		filterset.addFilter("BUILD_API_VERSION", getProperty(project, "android.target", BUILD_API_VERSION));

		// AndroidManifest
		filterset.addFilter("VERSION_NAME", getProperty(project, "android.app.version", "1.0.0"));

		filterset.addFilter("USES_SDK", getUsesSdk(project));

		filterset.addFilter("INSTALL_LOCATION", getProperty(project, "android.installLocation", "auto"));

		filterset.addFilter("ACTIVITY_THEME", getActivityTheme(getProperty(project, "android.activity.theme", "0")));

		filterset.addFilter("ACTIVITY_ORIENTATION",
				gerScreenOrientation(getProperty(project, "android.orientation", "0")));
		filterset.addFilter("VERSION_CODE", getProperty(project, "android.versionCode", "1"));

		filterset.addFilter("USES_PERMISSION", getUsesPermission(project));

		StringBuilder libraries = new StringBuilder("");
		try {
			int length = AndroidBuildUtils.getPluginLength(project);
			String runtimeProjectDir = getProperty(project, "app.runtime.project.dir");
			int index = 1;
			for (int i = 0; i < length; i++) {
				String pluginPath = AndroidBuildUtils.getPluginPath(project, i);
				File android = null;
				if (pluginPath != null && (android = new File(pluginPath, "lib/android")).exists()) {
					String relativePluginPath = findRelativePath(runtimeProjectDir, android.getAbsolutePath());
					libraries.append("android.library.reference.").append(index++).append("=").append(relativePluginPath)
							.append("\n");
				}
			}
		}
		catch (IOException e) {
			// e.printStackTrace();
		}
		filterset.addFilter("ANDROID_LIBRARY_PATH", libraries.toString());
	}

	private static String getUsesSdk(Project project) {
		StringBuilder builder = new StringBuilder();

		builder.append("<uses-sdk ");
		builder.append("android:minSdkVersion=\"").append(getProperty(project, "android.minSdkVersion", "7"))
				.append("\" ");
		builder.append("android:targetSdkVersion=\"").append(getProperty(project, "android.targetSdkVersion", "10"))
				.append("\" ");

		String maxSdkVersion = getProperty(project, "android.maxSdkVersion");
		if (maxSdkVersion != null && !"".equals(maxSdkVersion)) {
			builder.append("android:maxSdkVersion=\"").append(maxSdkVersion).append("\" ");
		}
		builder.append("/>");

		return builder.toString();
	}

	private static String getActivityTheme(String value) {
		int v = Integer.parseInt(value);

		switch (v) {
		case 1:
			return "@android:style/Theme";
		case 2:
			return "@android:style/Theme.NoTitleBar.Fullscreen";
		case 0:
		default:
			return "@android:style/Theme.NoTitleBar";
		}
	}

	private static String gerScreenOrientation(String value) {
		int o = Integer.parseInt(value);
		switch (o) {
		case 1:
			return "portrait";
		case 2:
			return "landscape";
		case 3:
			return "reversePortait";
		case 4:
			return "reverseLandscape";
		case 0:
		default:
			return "unspecified";
		}
	}

	protected static String getProperty(Project project, String key) {
		try {
			return (String) project.getProperties().get(key);
		}
		catch (Exception e) {
			return null;
		}
	}

	protected static String getProperty(Project project, String key, String defaultValue) {
		Object value = getProperty(project, key);
		return (value == null || "".equals(value) ? defaultValue : value.toString());
	}

	private static String getUsesPermission(Project project) throws Exception {
		StringBuilder builder = new StringBuilder("");
		try {
			int length = AndroidBuildUtils.getPluginLength(project);
			for (int i = 0; i < length; i++) {
				String[] permissions = AndroidBuildUtils.getPropertyValueWithName(project, i, "permission");
				if (permissions == null) {
					continue;
				}

				for (String permission : permissions) {
					String[] values = permission.split(",");
					for (String value : values) {
						if (permission != null && !"".equals(permission.trim())) {
							builder.append("<uses-permission android:name=\"" + value.trim() + "\"/>\n");
						}
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return builder.toString();
	}

	private static String findRelativePath(String base, String path) throws IOException {
		String a = new File(base).getCanonicalFile().toURI().getPath();
		String b = new File(path).getCanonicalFile().toURI().getPath();
		String[] basePaths = a.split("/");
		String[] otherPaths = b.split("/");

		int n = 0;
		for (; n < basePaths.length && n < otherPaths.length; n++) {
			if (basePaths[n].equals(otherPaths[n]) == false)
				break;
		}

		StringBuffer tmp = new StringBuffer("../");
		for (int m = n; m < basePaths.length - 1; m++)
			tmp.append("../");
		for (int m = n; m < otherPaths.length; m++) {
			tmp.append(otherPaths[m]);
			tmp.append("/");
		}

		return tmp.toString();
	}

}
