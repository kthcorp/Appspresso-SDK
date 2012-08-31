package com.appspresso.cli.ant.ios.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FilterSet;


public class CopyPbxproj extends Task {

	private static final String INFO_PLIST_STRINGS = "InfoPlist.strings";

	private static final String ICON_PNG = "Icon.png";
	private static final String ICON_72_PNG = "Icon-72.png";
	private static final String ICON_2X_PNG = "Icon@2x.png";
	private static final String ICON_2X_72_PNG = "Icon@2x-72.png";

	private static final String DEFAULT_PNG = "Default.png";
	private static final String DEFAULT_2X_PNG = "Default@2x.png";
	private static final String DEFAULT_PORTRAIT_IPAD_PNG = "Default-Portrait~ipad.png";
	private static final String DEFAULT_LANDSCAPE_IPAD_PNG = "Default-Landscape~ipad.png";
	private static final String DEFAULT_PORTRAIT_2X_IPAD_PNG = "Default-Portrait@2x~ipad.png";
	private static final String DEFAULT_LANDSCAPE_2X_IPAD_PNG = "Default-Landscape@2x~ipad.png";

	private static Map<String, String> BUILDFILE_DATA;
	private static Map<String, String> BUILDFILE_REF_DATA;
	private static Map<String, LanguageData> LPROJ_FILE_REFDATA;

	private final static Map<String, String> FILE_TYPE = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			put(INFO_PLIST_STRINGS, "text.plist.strings");

			put(ICON_PNG, "image.png");
			put(ICON_72_PNG, "image.png");
			put(ICON_2X_PNG, "image.png");
			put(ICON_2X_72_PNG, "image.png");

			put(DEFAULT_PNG, "image.png");
			put(DEFAULT_2X_PNG, "image.png");
			put(DEFAULT_LANDSCAPE_IPAD_PNG, "image.png");
			put(DEFAULT_PORTRAIT_IPAD_PNG, "image.png");
			put(DEFAULT_LANDSCAPE_2X_IPAD_PNG, "image.png");
			put(DEFAULT_PORTRAIT_2X_IPAD_PNG, "image.png");
		}
	};

	private static Map<String, BundleInfo> bundleInfo = new HashMap<String, CopyPbxproj.BundleInfo>();

	private static class LanguageData {
		private Map<String, String> HASH_VALUE;

		void setFileRef(Map<String, String> fileRef) {
			this.HASH_VALUE = fileRef;
		}
	}

	private static class BundleInfo {
		List<String> files = new ArrayList<String>();
	}

	private static List<String> scanBundle(File appdir) {
		List<String> bundles = new ArrayList<String>();

		for (String child : appdir.list()) {
			if (child.endsWith(".lproj")) {
				String lang = child.substring(0, child.lastIndexOf(".lproj"));
				bundles.add(lang);

				System.out.println(child + " find bundle : " + lang);
				BundleInfo info = new BundleInfo();
				bundleInfo.put(lang, info);

				File bundle = new File(appdir, child);
				String[] list = bundle.list();
				if (list == null)
					continue;

				for (String bundleChild : list) {
					info.files.add(bundleChild);
				}
			}
		}
		return bundles;
	}

	private List<String> scanIcons(File appdir) {
		List<String> icons = new ArrayList<String>();

		for (String child : appdir.list()) {
			if (child.startsWith("Icon")) {
				System.out.println(child + " find icon file : " + child);
				icons.add(child);
			}
		}

		return icons;
	}

	private void initFilter(Project project, FilterSet filter) {

		// ////////////////////////////////////////////////////////////////////////////////////////
		// project.pbxproj
		project.log("make project.pbxproj");
		filter.addFilter("__APPLICATION_NAME__", getProjName());

		filter.addFilter("AX_IOS_LIB",
				"\"" + new File(project.getProperty("ax.ios.dir"), "lib").getAbsolutePath() + "\"");
		filter.addFilter("CODE_SIGN_IDENTITY", getCodeSign(project));
		filter.addFilter("HEADERS_PATH",
				"\"" + new File(project.getProperty("ax.ios.dir"), "headers").getAbsolutePath() + "\",");

		String targetedDeviceFamily = project.getProperty("ios.targetDevice");
		filter.addFilter("TARGETED_DEVICE_FAMILY", "TARGETED_DEVICE_FAMILY = \"" + targetedDeviceFamily + "\";");

		// ////////////////////////////////////////////////////////////////////////////////////////
		File appdir = new File(project.getProperty("app.runtime.project.dir"),
				project.getProperty("app.runtime.project.name"));
		List<String> bundles = scanBundle(appdir);
		List<String> icons = scanIcons(appdir);

		// PBX_PROJECT_SECTION/PROJECT_OBJ/KNOWNREGIONS
		KNOWNREGIONS(filter, bundles);

		// PBX_VARIANTGROUP_SECTION/INFOPLIST_STRINGS/children
		PBX_VARIANTGROUP_SECTION(filter, bundles, INFO_PLIST_STRINGS, "PBX_VARIANTGROUP_SECTION/INFOPLIST_STRINGS");
		PBX_VARIANTGROUP_SECTION(filter, bundles, DEFAULT_PNG, "PBX_VARIANTGROUP_SECTION/DEFAULT");
		PBX_VARIANTGROUP_SECTION(filter, bundles, DEFAULT_2X_PNG, "PBX_VARIANTGROUP_SECTION/DEFAULT_2X");
		PBX_VARIANTGROUP_SECTION(filter, bundles, DEFAULT_PORTRAIT_IPAD_PNG,
				"PBX_VARIANTGROUP_SECTION/DEFAULT_PORTRAIT_IPAD");
		PBX_VARIANTGROUP_SECTION(filter, bundles, DEFAULT_LANDSCAPE_IPAD_PNG,
				"PBX_VARIANTGROUP_SECTION/DEFAULT_LANDSCAPE_IPAD");
		PBX_VARIANTGROUP_SECTION(filter, bundles, DEFAULT_PORTRAIT_2X_IPAD_PNG,
				"PBX_VARIANTGROUP_SECTION/DEFAULT_PORTRAIT_2X_IPAD");
		PBX_VARIANTGROUP_SECTION(filter, bundles, DEFAULT_LANDSCAPE_2X_IPAD_PNG,
				"PBX_VARIANTGROUP_SECTION/DEFAULT_LANDSCAPE_2X_IPAD");

		// PBX_FILEREFERENCE_SECTION
		PBX_FILEREFERENCE_SECTION(filter, bundles, icons);

		// PBX_BUILDFILE_SECTION
		// PBX_GROUP_SECTION/__APPLICATION_NAME__
		// PBX_RESOURCE_BUILD_PHASE_SECTION
		PBX_BUILDFILE_SECTION(filter, bundles, icons);

	}

	private static void PBX_BUILDFILE_SECTION(FilterSet filter, List<String> bundles, List<String> icons) {
		final String BUILD_FILE_FORMAT = "%s /* %s in Resources */ = {isa = PBXBuildFile; fileRef = %s /* %s */; };";
		final String GROUP_FORMAT = "%s /* %s */,";
		final String RESOURCE_BUILD_FORMAT = "%s /* %s in Resources */,";

		String[] bundleFileList = new String[] { INFO_PLIST_STRINGS,
				DEFAULT_PNG, DEFAULT_2X_PNG, DEFAULT_PORTRAIT_IPAD_PNG, DEFAULT_LANDSCAPE_IPAD_PNG,
				DEFAULT_PORTRAIT_2X_IPAD_PNG, DEFAULT_LANDSCAPE_2X_IPAD_PNG };

		////////////////////////////////////////////////////////////////////////////////
		// Splash (bundle for language)
		StringBuilder buildfile = new StringBuilder();
		StringBuilder group = new StringBuilder();
		StringBuilder resource = new StringBuilder();
		for (String file : bundleFileList) {
			for (Entry<String, BundleInfo> entry : bundleInfo.entrySet()) {
				if (entry.getValue().files.contains(file)) {
					buildfile.append(
							String.format(BUILD_FILE_FORMAT, BUILDFILE_DATA.get(file), file,
									BUILDFILE_REF_DATA.get(file), file)).append("\n");
					group.append(String.format(GROUP_FORMAT, BUILDFILE_REF_DATA.get(file), file)).append("\n");
					resource.append(String.format(RESOURCE_BUILD_FORMAT, BUILDFILE_DATA.get(file), file)).append("\n");
					break;	// 각 파일별로 최소한 1개 존재하는지만 확인하면 되므로 break.
				}
			}
		}

		// ICON_PNG, ICON_72_PNG, ICON_2X_PNG, ICON_2X_72_PNG,
		String[] iconFileList = new String[] { ICON_PNG, ICON_72_PNG, ICON_2X_PNG, ICON_2X_72_PNG };
		for (String file : iconFileList) {
			if (icons.contains(file)) {
				buildfile.append(
						String.format(BUILD_FILE_FORMAT, BUILDFILE_DATA.get(file), file,
								BUILDFILE_REF_DATA.get(file), file)).append("\n");
				group.append(String.format(GROUP_FORMAT, BUILDFILE_REF_DATA.get(file), file)).append("\n");
				resource.append(String.format(RESOURCE_BUILD_FORMAT, BUILDFILE_DATA.get(file), file)).append("\n");
			}
		}

		filter.addFilter("PBX_BUILDFILE_SECTION", buildfile.toString());
		filter.addFilter("PBX_GROUP_SECTION/__APPLICATION_NAME__", group.toString());
		filter.addFilter("PBX_RESOURCE_BUILD_PHASE_SECTION", resource.toString());
	}

	private static String PBX_VARIANTGROUP_SECTION_LOCALE(List<String> bundles, String file) {
		StringBuilder builder = new StringBuilder();

		for (String lang : bundles) {
			List<String> files = bundleInfo.get(lang).files;
			if (files.contains(file)) {
				LanguageData data = LPROJ_FILE_REFDATA.get(lang);
				if (data == null) {
					continue;
				}
				builder.append(String.format("%s /* %s */,\n", data.HASH_VALUE.get(file), lang));
			}
		}

		return builder.toString();
	}

	private static void PBX_VARIANTGROUP_SECTION(FilterSet filter, List<String> bundles, String file, String token) {
		String v = PBX_VARIANTGROUP_SECTION_LOCALE(bundles, file);
		if ("".equals(v.trim())) {
			filter.addFilter(token, "");
			return;
		}

		StringBuilder builder = new StringBuilder();
		String hash = BUILDFILE_REF_DATA.get(file);
		builder.append(hash).append(" /* ").append(file).append(" */ = {\n").append("isa = PBXVariantGroup;\n")
				.append("children = (\n").append(v).append("\n").append(");\n").append("name = \"").append(file)
				.append("\";\n").append("sourceTree = \"<group>\";\n").append("};\n");

		filter.addFilter(token, builder.toString());
	}

	private static void PBX_FILEREFERENCE_SECTION(FilterSet filter, List<String> bundles, List<String> icons) {
		final String BUNDLE_FORMAT = "%s /* %s */ = {isa = PBXFileReference; lastKnownFileType = %s; name = %s; path = \"%s.lproj/%s\"; sourceTree = \"<group>\"; };";
		StringBuilder builder = new StringBuilder();

		for (String lang : bundles) {
			BundleInfo info = bundleInfo.get(lang);
			for (String file : info.files) {
				LanguageData data = LPROJ_FILE_REFDATA.get(lang);
				if (data == null) {
					continue;
				}

				String type = FILE_TYPE.get(file);
				String value = data.HASH_VALUE.get(file);
				if (type != null) {
					builder.append(String.format(BUNDLE_FORMAT, value, lang, type, lang, lang, file)).append("\n");
				}
			}
		}

		final String ICON_FORMAT = "%s /* %s */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = \"%s\"; sourceTree = \"<group>\"; };";
		for (String icon : icons) {
			builder.append(String.format(ICON_FORMAT, BUILDFILE_REF_DATA.get(icon), icon, icon)).append("\n");
		}

		filter.addFilter("PBX_FILEREFERENCE_SECTION", builder.toString());
	}

	private static void KNOWNREGIONS(FilterSet filter, List<String> bundles) {
		StringBuilder builder = new StringBuilder();
		for (String lang : bundles) {
			builder.append("\"" + lang + "\",\n");
		}
		filter.addFilter("PBX_PROJECT_SECTION/PROJECT_OBJ/KNOWNREGIONS", builder.toString());
	}

	private void initData() {
		BUILDFILE_DATA = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put(INFO_PLIST_STRINGS, "63517821151ABC0C00ABEBCB");

				put(ICON_PNG, "635178A3151BFF9500ABEBCB");
				put(ICON_72_PNG, "635178A2151BFF9500ABEBCB");
				put(ICON_2X_PNG, "635178A4151BFF9500ABEBCB");
				put(ICON_2X_72_PNG, "635178B6151C050C00ABEBCB");

				put(DEFAULT_PNG, "6385F6181521AE80009D90F6");
				put(DEFAULT_2X_PNG, "6385F61F1521AE8A009D90F6");
				put(DEFAULT_PORTRAIT_IPAD_PNG, "6385F6111521AE77009D90F6");
				put(DEFAULT_LANDSCAPE_IPAD_PNG, "6385F60A1521AE6F009D90F6");
				put(DEFAULT_PORTRAIT_2X_IPAD_PNG, "6385F6031521AE65009D90F6");
				put(DEFAULT_LANDSCAPE_2X_IPAD_PNG, "6385F5FC1521AE43009D90F6");
			}
		};

		BUILDFILE_REF_DATA = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put(INFO_PLIST_STRINGS, "6351781F151ABC0C00ABEBCB");

				put(ICON_PNG, "6351789E151BFF9500ABEBCB");
				put(ICON_72_PNG, "6351789D151BFF9500ABEBCB");
				put(ICON_2X_PNG, "6351789F151BFF9500ABEBCB");
				put(ICON_2X_72_PNG, "635178B5151C050C00ABEBCB");

				put(DEFAULT_PNG, "6385F61A1521AE80009D90F6");
				put(DEFAULT_2X_PNG, "6385F6211521AE8A009D90F6");
				put(DEFAULT_PORTRAIT_IPAD_PNG, "6385F6131521AE77009D90F6");
				put(DEFAULT_LANDSCAPE_IPAD_PNG, "6385F60C1521AE6F009D90F6");
				put(DEFAULT_PORTRAIT_2X_IPAD_PNG, "6385F6051521AE65009D90F6");
				put(DEFAULT_LANDSCAPE_2X_IPAD_PNG, "6385F5FE1521AE43009D90F6");
			}
		};

		LPROJ_FILE_REFDATA = new HashMap<String, CopyPbxproj.LanguageData>();
		LPROJ_FILE_REFDATA.put("en", new LanguageData() {
			{
				setFileRef(new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;
					{
						put(INFO_PLIST_STRINGS, "63517820151ABC0C00ABEBCB");
						put(DEFAULT_PNG, "6385F6191521AE80009D90F6");
						put(DEFAULT_2X_PNG, "6385F6201521AE8A009D90F6");
						put(DEFAULT_PORTRAIT_IPAD_PNG, "6385F6121521AE77009D90F6");
						put(DEFAULT_LANDSCAPE_IPAD_PNG, "6385F60B1521AE6F009D90F6");
						put(DEFAULT_PORTRAIT_2X_IPAD_PNG, "6385F6041521AE65009D90F6");
						put(DEFAULT_LANDSCAPE_2X_IPAD_PNG, "6385F5FD1521AE43009D90F6");
					}
				});
			}
		});

		LPROJ_FILE_REFDATA.put("ko", new LanguageData() {
			{
				setFileRef(new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;
					{
						put(INFO_PLIST_STRINGS, "6385F5F81521ADED009D90F6");
						put(DEFAULT_PNG, "6385F61B1521AE87009D90F6");
						put(DEFAULT_2X_PNG, "6385F6221521AE8F009D90F6");
						put(DEFAULT_PORTRAIT_IPAD_PNG, "6385F6141521AE7D009D90F6");
						put(DEFAULT_LANDSCAPE_IPAD_PNG, "6385F60D1521AE74009D90F6");
						put(DEFAULT_PORTRAIT_2X_IPAD_PNG, "6385F6061521AE6B009D90F6");
						put(DEFAULT_LANDSCAPE_2X_IPAD_PNG, "6385F5FF1521AE49009D90F6");
					}
				});
			}
		});

		LPROJ_FILE_REFDATA.put("ja", new LanguageData() {
			{
				setFileRef(new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;
					{
						put(INFO_PLIST_STRINGS, "6385F5F91521ADF0009D90F6");
						put(DEFAULT_PNG, "6385F61C1521AE87009D90F6");
						put(DEFAULT_2X_PNG, "6385F6231521AE8F009D90F6");
						put(DEFAULT_PORTRAIT_IPAD_PNG, "6385F6151521AE7D009D90F6");
						put(DEFAULT_LANDSCAPE_IPAD_PNG, "6385F60E1521AE74009D90F6");
						put(DEFAULT_PORTRAIT_2X_IPAD_PNG, "6385F6071521AE6B009D90F6");
						put(DEFAULT_LANDSCAPE_2X_IPAD_PNG, "6385F6001521AE4B009D90F6");
					}
				});
			}
		});

		LPROJ_FILE_REFDATA.put("es", new LanguageData() {
			{
				setFileRef(new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;
					{
						put(INFO_PLIST_STRINGS, "6385F5FA1521ADFE009D90F6");
						put(DEFAULT_PNG, "6385F61D1521AE87009D90F6");
						put(DEFAULT_2X_PNG, "6385F6241521AE8F009D90F6");
						put(DEFAULT_PORTRAIT_IPAD_PNG, "6385F6161521AE7D009D90F6");
						put(DEFAULT_LANDSCAPE_IPAD_PNG, "6385F60F1521AE74009D90F6");
						put(DEFAULT_PORTRAIT_2X_IPAD_PNG, "6385F6081521AE6B009D90F6");
						put(DEFAULT_LANDSCAPE_2X_IPAD_PNG, "6385F6011521AE4D009D90F6");
					}
				});
			}
		});

		LPROJ_FILE_REFDATA.put("zh-Hans", new LanguageData() {
			{
				setFileRef(new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;
					{
						put(INFO_PLIST_STRINGS, "6385F5FB1521AE39009D90F6");
						put(DEFAULT_PNG, "6385F61E1521AE87009D90F6");
						put(DEFAULT_2X_PNG, "6385F6251521AE8F009D90F6");
						put(DEFAULT_PORTRAIT_IPAD_PNG, "6385F6171521AE7D009D90F6");
						put(DEFAULT_LANDSCAPE_IPAD_PNG, "6385F6101521AE74009D90F6");
						put(DEFAULT_PORTRAIT_2X_IPAD_PNG, "6385F6091521AE6B009D90F6");
						put(DEFAULT_LANDSCAPE_2X_IPAD_PNG, "6385F6021521AE50009D90F6");
					}
				});
			}
		});
	}

	protected static String getCodeSign(Project project) {
		StringBuilder codeSign = new StringBuilder();

		String activeSDK = project.getProperty("ios.sdk.type");

		String type = IOSUtils.resolveSDKType(activeSDK);
		project.log("ios sdk, type : " + activeSDK + ", " + type);
		// if ("os".equals(type)) { XXX : when app-export, not set TARGET_DEVICE...
		// TODO check available codeSign
			codeSign.append(String.format("\"CODE_SIGN_IDENTITY[sdk=iphone%s*]\" = ", type == null ? "os" : type));
			codeSign.append("\"");
			codeSign.append(project.getProperty("ios.codeSign"));
			codeSign.append("\";");
		// }

		return codeSign.toString();
	}

	private String getProjName() {
		return getProject().getProperty("app.runtime.project.name");
	}

	@Override
	public void execute() throws BuildException {
		Copy copy = (Copy) getProject().createTask("copy");

		initData();

		FilterSet filter = copy.createFilterSet();
		initFilter(getProject(), filter);

		copy.setOverwrite(true);
		copy.setTodir(new File(getProject().getProperty("app.runtime.project.dir"), getProjName() + ".xcodeproj"));
		copy.setFile(new File(getProject().getProperty("ax.ios.templates.app.dir"), "base/project.pbxproj"));
		copy.perform();
	}

}
