package com.appspresso.cli.ant.android.app;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AxConfigGen extends Task {
	private final static String AX_CONFIG_FILENAME = "appspresso-config.properties";

	private String dir;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void execute() throws BuildException {
		Hashtable properties = getProject().getProperties();
		convertKey(properties);
		generate(properties, new File(getDir(), AX_CONFIG_FILENAME));
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getDir() {
		if (this.dir == null) {
			return getProject().getProperty("app.runtime.project.dir") + "/src";
		}
		return this.dir;
	}

	private final static Map<String, String> BetaParameterMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 4504694205265094429L;
		{
			put("android.icon_high", "android.icon.hdpi");
			put("android.icon_middle", "android.icon.mdpi");
			put("android.icon_low", "android.icon.ldpi");

			// AxConfig
			put("android.defaultZoom", "android.config.webview.zoom.default");
			put("android.zoom.enable", "android.config.webview.zoom.support");
			put("android.zoom.control", "android.config.webview.zoom.control");
			put("android.webview.cache.enable", "android.config.webview.cache.enable");
			put("android.webview.cache.clearonfinish", "android.config.webview.cache.clearonfinish");

			put("android.splash.enable", "android.config.splash.enable");
			put("android.splash.orientation", "android.config.splash.orientation");
			put("android.splash.duration.min", "android.config.splash.duration.min");
			put("android.splash.duration.max", "android.config.splash.duration.max");
		}
	};

	private void convertKey(Hashtable<String, String> parameters) {
		Iterator<Entry<String, String>> compatibleMap = BetaParameterMap.entrySet().iterator();
		while (compatibleMap.hasNext()) {
			Entry<String, String> entry = compatibleMap.next();
			String oldKey = entry.getKey();

			String buildValue = null;
			if ((buildValue = parameters.get(oldKey)) != null) {
				parameters.put(entry.getValue(), buildValue);
				parameters.remove(oldKey);
			}
		}
	}

	private void generate(Hashtable<String, String> parameters, File axconfig) {
		PrintWriter writer = null;

		Properties properties = new Properties();
		properties.putAll(filteringPropertiesAboutAndroid(parameters));

		try {
			writer = new PrintWriter(axconfig);
			properties.store(writer, null);
		}
		catch (Exception e) {
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
				}
				catch (Exception ignored) {
				}
			}
		}
	}

	private final static String ANDROID_CONFIG_PREFIX = "android.config.";

	private Map<String, String> filteringPropertiesAboutAndroid(Hashtable<String, String> parameters) {
		int prefixLength = ANDROID_CONFIG_PREFIX.length();
		Map<String, String> result = new HashMap<String, String>();

		Iterator<Entry<String, String>> entries = parameters.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, String> entry = entries.next();
			String key = entry.getKey();
			if (key.startsWith(ANDROID_CONFIG_PREFIX)) {
				String value = entry.getValue();
				if (value != null && !"".equals(value.trim())) {
					result.put(key.substring(prefixLength, key.length()), value);
				}
			}
		}

		return result;
	}

}
