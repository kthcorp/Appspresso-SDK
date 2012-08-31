package com.appspresso.cli.ant.ios.app;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


public class AxConfigGen extends Task {
	private final static String AX_CONFIG_FILENAME = "appspresso_config.plist";

	private String dir;

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws BuildException {
		generate(getProject().getProperties(), new File(getDir(), AX_CONFIG_FILENAME));
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getDir() {
		if (this.dir == null) {
			return getProject().getProperty("app.runtime.project.dir");
		}
		return this.dir;
	}

	private static void generate(Hashtable<String, String> parameters, File path) {
		PrintWriter writer = null;

		try {
			Map<String, String> iOSParameterMap = filteringPropertiesAboutiOS(parameters);
			iOSParameterMap.putAll(getSupportedOrientaion(parameters));

			writer = new PrintWriter(path);

			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<!DOCTYPE plist PUBLIC \"-//Apple/DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
			writer.println("<plist version=1.0>");
			writer.println("<dict>");

			Iterator<Entry<String, String>> it = iOSParameterMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				writer.println("<key>" + entry.getKey() + "</key>");
				writer.println("<string>" + entry.getValue() + "</string>");
			}
			writer.println("</dict>");
			writer.println("</plist>");
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

	private static Map<String, String> getSupportedOrientaion(Hashtable<String, String> parameters) {
		Map<String, String> result = new HashMap<String, String>();
		String allValues = parameters.get("ios.application.orientation.supported");
		if (allValues == null) {
			return result;
		}

		for (String value : allValues.split(",")) {
			String v = value.trim();
			String orientation = IOSUtils.getUIInterfaceOrientation(Integer.parseInt(v));
			if (orientation != null) {
				result.put(orientation, "TRUE");
			}
		}
		return result;
	}

	private final static String IOS_CONFIG_PREFIX = "ios.config.";

	private final static List<String> map = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("ios.EmulateOrientation");
			add("ios.UIInterfaceOrientationLandscapeLeft");
			add("ios.UIInterfaceOrientationLandscapeRight");
			add("ios.UIInterfaceOrientationPortrait");
			add("ios.UIInterfaceOrientationPortraitUpsideDown");
			add("ios.ScalesPageToFit");
			add("ios.webview.cache.enable");

			add("ios.splash.duration.min");
			add("ios.splash.duration.max");
		}
	};

	private static Map<String, String> filteringPropertiesAboutiOS(Hashtable<String, String> parameters) {
		int prefixLength = IOS_CONFIG_PREFIX.length();
		Map<String, String> result = new HashMap<String, String>();

		Iterator<Entry<String, String>> entries = parameters.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, String> entry = entries.next();
			String key = entry.getKey();
			if (key.startsWith(IOS_CONFIG_PREFIX)) {
				String value = entry.getValue();
				if (value != null && !"".equals(value.trim())) {
					result.put(key.substring(prefixLength, key.length()), value);
				}
			}
			else if (map.contains(key)) {
				// XXX rudder DO NOT append '.config.' to name of preferences element in project.xml
				// see also com.appspresso.packaging.build.AndroidAntAppPackager.ParameterUtil
				String value = entry.getValue();
				result.put(key.substring(4, key.length()), value);
			}
		}

		return result;
	}

}
